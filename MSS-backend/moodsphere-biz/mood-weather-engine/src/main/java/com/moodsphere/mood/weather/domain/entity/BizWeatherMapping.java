package com.moodsphere.mood.weather.domain.entity;

import com.moodsphere.common.core.domain.BaseEntity;


public class BizWeatherMapping extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long recordId;

    private String weatherCode;

    private String weatherName;

    private Integer skyType;

    private Integer cloudDensity;

    private Integer rainIntensity;

    private Integer lightningIntensity;

    private Integer windSpeed;

    private Integer fogIntensity;

    private Integer colorTemperature;

    private Integer saturation;

    private String particleStyle;

    private Integer animationSeed;

    private String extraParams;

    private Integer delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getRecordId()
    {
        return recordId;
    }

    public void setRecordId(Long recordId)
    {
        this.recordId = recordId;
    }

    public String getWeatherCode()
    {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode)
    {
        this.weatherCode = weatherCode;
    }

    public String getWeatherName()
    {
        return weatherName;
    }

    public void setWeatherName(String weatherName)
    {
        this.weatherName = weatherName;
    }

    public Integer getSkyType()
    {
        return skyType;
    }

    public void setSkyType(Integer skyType)
    {
        this.skyType = skyType;
    }

    public Integer getCloudDensity()
    {
        return cloudDensity;
    }

    public void setCloudDensity(Integer cloudDensity)
    {
        this.cloudDensity = cloudDensity;
    }

    public Integer getRainIntensity()
    {
        return rainIntensity;
    }

    public void setRainIntensity(Integer rainIntensity)
    {
        this.rainIntensity = rainIntensity;
    }

    public Integer getLightningIntensity()
    {
        return lightningIntensity;
    }

    public void setLightningIntensity(Integer lightningIntensity)
    {
        this.lightningIntensity = lightningIntensity;
    }

    public Integer getWindSpeed()
    {
        return windSpeed;
    }

    public void setWindSpeed(Integer windSpeed)
    {
        this.windSpeed = windSpeed;
    }

    public Integer getFogIntensity()
    {
        return fogIntensity;
    }

    public void setFogIntensity(Integer fogIntensity)
    {
        this.fogIntensity = fogIntensity;
    }

    public Integer getColorTemperature()
    {
        return colorTemperature;
    }

    public void setColorTemperature(Integer colorTemperature)
    {
        this.colorTemperature = colorTemperature;
    }

    public Integer getSaturation()
    {
        return saturation;
    }

    public void setSaturation(Integer saturation)
    {
        this.saturation = saturation;
    }

    public String getParticleStyle()
    {
        return particleStyle;
    }

    public void setParticleStyle(String particleStyle)
    {
        this.particleStyle = particleStyle;
    }

    public Integer getAnimationSeed()
    {
        return animationSeed;
    }

    public void setAnimationSeed(Integer animationSeed)
    {
        this.animationSeed = animationSeed;
    }

    public String getExtraParams()
    {
        return extraParams;
    }

    public void setExtraParams(String extraParams)
    {
        this.extraParams = extraParams;
    }

    public Integer getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag)
    {
        this.delFlag = delFlag;
    }
}


