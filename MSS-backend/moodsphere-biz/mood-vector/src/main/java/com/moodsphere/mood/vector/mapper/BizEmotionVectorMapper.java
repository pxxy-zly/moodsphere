package com.moodsphere.mood.vector.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.mood.vector.domain.entity.BizEmotionVector;


public interface BizEmotionVectorMapper
{
    
    BizEmotionVector selectByRecordId(@Param("recordId") Long recordId);

    
    int upsertBizEmotionVector(BizEmotionVector vector);
}


