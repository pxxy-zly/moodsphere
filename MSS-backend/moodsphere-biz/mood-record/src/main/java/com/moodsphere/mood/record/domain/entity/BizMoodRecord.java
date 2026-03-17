package com.moodsphere.mood.record.domain.entity;

import java.math.BigDecimal;
import java.util.Date;
import com.moodsphere.common.core.domain.BaseEntity;


public class BizMoodRecord extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private Integer sourceType;

    private Integer recordType;

    private String contentText;

    private Integer voiceDuration;

    private Integer emotionIntensity;

    private Date recordTime;

    private Integer recordStatus;

    private Integer analyzeStatus;

    private Integer isPublic;

    private String province;

    private String city;

    private String district;

    private String locationName;

    private BigDecimal longitude;

    private BigDecimal latitude;

    private String deviceInfo;

    private Integer riskLevel;

    private Integer delFlag;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getUserId()
    {
        return userId;
    }

    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Integer getSourceType()
    {
        return sourceType;
    }

    public void setSourceType(Integer sourceType)
    {
        this.sourceType = sourceType;
    }

    public Integer getRecordType()
    {
        return recordType;
    }

    public void setRecordType(Integer recordType)
    {
        this.recordType = recordType;
    }

    public String getContentText()
    {
        return contentText;
    }

    public void setContentText(String contentText)
    {
        this.contentText = contentText;
    }

    public Integer getVoiceDuration()
    {
        return voiceDuration;
    }

    public void setVoiceDuration(Integer voiceDuration)
    {
        this.voiceDuration = voiceDuration;
    }

    public Integer getEmotionIntensity()
    {
        return emotionIntensity;
    }

    public void setEmotionIntensity(Integer emotionIntensity)
    {
        this.emotionIntensity = emotionIntensity;
    }

    public Date getRecordTime()
    {
        return recordTime;
    }

    public void setRecordTime(Date recordTime)
    {
        this.recordTime = recordTime;
    }

    public Integer getRecordStatus()
    {
        return recordStatus;
    }

    public void setRecordStatus(Integer recordStatus)
    {
        this.recordStatus = recordStatus;
    }

    public Integer getAnalyzeStatus()
    {
        return analyzeStatus;
    }

    public void setAnalyzeStatus(Integer analyzeStatus)
    {
        this.analyzeStatus = analyzeStatus;
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

    public String getDeviceInfo()
    {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo)
    {
        this.deviceInfo = deviceInfo;
    }

    public Integer getRiskLevel()
    {
        return riskLevel;
    }

    public void setRiskLevel(Integer riskLevel)
    {
        this.riskLevel = riskLevel;
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


