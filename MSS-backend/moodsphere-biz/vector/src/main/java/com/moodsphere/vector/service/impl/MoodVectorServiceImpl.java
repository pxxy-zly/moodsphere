package com.moodsphere.vector.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;
import com.moodsphere.vector.domain.entity.BizEmotionVector;
import com.moodsphere.vector.mapper.BizEmotionVectorMapper;
import com.moodsphere.vector.service.IMoodVectorService;
@Service
public class MoodVectorServiceImpl implements IMoodVectorService
{
    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    @Autowired
    private BizEmotionVectorMapper bizEmotionVectorMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizEmotionVector buildVector(Long recordId)
    {
        checkRecordId(recordId);
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, SecurityUtils.getUserId());
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        if (record.getAnalyzeStatus() != null && record.getAnalyzeStatus() == 2)
        {
            throw new ServiceException("AI分析失败，无法构建向量");
        }
        BizAiAnalysisResult analysisResult = bizAiAnalysisResultMapper.selectByRecordId(recordId);
        if (analysisResult == null)
        {
            throw new ServiceException("璇峰厛瀹屾垚AI鍒嗘瀽");
        }
        VectorTemplate template = templateByEmotion(analysisResult.getPrimaryEmotion());
        double intensityRate = normalizeIntensityRate(record.getEmotionIntensity());

        BizEmotionVector vector = new BizEmotionVector();
        vector.setRecordId(recordId);
        vector.setValence(dec(template.valence + (template.valence >= 0.5 ? 1 : -1) * (intensityRate - 0.5D) * 0.08D));
        vector.setArousal(dec(template.arousal * 0.75D + intensityRate * 0.25D));
        vector.setAnxiety(dec(template.anxiety * 0.8D + intensityRate * 0.2D * template.anxiety));
        vector.setCalmness(dec(template.calmness * 0.9D + (1D - intensityRate) * 0.1D));
        vector.setLoneliness(dec(template.loneliness));
        vector.setFatigue(dec(template.fatigue * 0.8D + intensityRate * 0.2D * 0.6D));
        vector.setAnger(dec(template.anger));
        vector.setHope(dec(template.hope));
        vector.setConfidence(dec(template.confidence));
        vector.setDimensionJson(buildDimensionJson(analysisResult, intensityRate));
        vector.setConfidenceScore(dec(calculateConfidenceScore(analysisResult.getRiskLevel())));
        vector.setDelFlag(0);
        vector.setCreateBy(SecurityUtils.getUsername());
        vector.setCreateTime(new Date());
        vector.setUpdateBy(SecurityUtils.getUsername());
        vector.setUpdateTime(new Date());

        bizEmotionVectorMapper.upsertBizEmotionVector(vector);
        return bizEmotionVectorMapper.selectByRecordId(recordId);
    }

    @Override
    public BizEmotionVector getVector(Long recordId)
    {
        checkRecordId(recordId);
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, SecurityUtils.getUserId());
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        return bizEmotionVectorMapper.selectByRecordId(recordId);
    }

    private String buildDimensionJson(BizAiAnalysisResult analysisResult, double intensityRate)
    {
        JSONObject object = new JSONObject();
        object.put("primaryEmotion", analysisResult.getPrimaryEmotion());
        object.put("secondaryEmotion", analysisResult.getSecondaryEmotion());
        object.put("intensityRate", round(intensityRate));
        object.put("source", "rule-v1");
        return object.toJSONString();
    }

    private VectorTemplate templateByEmotion(String primaryEmotion)
    {
        if (StringUtils.isEmpty(primaryEmotion))
        {
            return VectorTemplate.neutral();
        }
        switch (primaryEmotion)
        {
            case "happy":
                return new VectorTemplate(0.84D, 0.66D, 0.12D, 0.52D, 0.16D, 0.22D, 0.10D, 0.86D, 0.78D);
            case "anxious":
                return new VectorTemplate(0.28D, 0.84D, 0.82D, 0.18D, 0.42D, 0.54D, 0.36D, 0.26D, 0.34D);
            case "sad":
                return new VectorTemplate(0.20D, 0.42D, 0.56D, 0.22D, 0.78D, 0.58D, 0.22D, 0.22D, 0.30D);
            case "irritable":
                return new VectorTemplate(0.16D, 0.88D, 0.62D, 0.14D, 0.34D, 0.46D, 0.82D, 0.24D, 0.36D);
            case "lonely":
                return new VectorTemplate(0.22D, 0.36D, 0.48D, 0.28D, 0.88D, 0.56D, 0.18D, 0.28D, 0.28D);
            case "calm":
                return new VectorTemplate(0.64D, 0.24D, 0.14D, 0.90D, 0.24D, 0.30D, 0.08D, 0.68D, 0.72D);
            default:
                return VectorTemplate.neutral();
        }
    }

    private double calculateConfidenceScore(Integer riskLevel)
    {
        int risk = riskLevel == null ? 0 : Math.max(0, Math.min(3, riskLevel));
        return Math.max(0.35D, 0.92D - risk * 0.18D);
    }

    private double normalizeIntensityRate(Integer intensity)
    {
        if (intensity == null)
        {
            return 0.5D;
        }
        double value = Math.max(1D, Math.min(10D, intensity));
        return value / 10D;
    }

    private BigDecimal dec(double value)
    {
        return BigDecimal.valueOf(round(Math.max(0D, Math.min(1D, value))));
    }

    private double round(double value)
    {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }
    private static class VectorTemplate
    {
        private final double valence;

        private final double arousal;

        private final double anxiety;

        private final double calmness;

        private final double loneliness;

        private final double fatigue;

        private final double anger;

        private final double hope;

        private final double confidence;

        private VectorTemplate(double valence, double arousal, double anxiety, double calmness, double loneliness, double fatigue,
                double anger, double hope, double confidence)
        {
            this.valence = valence;
            this.arousal = arousal;
            this.anxiety = anxiety;
            this.calmness = calmness;
            this.loneliness = loneliness;
            this.fatigue = fatigue;
            this.anger = anger;
            this.hope = hope;
            this.confidence = confidence;
        }

        private static VectorTemplate neutral()
        {
            return new VectorTemplate(0.50D, 0.50D, 0.40D, 0.45D, 0.40D, 0.45D, 0.30D, 0.45D, 0.50D);
        }
    }
}

