package com.moodsphere.mood.weather.service;

import com.moodsphere.mood.weather.domain.entity.BizWeatherMapping;
import com.moodsphere.mood.weather.domain.entity.BizWeatherSnapshot;

/**
 * 天气映射服务接口
 *
 * @author ruoyi
 */
public interface IMoodWeatherService
{
    /**
     * 生成天气映射
     *
     * @param recordId 记录ID
     * @return 映射结果
     */
    BizWeatherMapping generateWeather(Long recordId);

    /**
     * 查询某条记录映射
     *
     * @param recordId 记录ID
     * @return 映射结果
     */
    BizWeatherMapping getMapping(Long recordId);

    /**
     * 查询今日天气快照
     *
     * @return 今日快照
     */
    BizWeatherSnapshot getTodaySnapshot();
}
