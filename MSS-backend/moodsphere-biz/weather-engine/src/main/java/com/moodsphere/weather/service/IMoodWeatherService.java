package com.moodsphere.weather.service;

import java.util.Date;
import java.util.List;

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
     * 获取用户今日最新动态天气映射
     *
     * @return 今日最新动态天气映射
     */
    BizWeatherMapping getTodayLatestMapping();

    /**
     * 获取今日天气快照
     * 
     * @return 今日天气快照
     */
    BizWeatherSnapshot getTodaySnapshot();

    /**
     * 获取指定日期天气快照（日期为空时默认今日）
     *
     * @param snapshotDate 快照日期
     * @return 天气快照
     */
    BizWeatherSnapshot getSnapshotByDate(Date snapshotDate);

    /**
     * 获取最近天气快照列表（按日期倒序）
     *
     * @param limit 条数限制
     * @return 天气快照列表
     */
    List<BizWeatherSnapshot> listRecentSnapshots(Integer limit);
}

