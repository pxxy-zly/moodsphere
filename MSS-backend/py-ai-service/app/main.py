from __future__ import annotations

import json
import logging
import os
import time
import uuid
from typing import Iterable

from dotenv import load_dotenv
from fastapi import Depends, FastAPI, Header, HTTPException
from openai import OpenAI

from .schemas import AnalyzeRequest, AnalyzeResponse

load_dotenv()

app = FastAPI(title="MoodSphere Python AI Service", version="0.1.0")
logger = logging.getLogger(__name__)

AI_API_TOKEN = os.getenv("AI_API_TOKEN", "").strip()
AI_USE_BAILIAN = os.getenv("AI_USE_BAILIAN", "true").strip().lower() == "true"
AI_FALLBACK_TO_RULE = os.getenv("AI_FALLBACK_TO_RULE", "true").strip().lower() == "true"
AI_MODEL_NAME = os.getenv("AI_MODEL_NAME", "qwen-plus")
AI_MODEL_VERSION = os.getenv("AI_MODEL_VERSION", "latest")
AI_PROMPT_VERSION = os.getenv("AI_PROMPT_VERSION", "p2-bailian-json-weather-summary")
DASHSCOPE_API_KEY = os.getenv("DASHSCOPE_API_KEY", "").strip()
DASHSCOPE_BASE_URL = os.getenv("DASHSCOPE_BASE_URL", "https://dashscope.aliyuncs.com/compatible-mode/v1").strip()

_client: OpenAI | None = None


def get_client() -> OpenAI:
    global _client
    if _client is not None:
        return _client
    if not DASHSCOPE_API_KEY:
        raise RuntimeError("DASHSCOPE_API_KEY is empty")
    _client = OpenAI(
        api_key=DASHSCOPE_API_KEY,
        base_url=DASHSCOPE_BASE_URL,
    )
    return _client

def verify_token(authorization: str | None = Header(default=None)) -> None:
    if not AI_API_TOKEN:
        return
    if not authorization or not authorization.startswith("Bearer "):
        raise HTTPException(status_code=401, detail="Missing bearer token")
    token = authorization.removeprefix("Bearer ").strip()
    if token != AI_API_TOKEN:
        raise HTTPException(status_code=401, detail="Invalid token")


def contains_any(text: str, words: Iterable[str]) -> bool:
    return any(word in text for word in words)


def detect_emotion(text: str) -> tuple[str, str, int, str]:
    if contains_any(text, ["suicide", "kill myself", "self harm", "自杀", "想死", "轻生", "伤害自己"]):
        return ("sad", "anxious", 3, "检测到高风险表达")
    if contains_any(text, ["anxious", "panic", "nervous", "worry", "焦虑", "紧张", "担心", "恐慌"]):
        return ("anxious", "tired", 1, "检测到焦虑倾向")
    if contains_any(text, ["sad", "depressed", "down", "难过", "低落", "抑郁", "伤心"]):
        return ("sad", "lonely", 1, "检测到低落倾向")
    if contains_any(text, ["angry", "mad", "irritable", "生气", "烦躁", "愤怒"]):
        return ("irritable", "anxious", 1, "检测到烦躁倾向")
    if contains_any(text, ["happy", "great", "good", "开心", "愉快", "高兴"]):
        return ("happy", "calm", 0, "积极情绪")
    return ("calm", "confused", 0, "情绪整体平稳")


def detect_scene(text: str) -> str:
    if contains_any(text, ["work", "office", "meeting", "工作", "公司", "开会"]):
        return "work"
    if contains_any(text, ["study", "school", "exam", "学习", "学校", "考试"]):
        return "study"
    if contains_any(text, ["family", "parent", "home", "家庭", "父母", "家里"]):
        return "family"
    if contains_any(text, ["love", "relationship", "感情", "恋爱"]):
        return "love"
    if contains_any(text, ["sleep", "insomnia", "睡眠", "失眠"]):
        return "sleep"
    if contains_any(text, ["health", "sick", "illness", "健康", "生病"]):
        return "health"
    return "general"


