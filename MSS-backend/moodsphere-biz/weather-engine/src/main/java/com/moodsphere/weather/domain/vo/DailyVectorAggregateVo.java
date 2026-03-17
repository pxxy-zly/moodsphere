package com.moodsphere.weather.domain.vo;

import java.math.BigDecimal;


public class DailyVectorAggregateVo
{
    private BigDecimal avgValence;

    private BigDecimal avgArousal;

    private BigDecimal avgAnxiety;

    private BigDecimal avgCalmness;

    private BigDecimal avgLoneliness;

    private BigDecimal avgFatigue;

    private BigDecimal avgAnger;

    private BigDecimal avgHope;

    private BigDecimal avgConfidence;

    private Integer recordCount;

    public BigDecimal getAvgValence()
    {
        return avgValence;
    }

    public void setAvgValence(BigDecimal avgValence)
    {
        this.avgValence = avgValence;
    }

    public BigDecimal getAvgArousal()
    {
        return avgArousal;
    }

    public void setAvgArousal(BigDecimal avgArousal)
    {
        this.avgArousal = avgArousal;
    }

    public BigDecimal getAvgAnxiety()
    {
        return avgAnxiety;
    }

    public void setAvgAnxiety(BigDecimal avgAnxiety)
    {
        this.avgAnxiety = avgAnxiety;
    }

    public BigDecimal getAvgCalmness()
    {
        return avgCalmness;
    }

    public void setAvgCalmness(BigDecimal avgCalmness)
    {
        this.avgCalmness = avgCalmness;
    }

    public BigDecimal getAvgLoneliness()
    {
        return avgLoneliness;
    }

    public void setAvgLoneliness(BigDecimal avgLoneliness)
    {
        this.avgLoneliness = avgLoneliness;
    }

    public BigDecimal getAvgFatigue()
    {
        return avgFatigue;
    }

    public void setAvgFatigue(BigDecimal avgFatigue)
    {
        this.avgFatigue = avgFatigue;
    }

    public BigDecimal getAvgAnger()
    {
        return avgAnger;
    }

    public void setAvgAnger(BigDecimal avgAnger)
    {
        this.avgAnger = avgAnger;
    }

    public BigDecimal getAvgHope()
    {
        return avgHope;
    }

    public void setAvgHope(BigDecimal avgHope)
    {
        this.avgHope = avgHope;
    }

    public BigDecimal getAvgConfidence()
    {
        return avgConfidence;
    }

    public void setAvgConfidence(BigDecimal avgConfidence)
    {
        this.avgConfidence = avgConfidence;
    }

    public Integer getRecordCount()
    {
        return recordCount;
    }

    public void setRecordCount(Integer recordCount)
    {
        this.recordCount = recordCount;
    }
}



