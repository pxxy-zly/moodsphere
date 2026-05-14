package com.moodsphere.weather.service;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.moodsphere.analyze.service.AnalyzeTaskPostProcessor;

/**
 * AI 分析完成后自动生成情绪天气
 */
@Component
@Order(20)
public class MoodWeatherAnalyzeTaskPostProcessor implements AnalyzeTaskPostProcessor
{
    private final IMoodWeatherService moodWeatherService;

    public MoodWeatherAnalyzeTaskPostProcessor(IMoodWeatherService moodWeatherService)
    {
        this.moodWeatherService = moodWeatherService;
    }

    @Override
    public String getName()
    {
        return "mood-weather";
    }

    @Override
    public void process(Long recordId, Long userId, String operator)
    {
        moodWeatherService.generateWeatherForUser(recordId, userId, operator);
    }
}
