package com.moodsphere.analyze.domain.dto;

import java.util.Date;

/**
 * Java -> Python 分析请求
 */
public class PythonAnalyzeRequest
{
    private Long recordId;

    private Long userId;

    private String contentText;

    private Integer emotionIntensity;

    private Integer sourceType;

    private Date recordTime;

    private String traceId;

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

    public String getContentText()
    {
        return contentText;
    }

    public void setContentText(String contentText)
    {
        this.contentText = contentText;
    }

    public Integer getEmotionIntensity()
    {
        return emotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity)
    {
        this.emotionIntensity = emotionIntensity;
    }

    public Integer getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(Integer sourceType)
    {
        this.sourceType = sourceType;
    }

    public Date getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }

    public String getTraceId()
    {
        return traceId;
    }

    public void setTraceId(String traceId)
    {
        this.traceId = traceId;
    }
}
