package com.moodsphere.analyze.service;

import com.moodsphere.analyze.domain.vo.MoodAnalyzeTaskVo;

/**
 * AI情绪分析服务接口
 */
public interface IMoodAiAnalyzeService
{
    /**
     * 提交 AI 情绪分析任务
     * 
     * @param recordId 记录ID
     * @return 任务信息
     */
    MoodAnalyzeTaskVo submitAnalyzeTask(Long recordId);

    /**
     * 根据任务ID获取任务状态
     * 
     * @param taskId 任务ID
     * @return 任务信息
     */
    MoodAnalyzeTaskVo getAnalyzeTaskByTaskId(Long taskId);

    /**
     * 根据记录ID获取最新任务状态
     * 
     * @param recordId 记录ID
     * @return 任务信息
     */
    MoodAnalyzeTaskVo getAnalyzeTaskByRecordId(Long recordId);
}

