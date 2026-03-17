package com.moodsphere.weather.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.weather.domain.dto.MoodWeatherGenerateBody;
import com.moodsphere.weather.service.IMoodWeatherService;

@RestController
@RequestMapping("/app/mood/weather")
public class MoodWeatherController
{
    @Autowired
    private IMoodWeatherService moodWeatherService;

    @PostMapping("/generate")
    public AjaxResult generate(@RequestBody(required = false) MoodWeatherGenerateBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodWeatherService.generateWeather(recordId));
    }

    @GetMapping("/mapping/{recordId}")
    public AjaxResult mapping(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodWeatherService.getMapping(recordId));
    }

    @GetMapping("/today")
    public AjaxResult today()
    {
        return AjaxResult.success(moodWeatherService.getTodaySnapshot());
    }
}


