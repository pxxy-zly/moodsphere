package com.moodsphere.mood.vector.service;

import com.moodsphere.mood.vector.domain.entity.BizEmotionVector;

/**
 * 向量服务接口
 *
 * @author ruoyi
 */
public interface IMoodVectorService
{
    /**
     * 构建向量
     *
     * @param recordId 记录ID
     * @return 向量
     */
    BizEmotionVector buildVector(Long recordId);

    /**
     * 查询向量
     *
     * @param recordId 记录ID
     * @return 向量
     */
    BizEmotionVector getVector(Long recordId);
}
