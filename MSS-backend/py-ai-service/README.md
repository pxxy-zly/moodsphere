# Python AI Service Skeleton

This service provides a FastAPI endpoint for mood analysis:

- `GET /health`
- `POST /v1/mood/analyze`

By default it can call Alibaba Cloud Model Studio (Bailian) through the OpenAI-compatible endpoint.

## 1) Setup

```bash
cd py-ai-service
python -m venv .venv
.venv\Scripts\activate
pip install -r requirements.txt
copy .env.example .env
```

## 2) Run

```bash
uvicorn app.main:app --host 0.0.0.0 --port 9001 --reload
```

## 3) Required environment variables (Bailian)

- `DASHSCOPE_API_KEY` (required when `AI_USE_BAILIAN=true`)
- `DASHSCOPE_BASE_URL` (default: `https://dashscope.aliyuncs.com/compatible-mode/v1`)
- `AI_MODEL_NAME` (default: `qwen-plus`)
- `AI_USE_BAILIAN` (`true/false`)
- `AI_FALLBACK_TO_RULE` (`true/false`)
- `AI_API_TOKEN` (optional, for Java -> Python service authentication)

## 4) Java side config

Set environment variables for `moodsphere-admin`:

- `MOOD_AI_PY_ENABLED=true`
- `MOOD_AI_PY_BASE_URL=http://127.0.0.1:9001`
- `MOOD_AI_PY_ANALYZE_PATH=/v1/mood/analyze`
- `MOOD_AI_PY_TOKEN=replace_me` (optional)
- `MOOD_AI_PY_TIMEOUT_MS=8000`
- `MOOD_AI_PY_FALLBACK_TO_MOCK=true`

## 5) Notes

- For Alibaba Cloud Bailian, you should configure API key with env var `DASHSCOPE_API_KEY`.
- If Bailian call fails and `AI_FALLBACK_TO_RULE=true`, service will return rule-engine output instead of failing hard.
