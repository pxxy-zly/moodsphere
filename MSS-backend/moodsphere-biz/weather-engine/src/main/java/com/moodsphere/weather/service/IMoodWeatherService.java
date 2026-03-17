package com.moodsphere.weather.service;

import com.moodsphere.weather.domain.entity.BizWeatherMapping;
import com.moodsphere.weather.domain.entity.BizWeatherSnapshot;

public interface IMoodWeatherService
{
    BizWeatherMapping generateWeather(Long recordId);

    BizWeatherMapping getMapping(Long recordId);

    BizWeatherSnapshot getTodaySnapshot();
}