def extract_keywords(text: str, limit: int = 5) -> list[str]:
    normalized = (
        text.replace("\r", " ")
        .replace("\n", " ")
        .replace("\t", " ")
        .replace("，", " ")
        .replace("。", " ")
        .replace("！", " ")
        .replace("？", " ")
        .replace("；", " ")
        .replace("：", " ")
        .replace("、", " ")
        .replace(",", " ")
        .replace("!", " ")
        .replace("?", " ")
        .replace(";", " ")
        .replace(":", " ")
    )
    result: list[str] = []
    seen: set[str] = set()
    for item in normalized.split():
        word = item.strip()
        if len(word) <= 1 or word in seen:
            continue
        seen.add(word)
        result.append(word[:12])
        if len(result) >= limit:
            break
    return result or [text[:12]]


def build_scores(primary: str, secondary: str, intensity: int | None) -> dict[str, float]:
    intensity = min(10, max(1, intensity or 5))
    rate = intensity / 10.0
    primary_score = round(0.58 + rate * 0.34, 4)
    secondary_score = round(0.36 + rate * 0.24, 4)
    baseline = round(0.08 + (1 - rate) * 0.10, 4)
    scores = {
        "happy": baseline,
        "anxious": baseline,
        "tired": baseline,
        "calm": baseline,
        "wronged": baseline,
        "expect": baseline,
        "lonely": baseline,
        "irritable": baseline,
        "sad": baseline,
        "warm": baseline,
        "confused": baseline,
        "hopeful": baseline,
    }
    scores[primary] = primary_score
    scores[secondary] = secondary_score
    return scores


def normalize_scores(scores: dict[str, float] | None, primary: str, secondary: str, intensity: int | None) -> dict[str, float]:
    if not scores:
        return build_scores(primary, secondary, intensity)
    normalized: dict[str, float] = {}
    for key, value in scores.items():
        try:
            normalized[str(key)] = round(float(value), 4)
        except Exception:
            continue
    if not normalized:
        return build_scores(primary, secondary, intensity)
    if primary not in normalized:
        normalized[primary] = 0.7
    if secondary not in normalized:
        normalized[secondary] = 0.45
    return normalized


def clamp_risk_level(risk_level: int | None) -> int:
    if risk_level is None:
        return 0
    return min(3, max(0, int(risk_level)))


def ensure_keywords(raw_keywords: list[str] | None, text: str) -> list[str]:
    if not raw_keywords:
        return extract_keywords(text)
    result: list[str] = []
    seen: set[str] = set()
    for item in raw_keywords:
        word = str(item).strip()
        if len(word) <= 1 or word in seen:
            continue
        seen.add(word)
        result.append(word[:12])
        if len(result) >= 8:
            break
    return result or extract_keywords(text)


def parse_json_text(content: str) -> dict:
    content = content.strip()
    if content.startswith("```"):
        lines = [line for line in content.splitlines() if not line.strip().startswith("```")]
        content = "\n".join(lines).strip()
    return json.loads(content)


def call_bailian_llm(payload: AnalyzeRequest) -> dict:
    client = get_client()
    prompt = (
        "你是情绪分析引擎。请只输出JSON对象，不要输出任何额外文本。\n"
        "字段要求：\n"
        "primaryEmotion: string (happy/anxious/tired/calm/wronged/expect/lonely/irritable/sad/warm/confused/hopeful)\n"
        "secondaryEmotion: string\n"
        "emotionKeywords: string[]\n"
        "emotionScores: object<string, number[0,1]>\n"
        "sceneRecognition: string (work/study/family/social/love/sleep/health/entertainment/sport/alone/general)\n"
        "riskLevel: integer (0-3)\n"
        "riskReason: string\n"
        "aiSummary: string，使用中文，40到70字，写给用户自己看的心境天气反馈；使用第二人称“你”，可轻微使用天气隐喻，温柔具体。\n"
        "aiSummary 禁止出现：用户表达、心理风险、强度、x/10、健康状态、自然状态、诊断、属于、无风险、低风险。\n"
        f"输入文本：{payload.contentText}\n"
        f"情绪强度(1-10)：{payload.emotionIntensity or 5}\n"
    )
    completion = client.chat.completions.create(
        model=AI_MODEL_NAME,
        temperature=0.2,
        messages=[
            {"role": "system", "content": "你是专业的中文心理情绪分析助手，输出必须是合法JSON。"},
            {"role": "user", "content": prompt},
        ],
    )
    if not completion.choices:
        raise RuntimeError("Empty choices from Bailian")
    content = completion.choices[0].message.content or ""
    if not content.strip():
        raise RuntimeError("Empty content from Bailian")
    return parse_json_text(content)


