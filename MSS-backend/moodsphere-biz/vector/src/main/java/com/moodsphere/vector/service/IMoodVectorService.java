package com.moodsphere.vector.service;

import com.moodsphere.vector.domain.entity.BizEmotionVector;

/**
 * 情绪向量服务接口
 */
public interface IMoodVectorService
{
    /**
     * 构建情绪向量
     * 根据AI分析结果生成情绪向量
     * 
     * @param recordId 记录ID
     * @return 情绪向量实体
     */
    BizEmotionVector buildVector(Long recordId);

    /**
     * 获取情绪向量
     * 
     * @param recordId 记录ID
     * @return 情绪向量实体
     */
    BizEmotionVector getVector(Long recordId);
}


