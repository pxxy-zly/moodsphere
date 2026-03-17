package com.moodsphere.mood.analyze.domain.entity;

import java.util.Date;
import com.moodsphere.common.core.domain.BaseEntity;


public class BizAiAnalysisResult extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long recordId;

    private String primaryEmotion;

    private String secondaryEmotion;

    private String emotionKeywords;

    private String emotionScores;

    private String sceneRecognition;

    private Integer riskLevel;

    private String riskReason;

    private String aiSummary;

    private String rawResponse;

    private String provider;

    private String modelName;

    private String modelVersion;

    private String promptVersion;

    private String requestId;

    private Date analysisAt;

    private Integer analysisCostMs;

    private Integer status;

    private Integer delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

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

    public String getEmotionKeywords()
    {
        return emotionKeywords;
    }

    public void setEmotionKeywords(String emotionKeywords)
    {
        this.emotionKeywords = emotionKeywords;
    }

    public String getEmotionScores()
    {
        return emotionScores;
    }

    public void setEmotionScores(String emotionScores)
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

    public String getRawResponse()
    {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse)
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

    public Date getAnalysisAt()
    {
        return analysisAt;
    }

    public void setAnalysisAt(Date analysisAt)
    {
        this.analysisAt = analysisAt;
    }

    public Integer getAnalysisCostMs()
    {
        return analysisCostMs;
    }

    public void setAnalysisCostMs(Integer analysisCostMs)
    {
        this.analysisCostMs = analysisCostMs;
    }

    public Integer getStatus()
    {
        return status;
    }

    public void setStatus(Integer status)
    {
        this.status = status;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }
}


