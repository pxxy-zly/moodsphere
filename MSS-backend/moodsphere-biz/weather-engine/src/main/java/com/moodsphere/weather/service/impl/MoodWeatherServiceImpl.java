package com.moodsphere.weather.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;
import com.moodsphere.vector.domain.entity.BizEmotionVector;
import com.moodsphere.vector.mapper.BizEmotionVectorMapper;
import com.moodsphere.weather.domain.entity.BizWeatherMapping;
import com.moodsphere.weather.domain.entity.BizWeatherSnapshot;
import com.moodsphere.weather.domain.vo.DailyVectorAggregateVo;
import com.moodsphere.weather.mapper.BizWeatherMappingMapper;
import com.moodsphere.weather.mapper.BizWeatherSnapshotMapper;
import com.moodsphere.weather.service.IMoodWeatherService;
@Service
public class MoodWeatherServiceImpl implements IMoodWeatherService
{
    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizEmotionVectorMapper bizEmotionVectorMapper;

    @Autowired
    private BizWeatherMappingMapper bizWeatherMappingMapper;

    @Autowired
    private BizWeatherSnapshotMapper bizWeatherSnapshotMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizWeatherMapping generateWeather(Long recordId)
    {
        checkRecordId(recordId);
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        BizEmotionVector vector = bizEmotionVectorMapper.selectByRecordId(recordId);
        if (vector == null)
        {
            throw new ServiceException("璇峰厛鐢熸垚鎯呯华鍚戦噺");
        }
        WeatherProfile profile = buildWeatherProfile(
                safeDouble(vector.getValence()),
                safeDouble(vector.getArousal()),
                safeDouble(vector.getAnxiety()),
                safeDouble(vector.getCalmness()),
                safeDouble(vector.getLoneliness()),
                safeDouble(vector.getFatigue()),
                safeDouble(vector.getAnger()),
                safeDouble(vector.getHope()),
                safeDouble(vector.getConfidence()));

        Date now = new Date();
        BizWeatherMapping mapping = new BizWeatherMapping();
        mapping.setRecordId(recordId);
        mapping.setWeatherCode(profile.weatherCode);
        mapping.setWeatherName(profile.weatherName);
        mapping.setSkyType(profile.skyType);
        mapping.setCloudDensity(profile.cloudDensity);
        mapping.setRainIntensity(profile.rainIntensity);
        mapping.setLightningIntensity(profile.lightningIntensity);
        mapping.setWindSpeed(profile.windSpeed);
        mapping.setFogIntensity(profile.fogIntensity);
        mapping.setColorTemperature(profile.colorTemperature);
        mapping.setSaturation(profile.saturation);
        mapping.setParticleStyle(profile.particleStyle);
        mapping.setAnimationSeed((int) (Math.abs(recordId % 100000L)));
        mapping.setExtraParams(buildMappingExtraParams(vector));
        mapping.setDelFlag(0);
        mapping.setCreateBy(username);
        mapping.setCreateTime(now);
        mapping.setUpdateBy(username);
        mapping.setUpdateTime(now);
        bizWeatherMappingMapper.upsertBizWeatherMapping(mapping);

        refreshDailySnapshot(userId, record.getRecordTime(), username);
        return bizWeatherMappingMapper.selectByRecordId(recordId);
    }

