package com.moodsphere.weather.mapper;

import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import com.moodsphere.weather.domain.entity.BizWeatherSnapshot;
import com.moodsphere.weather.domain.vo.DailyVectorAggregateVo;


public interface BizWeatherSnapshotMapper
{
    
    BizWeatherSnapshot selectByUserAndDate(@Param("userId") Long userId, @Param("snapshotDate") Date snapshotDate);

    List<BizWeatherSnapshot> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    
    int upsertBizWeatherSnapshot(BizWeatherSnapshot snapshot);

    
    DailyVectorAggregateVo selectDailyVectorAggregate(@Param("userId") Long userId, @Param("snapshotDate") Date snapshotDate);
}



