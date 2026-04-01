package com.moodsphere.weather.service;

import com.moodsphere.weather.domain.entity.BizWeatherMapping;
import com.moodsphere.weather.domain.entity.BizWeatherSnapshot;

/**
 * 情绪天气服务接口
 */
public interface IMoodWeatherService
{
    /**
     * 生成情绪天气
     * 根据情绪向量生成对应的天气效果
     * 
     * @param recordId 记录ID
     * @return 天气映射
     */
    BizWeatherMapping generateWeather(Long recordId);

    /**
     * 获取天气映射
     * 
     * @param recordId 记录ID
     * @return 天气映射
     */
    BizWeatherMapping getMapping(Long recordId);

    /**
     * 获取今日天气快照
     * 
     * @return 今日天气快照
     */
    BizWeatherSnapshot getTodaySnapshot();
}


