package com.moodsphere.vector.service;

import com.moodsphere.vector.domain.entity.BizEmotionVector;

public interface IMoodVectorService
{
    BizEmotionVector buildVector(Long recordId);

    BizEmotionVector getVector(Long recordId);
}


