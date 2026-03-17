package com.moodsphere.mood.vector.domain.entity;

import java.math.BigDecimal;
import com.moodsphere.common.core.domain.BaseEntity;


public class BizEmotionVector extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    private Long recordId;

    private BigDecimal valence;

    private BigDecimal arousal;

    private BigDecimal anxiety;

    private BigDecimal calmness;

    private BigDecimal loneliness;

    private BigDecimal fatigue;

    private BigDecimal anger;

    private BigDecimal hope;

    private BigDecimal confidence;

    private String dimensionJson;

    private BigDecimal confidenceScore;

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

    public BigDecimal getValence()
    {
        return valence;
    }

    public void setValence(BigDecimal valence)
    {
        this.valence = valence;
    }

    public BigDecimal getArousal()
    {
        return arousal;
    }

    public void setArousal(BigDecimal arousal)
    {
        this.arousal = arousal;
    }

    public BigDecimal getAnxiety()
    {
        return anxiety;
    }

    public void setAnxiety(BigDecimal anxiety)
    {
        this.anxiety = anxiety;
    }

    public BigDecimal getCalmness()
    {
        return calmness;
    }

    public void setCalmness(BigDecimal calmness)
    {
        this.calmness = calmness;
    }

    public BigDecimal getLoneliness()
    {
        return loneliness;
    }

    public void setLoneliness(BigDecimal loneliness)
    {
        this.loneliness = loneliness;
    }

    public BigDecimal getFatigue()
    {
        return fatigue;
    }

    public void setFatigue(BigDecimal fatigue)
    {
        this.fatigue = fatigue;
    }

    public BigDecimal getAnger()
    {
        return anger;
    }

    public void setAnger(BigDecimal anger)
    {
        this.anger = anger;
    }

    public BigDecimal getHope()
    {
        return hope;
    }

    public void setHope(BigDecimal hope)
    {
        this.hope = hope;
    }

    public BigDecimal getConfidence()
    {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence)
    {
        this.confidence = confidence;
    }

    public String getDimensionJson()
    {
        return dimensionJson;
    }

    public void setDimensionJson(String dimensionJson)
    {
        this.dimensionJson = dimensionJson;
    }

    public BigDecimal getConfidenceScore()
    {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore)
    {
        this.confidenceScore = confidenceScore;
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


