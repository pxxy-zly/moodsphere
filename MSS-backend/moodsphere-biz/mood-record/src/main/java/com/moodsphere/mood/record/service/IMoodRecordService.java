package com.moodsphere.mood.record.service;

import com.moodsphere.mood.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.mood.record.domain.entity.BizMoodRecord;

/**
 * 情绪记录服务接口
 *
 * @author ruoyi
 */
public interface IMoodRecordService
{
    /**
     * 创建记录
     *
     * @param body 请求参数
     * @return 记录ID
     */
    Long createRecord(MoodRecordCreateBody body);

    /**
     * 查询记录详情
     *
     * @param recordId 记录ID
     * @return 记录
     */
    BizMoodRecord getRecord(Long recordId);

    /**
     * 查询最近一条记录
     *
     * @return 记录
     */
    BizMoodRecord getLatestRecord();

    /**
     * 提交记录
     *
     * @param recordId 记录ID
     */
    void submitRecord(Long recordId);
}
