package com.moodsphere.system.user.domain.entity;

import java.util.Date;
import com.moodsphere.common.core.domain.BaseEntity;

public class BizUserInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long userId;

    private String nickName;

    private String avatar;

    private Integer gender;

    private Date birthday;

    private String signature;

    private Integer emotionLevel;

    private Integer recordDays;

    private Integer totalRecords;

    private Integer anonymousProjection;

    private Date lastRecordTime;

    private String delFlag;

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

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getAvatar()
    {
        return avatar;
    }

    public void setAvatar(String avatar)
    {
        this.avatar = avatar;
    }

    public Integer getGender()
    {
        return gender;
    }

    public void setGender(Integer gender)
    {
        this.gender = gender;
    }

    public Date getBirthday()
    {
        return birthday;
    }

    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    public String getSignature()
    {
        return signature;
    }

    public void setSignature(String signature)
    {
        this.signature = signature;
    }

    public Integer getEmotionLevel()
    {
        return emotionLevel;
    }

    public void setEmotionLevel(Integer emotionLevel)
    {
        this.emotionLevel = emotionLevel;
    }

    public Integer getRecordDays()
    {
        return recordDays;
    }

    public void setRecordDays(Integer recordDays)
    {
        this.recordDays = recordDays;
    }

    public Integer getTotalRecords()
    {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords)
    {
        this.totalRecords = totalRecords;
    }

    public Integer getAnonymousProjection()
    {
        return anonymousProjection;
    }

    public void setAnonymousProjection(Integer anonymousProjection)
    {
        this.anonymousProjection = anonymousProjection;
    }

    public Date getLastRecordTime()
    {
        return lastRecordTime;
    }

    public void setLastRecordTime(Date lastRecordTime)
    {
        this.lastRecordTime = lastRecordTime;
    }

    public String getDelFlag()
    {
        return delFlag;
    }

    public void setDelFlag(String delFlag)
    {
        this.delFlag = delFlag;
    }
}
