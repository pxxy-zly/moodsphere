package com.moodsphere.mood.weather.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.mood.weather.domain.entity.BizWeatherMapping;


public interface BizWeatherMappingMapper
{
    
    BizWeatherMapping selectByRecordId(@Param("recordId") Long recordId);

    
    int upsertBizWeatherMapping(BizWeatherMapping mapping);
}


