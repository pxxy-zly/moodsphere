package com.moodsphere.record.service;

import com.moodsphere.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.record.domain.entity.BizMoodRecord;

/**
 * 情绪记录服务接口
 */
public interface IMoodRecordService
{
    /**
     * 创建情绪记录
     * 
     * @param body 创建请求体
     * @return 记录ID
     */
    Long createRecord(MoodRecordCreateBody body);

    /**
     * 获取记录详情
     * 
     * @param recordId 记录ID
     * @return 记录实体
     */
    BizMoodRecord getRecord(Long recordId);

    /**
     * 获取最新记录
     * 
     * @return 最新记录实体
     */
    BizMoodRecord getLatestRecord();

    /**
     * 提交记录
     * 
     * @param recordId 记录ID
     */
    void submitRecord(Long recordId);
}

