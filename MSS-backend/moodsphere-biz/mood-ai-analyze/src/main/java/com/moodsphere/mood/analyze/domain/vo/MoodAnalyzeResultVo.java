package com.moodsphere.mood.analyze.domain.vo;

import com.moodsphere.mood.analyze.domain.entity.BizAiAnalysisResult;


public class MoodAnalyzeResultVo
{
    
    private Long recordId;

    
    private Integer analyzeStatus;

    
    private BizAiAnalysisResult result;

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public Integer getAnalyzeStatus()
    {
        return analyzeStatus;
    }

    public void setAnalyzeStatus(Integer analyzeStatus)
    {
        this.analyzeStatus = analyzeStatus;
    }

    public BizAiAnalysisResult getResult()
    {
        return result;
    }

    public void setResult(BizAiAnalysisResult result)
    {
        this.result = result;
    }
}


