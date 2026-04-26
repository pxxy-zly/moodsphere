from __future__ import annotations

from datetime import datetime
from typing import Any

from pydantic import BaseModel, Field


class AnalyzeRequest(BaseModel):
    recordId: int
    userId: int | None = None
    contentText: str = Field(min_length=1)
    emotionIntensity: int | None = Field(default=5, ge=1, le=10)
    sourceType: int | None = None
    recordTime: datetime | None = None
    traceId: str | None = None


class AnalyzeResponse(BaseModel):
    primaryEmotion: str
    secondaryEmotion: str
    emotionKeywords: list[str]
    emotionScores: dict[str, float]
    sceneRecognition: str
    riskLevel: int
    riskReason: str
    aiSummary: str
    rawResponse: dict[str, Any]
    provider: str
    modelName: str
    modelVersion: str
    promptVersion: str
    requestId: str
    analysisCostMs: int
