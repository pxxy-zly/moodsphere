package com.moodsphere.analyze.service;

import com.moodsphere.analyze.domain.vo.MoodAnalyzeResultVo;

/**
 * AI情绪分析服务接口
 */
public interface IMoodAiAnalyzeService
{
    /**
     * 执行AI情绪分析
     * 
     * @param recordId 记录ID
     * @return 分析结果
     */
    MoodAnalyzeResultVo runAnalyze(Long recordId);

    /**
     * 获取情绪分析结果
     * 
     * @param recordId 记录ID
     * @return 分析结果
     */
    MoodAnalyzeResultVo getAnalyzeResult(Long recordId);
}


