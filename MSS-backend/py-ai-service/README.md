# Python AI Service Skeleton

This service provides a FastAPI endpoint for multimodal mood analysis:

- `GET /health`
- `POST /v1/mood/analyze`

By default it calls Alibaba Cloud Model Studio (Bailian) through the OpenAI-compatible endpoint, using Qwen-Omni for text, image, and voice analysis.

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
- `AI_MODEL_NAME` (default: `qwen3.5-omni-plus-2026-03-15`)
- `AI_MODEL_VERSION` (default: `2026-03-15`)
- `AI_USE_BAILIAN` (`true/false`)
- `AI_FALLBACK_TO_RULE` (`true/false`)
- `AI_API_TOKEN` (optional, for Java -> Python service authentication)
- `AI_MAX_MEDIA_ASSETS` (default: `12`)
- `AI_MAX_INLINE_FILE_BYTES` (default: `9437184`; used only when the request sends a local file path instead of an HTTP/data URL)

## 4) Multimodal request payload

`contentText` may be empty when the record only has image or voice assets. The Java backend sends bound `biz_mood_asset` rows as `mediaAssets`:

```json
{
  "recordId": 1,
  "userId": 100,
  "contentText": "今天有点累",
  "emotionIntensity": 6,
  "mediaAssets": [
    {
      "id": 10,
      "assetType": 1,
      "fileUrl": "https://example.com/uploads/a.jpg",
      "mimeType": "image/jpeg"
    },
    {
      "id": 11,
      "assetType": 2,
      "fileUrl": "https://example.com/uploads/b.m4a",
      "mimeType": "audio/mp4",
      "duration": 12
    }
  ]
}
```

`assetType=1` is image and `assetType=2` is voice. For direct Python testing, `imageUrls` and `audioUrls` are also accepted.

## 5) Java side config

Set environment variables for `moodsphere-admin`:

- `MOOD_AI_PY_ENABLED=true`
- `MOOD_AI_PY_BASE_URL=http://127.0.0.1:9001`
- `MOOD_AI_PY_ANALYZE_PATH=/v1/mood/analyze`
- `MOOD_AI_PY_TOKEN=replace_me` (optional)
- `MOOD_AI_PY_TIMEOUT_MS=30000`
- `MOOD_AI_PY_FALLBACK_TO_MOCK=true`

## 6) Notes

- For Alibaba Cloud Bailian, you should configure API key with env var `DASHSCOPE_API_KEY`.
- If Bailian call fails and `AI_FALLBACK_TO_RULE=true`, service will return rule-engine output instead of failing hard.
- Qwen-Omni is called with streaming enabled and text output only. Images are sent as `image_url`; voice files are sent as `input_audio`.
