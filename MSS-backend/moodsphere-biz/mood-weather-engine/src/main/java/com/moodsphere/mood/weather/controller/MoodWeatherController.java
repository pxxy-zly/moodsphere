package com.moodsphere.mood.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.mood.weather.domain.dto.MoodWeatherGenerateBody;
import com.moodsphere.mood.weather.service.IMoodWeatherService;

/**
 * 天气映射控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/mood/weather")
public class MoodWeatherController
{
    @Autowired
    private IMoodWeatherService moodWeatherService;

    /**
     * 生成天气映射
     *
     * @param body 请求参数
     * @return 映射结果
     */
    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody(required = false) MoodWeatherGenerateBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodWeatherService.generateWeather(recordId));
    }

    /**
     * 查询某条记录映射
     *
     * @param recordId 记录ID
     * @return 映射结果
     */
    @GetMapping("/mapping/{recordId}")
    public AjaxResult mapping(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodWeatherService.getMapping(recordId));
    }

    /**
     * 查询今日天气快照
     *
     * @return 今日快照
     */
    @GetMapping("/today")
    public AjaxResult today()
    {
        return AjaxResult.success(moodWeatherService.getTodaySnapshot());
    }
}
