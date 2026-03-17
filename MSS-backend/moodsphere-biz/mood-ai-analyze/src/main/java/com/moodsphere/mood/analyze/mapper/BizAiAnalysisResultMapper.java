package com.moodsphere.mood.analyze.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.mood.analyze.domain.entity.BizAiAnalysisResult;


public interface BizAiAnalysisResultMapper
{
    
    BizAiAnalysisResult selectByRecordId(@Param("recordId") Long recordId);

    
    int upsertBizAiAnalysisResult(BizAiAnalysisResult result);
}


