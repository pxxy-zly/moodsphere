package com.moodsphere.weather.mapper;

import java.util.Date;
import org.apache.ibatis.annotations.Param;
import com.moodsphere.weather.domain.entity.BizWeatherMapping;


public interface BizWeatherMappingMapper
{
    
    BizWeatherMapping selectByRecordId(@Param("recordId") Long recordId);

    BizWeatherMapping selectTodayLatestByUserId(@Param("userId") Long userId, @Param("snapshotDate") Date snapshotDate);

    
    int upsertBizWeatherMapping(BizWeatherMapping mapping);
}