def build_rule_result(payload: AnalyzeRequest) -> tuple[str, str, str, int, str, str, list[str], dict[str, float], str]:
    text = payload.contentText.strip()
    normalized = text.lower()
    primary, secondary, risk_level, risk_reason = detect_emotion(normalized)
    scene = detect_scene(normalized)
    keywords = extract_keywords(text)
    scores = build_scores(primary, secondary, payload.emotionIntensity)
    emotion_labels = {
        "happy": "愉悦",
        "anxious": "焦虑",
        "tired": "疲惫",
        "calm": "平静",
        "wronged": "委屈",
        "expect": "期待",
        "lonely": "孤独",
        "irritable": "烦躁",
        "sad": "低落",
        "warm": "温暖",
        "confused": "困惑",
        "hopeful": "希望感",
    }
    primary_label = emotion_labels.get(primary, primary)
    secondary_label = emotion_labels.get(secondary, secondary)
    summary = f"今天的你带着明显的{primary_label}，旁边也有一点{secondary_label}在陪伴。"
    if risk_level >= 2:
        summary += " 如果这片天气持续压得很低，请先把安全感放在第一位，也可以联系可信任的人。"
    elif risk_level == 1:
        summary += " 情绪的风有些紧，可以先放慢节奏，给自己一个可执行的小缓冲。"
    else:
        summary += " 可以把这份状态轻轻记下来，留给之后的自己回看。"
    return (text, primary, secondary, risk_level, risk_reason, scene, keywords, scores, summary)


@app.get("/health")
def health() -> dict[str, str]:
    return {"status": "ok"}


@app.post("/v1/mood/analyze", response_model=AnalyzeResponse)
def analyze(
    payload: AnalyzeRequest,
    _: None = Depends(verify_token),
    x_request_id: str | None = Header(default=None),
) -> AnalyzeResponse:
    start = time.time()
    text = payload.contentText.strip()
    provider = "python-rule"
    model_name = "rule-python-v1"
    model_version = AI_MODEL_VERSION
    llm_json: dict | None = None
    if AI_USE_BAILIAN:
        try:
            llm_json = call_bailian_llm(payload)
            provider = "aliyun-bailian"
            model_name = AI_MODEL_NAME
        except Exception as ex:
            logger.warning("Bailian call failed, fallback=%s, err=%s", AI_FALLBACK_TO_RULE, ex)
            if not AI_FALLBACK_TO_RULE:
                raise HTTPException(status_code=502, detail=f"Bailian call failed: {ex}") from ex

    if llm_json:
        primary = str(llm_json.get("primaryEmotion") or "calm")
        secondary = str(llm_json.get("secondaryEmotion") or "confused")
        keywords = ensure_keywords(llm_json.get("emotionKeywords"), text)
        scene = str(llm_json.get("sceneRecognition") or detect_scene(text.lower()))
        risk_level = clamp_risk_level(llm_json.get("riskLevel"))
        risk_reason = str(llm_json.get("riskReason") or "模型未给出风险原因")
        scores = normalize_scores(llm_json.get("emotionScores"), primary, secondary, payload.emotionIntensity)
        summary = str(llm_json.get("aiSummary") or f"主情绪为{primary}，次情绪为{secondary}。")
    else:
        _, primary, secondary, risk_level, risk_reason, scene, keywords, scores, summary = build_rule_result(payload)

    request_id = x_request_id or payload.traceId or uuid.uuid4().hex
    cost_ms = max(1, int((time.time() - start) * 1000))
    raw = {
        "mode": "bailian" if llm_json else "python-rule",
        "primaryEmotion": primary,
        "secondaryEmotion": secondary,
        "riskLevel": risk_level,
        "scene": scene,
        "keywords": keywords,
        "scores": scores,
        "llm": llm_json,
    }
    return AnalyzeResponse(
        primaryEmotion=primary,
        secondaryEmotion=secondary,
        emotionKeywords=keywords,
        emotionScores=scores,
        sceneRecognition=scene,
        riskLevel=risk_level,
        riskReason=risk_reason,
        aiSummary=summary,
        rawResponse=raw,
        provider=provider,
        modelName=model_name,
        modelVersion=model_version,
        promptVersion=AI_PROMPT_VERSION,
        requestId=request_id,
        analysisCostMs=cost_ms,
    )
