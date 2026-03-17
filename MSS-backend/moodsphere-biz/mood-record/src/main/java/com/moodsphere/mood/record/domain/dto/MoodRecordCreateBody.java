package com.moodsphere.mood.record.domain.dto;

import java.math.BigDecimal;
import java.util.Date;

public class MoodRecordCreateBody
{
    private String contentText;

    private Integer emotionIntensity;

    private Integer isPublic;

    private String province;

    private String city;

    private String district;

    private String locationName;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private Date recordTime;

    public String getContentText()
    {
        return contentText;
    }

    public void setContentText(String contentText)
    {
        this.contentText = contentText;
    }

    public Integer getEmotionIntensity()
    {
        return emotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity)
    {
        this.emotionIntensity = emotionIntensity;
    }

    public Integer getIsPublic()
    {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic)
    {
        this.isPublic = isPublic;
    }

    public String getProvince()
    {
        return province;
    }

    public void setProvince(String province)
    {
        this.province = province;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getDistrict()
    {
        return district;
    }

    public void setDistrict(String district)
    {
        this.district = district;
    }

    public String getLocationName()
    {
        return locationName;
    }

    public void setLocationName(String locationName)
    {
        this.locationName = locationName;
    }

    public BigDecimal getLongitude()
    {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude)
    {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude()
    {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude)
    {
        this.latitude = latitude;
    }

    public Date getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }
}


