from __future__ import annotations

from datetime import datetime
from typing import Any

from pydantic import BaseModel, Field


class MediaAsset(BaseModel):
    id: int | None = None
    assetType: int | None = None
    fileUrl: str
    mimeType: str | None = None
    fileSize: int | None = None
    duration: int | None = None
    thumbnailUrl: str | None = None
    width: int | None = None
    height: int | None = None


class AnalyzeRequest(BaseModel):
    recordId: int
    userId: int | None = None
    contentText: str | None = ""
    emotionIntensity: int | None = Field(default=5, ge=1, le=10)
    sourceType: int | None = None
    recordTime: datetime | None = None
    traceId: str | None = None
    mediaAssets: list[MediaAsset] = Field(default_factory=list)
    imageUrls: list[str] = Field(default_factory=list)
    audioUrls: list[str] = Field(default_factory=list)


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
