package com.moodsphere.vector.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.moodsphere.analyze.service.AnalyzeTaskPostProcessor;

/**
 * AI 分析完成后自动生成情绪向量
 */
@Component
@Order(10)
public class MoodVectorAnalyzeTaskPostProcessor implements AnalyzeTaskPostProcessor
{
    private final IMoodVectorService moodVectorService;

    public MoodVectorAnalyzeTaskPostProcessor(IMoodVectorService moodVectorService)
    {
        this.moodVectorService = moodVectorService;
    }

    @Override
    public String getName()
    {
        return "mood-vector";
    }

    @Override
    public void process(Long recordId, Long userId, String operator)
    {
        moodVectorService.buildVectorForUser(recordId, userId, operator);
    }
}
