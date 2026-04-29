package com.moodsphere.weather.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.weather.domain.dto.MoodWeatherGenerateBody;
import com.moodsphere.weather.service.IMoodWeatherService;

/**
 * 情绪天气控制器
 * 提供情绪天气的生成、映射查询和今日快照接口
 */
@RestController
@RequestMapping("/app/mood/weather")
public class MoodWeatherController
{
    @Autowired
    private IMoodWeatherService moodWeatherService;

    /**
     * 生成情绪天气
     * 根据情绪向量生成对应的天气效果
     * 
     * @param body 生成请求体
     * @return 生成结果
     */
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody(required = false) MoodWeatherGenerateBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodWeatherService.generateWeather(recordId));
    }

    /**
     * 获取天气映射
     * 
     * @param recordId 记录ID
     * @return 天气映射
     */
    @GetMapping("/mapping/{recordId}")
    public AjaxResult mapping(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodWeatherService.getMapping(recordId));
    }

    /**
     * 获取今日当前动态天气（今日最新映射）
     *
     * @return 今日当前动态天气
     */
    @GetMapping("/today")
    public AjaxResult today()
    {
        return AjaxResult.success(moodWeatherService.getTodayLatestMapping());
    }

    /**
     * 获取指定日期天气快照（日期为空时默认今日）
     *
     * @param date 快照日期，格式yyyy-MM-dd
     * @return 天气快照
     */
    @GetMapping("/snapshot")
    public AjaxResult snapshot(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date date)
    {
        return AjaxResult.success(moodWeatherService.getSnapshotByDate(date));
    }

    /**
     * 获取最近天气快照列表（按日期倒序）
     *
     * @param limit 条数，默认7，最大30
     * @return 天气快照列表
     */
    @GetMapping("/snapshot/history")
    public AjaxResult snapshotHistory(@RequestParam(required = false) Integer limit)
    {
        return AjaxResult.success(moodWeatherService.listRecentSnapshots(limit));
    }
}