    @Override
    public BizWeatherMapping getMapping(Long recordId)
    {
        checkRecordId(recordId);
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, SecurityUtils.getUserId());
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        return bizWeatherMappingMapper.selectByRecordId(recordId);
    }

    @Override
    public BizWeatherSnapshot getTodaySnapshot()
    {
        return bizWeatherSnapshotMapper.selectByUserAndDate(SecurityUtils.getUserId(), todayDate());
    }

    private void refreshDailySnapshot(Long userId, Date recordTime, String username)
    {
        Date snapshotDate = dateOnly(recordTime == null ? new Date() : recordTime);
        DailyVectorAggregateVo aggregate = bizWeatherSnapshotMapper.selectDailyVectorAggregate(userId, snapshotDate);
        if (aggregate == null || aggregate.getRecordCount() == null || aggregate.getRecordCount() <= 0)
        {
            return;
        }

        WeatherProfile profile = buildWeatherProfile(
                safeDouble(aggregate.getAvgValence()),
                safeDouble(aggregate.getAvgArousal()),
                safeDouble(aggregate.getAvgAnxiety()),
                safeDouble(aggregate.getAvgCalmness()),
                safeDouble(aggregate.getAvgLoneliness()),
                safeDouble(aggregate.getAvgFatigue()),
                safeDouble(aggregate.getAvgAnger()),
                safeDouble(aggregate.getAvgHope()),
                safeDouble(aggregate.getAvgConfidence()));

        BizWeatherSnapshot snapshot = new BizWeatherSnapshot();
        snapshot.setUserId(userId);
        snapshot.setSnapshotDate(snapshotDate);
        snapshot.setWeatherCode(profile.weatherCode);
        snapshot.setWeatherName(profile.weatherName);
        snapshot.setSkyType(profile.skyType);
        snapshot.setCloudDensity(profile.cloudDensity);
        snapshot.setRainIntensity(profile.rainIntensity);
        snapshot.setLightningIntensity(profile.lightningIntensity);
        snapshot.setWindSpeed(profile.windSpeed);
        snapshot.setFogIntensity(profile.fogIntensity);
        snapshot.setColorTemperature(profile.colorTemperature);
        snapshot.setSaturation(profile.saturation);
        snapshot.setParticleStyle(profile.particleStyle);
        snapshot.setAnimationSeed((int) (Math.abs((userId + snapshotDate.getTime()) % 100000L)));
        snapshot.setCombinedVector(buildCombinedVector(aggregate));
        snapshot.setRecordCount(aggregate.getRecordCount());
        snapshot.setDelFlag(0);
        snapshot.setCreateBy(username);
        snapshot.setCreateTime(new Date());
        snapshot.setUpdateBy(username);
        snapshot.setUpdateTime(new Date());
        bizWeatherSnapshotMapper.upsertBizWeatherSnapshot(snapshot);
    }

    private String buildMappingExtraParams(BizEmotionVector vector)
    {
        JSONObject object = new JSONObject();
        object.put("valence", vector.getValence());
        object.put("arousal", vector.getArousal());
        object.put("anxiety", vector.getAnxiety());
        object.put("calmness", vector.getCalmness());
        object.put("hope", vector.getHope());
        object.put("confidence", vector.getConfidence());
        return object.toJSONString();
    }

    private String buildCombinedVector(DailyVectorAggregateVo aggregate)
    {
        JSONObject object = new JSONObject();
        object.put("valence", aggregate.getAvgValence());
        object.put("arousal", aggregate.getAvgArousal());
        object.put("anxiety", aggregate.getAvgAnxiety());
        object.put("calmness", aggregate.getAvgCalmness());
        object.put("loneliness", aggregate.getAvgLoneliness());
        object.put("fatigue", aggregate.getAvgFatigue());
        object.put("anger", aggregate.getAvgAnger());
        object.put("hope", aggregate.getAvgHope());
        object.put("confidence", aggregate.getAvgConfidence());
        return object.toJSONString();
    }

    private WeatherProfile buildWeatherProfile(double valence, double arousal, double anxiety, double calmness, double loneliness,
            double fatigue, double anger, double hope, double confidence)
    {
        if (anxiety >= 0.75D || anger >= 0.70D)
        {
            return new WeatherProfile("storm", "storm", 5, 9, 9, 8, 8, 4, 4200, 38, "storm");
        }
        if (fatigue >= 0.65D && loneliness >= 0.60D)
        {
            return new WeatherProfile("mist", "mist", 4, 8, 2, 0, 2, 8, 4600, 42, "mist");
        }
        if (valence >= 0.72D && hope >= 0.62D)
        {
            return new WeatherProfile("sunny", "sunny", 1, 2, 0, 0, 2, 1, 6700, 86, "sunshine");
        }
        if (calmness >= 0.72D && confidence >= 0.55D)
        {
            return new WeatherProfile("breeze", "breeze", 2, 3, 0, 0, 4, 1, 6100, 72, "breeze");
        }
        if (valence <= 0.35D || arousal <= 0.30D)
        {
            return new WeatherProfile("rain", "rain", 3, 7, 6, 1, 4, 3, 5000, 52, "rain");
        }
        return new WeatherProfile("cloudy", "cloudy", 2, 6, 1, 0, 3, 2, 5600, 64, "cloud");
    }

    private double safeDouble(BigDecimal value)
    {
        return value == null ? 0D : value.doubleValue();
    }

    private Date dateOnly(Date date)
    {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return java.sql.Date.valueOf(localDate);
    }

    private Date todayDate()
    {
        return java.sql.Date.valueOf(LocalDate.now());
    }

    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }
    private static class WeatherProfile
    {
        private final String weatherCode;

        private final String weatherName;

        private final int skyType;

        private final int cloudDensity;

        private final int rainIntensity;

        private final int lightningIntensity;

        private final int windSpeed;

        private final int fogIntensity;

        private final int colorTemperature;

        private final int saturation;

        private final String particleStyle;

        private WeatherProfile(String weatherCode, String weatherName, int skyType, int cloudDensity, int rainIntensity,
                int lightningIntensity, int windSpeed, int fogIntensity, int colorTemperature, int saturation, String particleStyle)
        {
            this.weatherCode = weatherCode;
            this.weatherName = weatherName;
            this.skyType = skyType;
            this.cloudDensity = cloudDensity;
            this.rainIntensity = rainIntensity;
            this.lightningIntensity = lightningIntensity;
            this.windSpeed = windSpeed;
            this.fogIntensity = fogIntensity;
            this.colorTemperature = colorTemperature;
            this.saturation = saturation;
            this.particleStyle = particleStyle;
        }
    }
}

