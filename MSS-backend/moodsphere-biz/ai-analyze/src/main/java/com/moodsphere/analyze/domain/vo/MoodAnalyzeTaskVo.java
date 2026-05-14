package com.moodsphere.analyze.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;

public class MoodAnalyzeTaskVo
{
    private Long taskId;

    private String taskNo;

    private Long recordId;

    private String taskStatus;

    private Integer analyzeStatus;

    private Integer pollIntervalMs;

    private BizAiAnalysisResult result;

    private String errorCode;

    private String errorMessage;

    private String requestId;

    private String provider;

    private String modelName;

    private String modelVersion;

    private String promptVersion;

    private Integer analysisCostMs;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date queuedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishedAt;

    public Long getTaskId()
    {
        return taskId;
    }

    public void setTaskId(Long taskId)
    {
        this.taskId = taskId;
    }

    public String getTaskNo()
    {
        return taskNo;
    }

    public void setTaskNo(String taskNo)
    {
        this.taskNo = taskNo;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getTaskStatus()
    {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus)
    {
        this.taskStatus = taskStatus;
    }

    public Integer getAnalyzeStatus()
    {
        return analyzeStatus;
    }

    public void setAnalyzeStatus(Integer analyzeStatus)
    {
        this.analyzeStatus = analyzeStatus;
    }

    public Integer getPollIntervalMs()
    {
        return pollIntervalMs;
    }

    public void setPollIntervalMs(Integer pollIntervalMs)
    {
        this.pollIntervalMs = pollIntervalMs;
    }

    public BizAiAnalysisResult getResult()
    {
        return result;
    }

    public void setResult(BizAiAnalysisResult result)
    {
        this.result = result;
    }

    public String getErrorCode()
    {
        return errorCode;
    }

    public void setErrorCode(String errorCode)
    {
        this.errorCode = errorCode;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage)
    {
        this.errorMessage = errorMessage;
    }

    public String getRequestId()
    {
        return requestId;
    }

    public void setRequestId(String requestId)
    {
        this.requestId = requestId;
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

    public Integer getAnalysisCostMs()
    {
        return analysisCostMs;
    }

    public void setAnalysisCostMs(Integer analysisCostMs)
    {
        this.analysisCostMs = analysisCostMs;
    }

    public Date getQueuedAt()
    {
        return queuedAt;
    }

    public void setQueuedAt(Date queuedAt)
    {
        this.queuedAt = queuedAt;
    }

    public Date getStartedAt()
    {
        return startedAt;
    }

    public void setStartedAt(Date startedAt)
    {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt()
    {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt)
    {
        this.finishedAt = finishedAt;
    }
}
