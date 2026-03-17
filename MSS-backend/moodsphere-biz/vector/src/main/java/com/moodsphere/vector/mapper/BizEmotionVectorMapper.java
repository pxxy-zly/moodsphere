package com.moodsphere.vector.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.vector.domain.entity.BizEmotionVector;


public interface BizEmotionVectorMapper
{
    
    BizEmotionVector selectByRecordId(@Param("recordId") Long recordId);

    
    int upsertBizEmotionVector(BizEmotionVector vector);
}



