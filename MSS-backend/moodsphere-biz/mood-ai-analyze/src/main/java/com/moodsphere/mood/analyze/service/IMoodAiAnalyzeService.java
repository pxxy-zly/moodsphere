package com.moodsphere.mood.analyze.service;

import com.moodsphere.mood.analyze.domain.vo.MoodAnalyzeResultVo;

/**
 * AI分析服务接口
 *
 * @author ruoyi
 */
public interface IMoodAiAnalyzeService
{
    /**
     * 执行AI分析
     *
     * @param recordId 记录ID
     * @return 分析结果
     */
    MoodAnalyzeResultVo runAnalyze(Long recordId);

    /**
     * 查询分析结果
     *
     * @param recordId 记录ID
     * @return 分析结果
     */
    MoodAnalyzeResultVo getAnalyzeResult(Long recordId);
}
