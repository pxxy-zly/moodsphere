package com.moodsphere.analyze.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;


public interface BizAiAnalysisResultMapper
{
    
    BizAiAnalysisResult selectByRecordId(@Param("recordId") Long recordId);

    
    int upsertBizAiAnalysisResult(BizAiAnalysisResult result);
}



