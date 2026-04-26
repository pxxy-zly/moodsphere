package com.moodsphere.analyze.domain.dto;

import java.util.List;
import java.util.Map;

/**
 * Python -> Java 分析响应
 */
public class PythonAnalyzeResponse
{
    private String primaryEmotion;

    private String secondaryEmotion;

    private List<String> emotionKeywords;

    private Map<String, Double> emotionScores;

    private String sceneRecognition;

    private Integer riskLevel;

    private String riskReason;

    private String aiSummary;

    private Object rawResponse;

    private String provider;

    private String modelName;

    private String modelVersion;

    private String promptVersion;

    private String requestId;

    private Integer analysisCostMs;

    public String getPrimaryEmotion()
    {
        return primaryEmotion;
    }

    public void setPrimaryEmotion(String primaryEmotion)
    {
        this.primaryEmotion = primaryEmotion;
    }

    public String getSecondaryEmotion()
    {
        return secondaryEmotion;
    }

    public void setSecondaryEmotion(String secondaryEmotion)
    {
        this.secondaryEmotion = secondaryEmotion;
    }

    public List<String> getEmotionKeywords()
    {
        return emotionKeywords;
    }

    public void setEmotionKeywords(List<String> emotionKeywords)
    {
        this.emotionKeywords = emotionKeywords;
    }

    public Map<String, Double> getEmotionScores()
    {
        return emotionScores;
    }

    public void setEmotionScores(Map<String, Double> emotionScores)
    {
        this.emotionScores = emotionScores;
    }

    public String getSceneRecognition()
    {
        return sceneRecognition;
    }

    public void setSceneRecognition(String sceneRecognition)
    {
        this.sceneRecognition = sceneRecognition;
    }

    public Integer getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel)
    {
        this.riskLevel = riskLevel;
    }

    public String getRiskReason()
    {
        return riskReason;
    }

    public void setRiskReason(String riskReason)
    {
        this.riskReason = riskReason;
    }

    public String getAiSummary()
    {
        return aiSummary;
    }

    public void setAiSummary(String aiSummary)
    {
        this.aiSummary = aiSummary;
    }

    public Object getRawResponse()
    {
        return rawResponse;
    }

    public void setRawResponse(Object rawResponse)
    {
        this.rawResponse = rawResponse;
    }

    public String getProvider()
    {
        return provider;
    }

    public void setProvider(String provider)
    {
        this.provider = provider;
    }

    public String getModelName()
    {
        return modelName;
    }

    public void setModelName(String modelName)
    {
        this.modelName = modelName;
    }

    public String getModelVersion()
    {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion)
    {
        this.modelVersion = modelVersion;
    }

    public String getPromptVersion()
    {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion)
    {
        this.promptVersion = promptVersion;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
    }

    public Integer getAnalysisCostMs()
    {
        return analysisCostMs;
    }

    public void setAnalysisCostMs(Integer analysisCostMs)
    {
        this.analysisCostMs = analysisCostMs;
    }
}
