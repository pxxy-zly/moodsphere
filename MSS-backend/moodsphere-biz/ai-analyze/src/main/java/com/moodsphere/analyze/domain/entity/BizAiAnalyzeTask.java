package com.moodsphere.analyze.domain.entity;

import java.util.Date;

import com.moodsphere.common.core.domain.BaseEntity;

/**
 * AI 分析任务
 */
public class BizAiAnalyzeTask extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private String taskNo;

    private Long recordId;

    private Long userId;

    private Integer taskStatus;

    private String failCode;

    private String failMessage;

    private Integer retryCount;

    private String requestSnapshot;

    private String responseSnapshot;

    private String provider;

    private String modelName;

    private String modelVersion;

    private String promptVersion;

    private String requestId;

    private Integer analysisCostMs;

    private Date queuedAt;

    private Date startedAt;

    private Date finishedAt;

    private Integer delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
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

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Integer getTaskStatus()
    {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus)
    {
        this.taskStatus = taskStatus;
    }

    public String getFailCode()
    {
        return failCode;
    }

    public void setFailCode(String failCode)
    {
        this.failCode = failCode;
    }

    public String getFailMessage()
    {
        return failMessage;
    }

    public void setFailMessage(String failMessage)
    {
        this.failMessage = failMessage;
    }

    public Integer getRetryCount()
    {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount)
    {
        this.retryCount = retryCount;
    }

    public String getRequestSnapshot()
    {
        return requestSnapshot;
    }

    public void setRequestSnapshot(String requestSnapshot)
    {
        this.requestSnapshot = requestSnapshot;
    }

    public String getResponseSnapshot()
    {
        return responseSnapshot;
    }

    public void setResponseSnapshot(String responseSnapshot)
    {
        this.responseSnapshot = responseSnapshot;
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

    public Integer getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }
}
