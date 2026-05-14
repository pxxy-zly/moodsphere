package com.moodsphere.vector.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;
import com.moodsphere.vector.domain.entity.BizEmotionVector;
import com.moodsphere.vector.mapper.BizEmotionVectorMapper;
import com.moodsphere.vector.service.IMoodVectorService;

/**
 * 情绪向量服务实现类
 */
@Service
public class MoodVectorServiceImpl implements IMoodVectorService
{
    /** 分析状态：分析失败 */
    private static final int ANALYZE_STATUS_FAIL = 2;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    @Autowired
    private BizEmotionVectorMapper bizEmotionVectorMapper;

    /**
     * 构建情绪向量
     * 根据AI分析结果生成情绪向量
     * 
     * @param recordId 记录ID
     * @return 情绪向量实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizEmotionVector buildVector(Long recordId)
    {
        return buildVectorForUser(recordId, SecurityUtils.getUserId(), defaultUsername(SecurityUtils.getUsername()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BizEmotionVector buildVectorForUser(Long recordId, Long userId, String username)
    {
        checkRecordId(recordId);
        String operator = defaultUsername(username);

        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        if (record.getAnalyzeStatus() != null && record.getAnalyzeStatus() == ANALYZE_STATUS_FAIL)
        {
            throw new ServiceException("AI分析失败，无法构建向量");
        }

        BizAiAnalysisResult analysisResult = bizAiAnalysisResultMapper.selectByRecordId(recordId);
        if (analysisResult == null)
        {
            throw new ServiceException("请先完成AI分析");
        }

        VectorTemplate template = templateByEmotion(analysisResult.getPrimaryEmotion());
        double intensityRate = normalizeIntensityRate(record.getEmotionIntensity());

        Date now = new Date();
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
        vector.setCreateBy(operator);
        vector.setCreateTime(now);
        vector.setUpdateBy(operator);
        vector.setUpdateTime(now);

        bizEmotionVectorMapper.upsertBizEmotionVector(vector);
        BizEmotionVector latest = bizEmotionVectorMapper.selectByRecordId(recordId);
        if (latest == null)
        {
            throw new ServiceException("情绪向量生成失败");
        }
        return latest;
    }

    /**
     * 获取情绪向量
     * 
     * @param recordId 记录ID
     * @return 情绪向量实体
     */
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

    /**
     * 构建维度JSON
     * 
     * @param analysisResult AI分析结果
     * @param intensityRate 情绪强度比率
     * @return 维度JSON字符串
     */
    private String buildDimensionJson(BizAiAnalysisResult analysisResult, double intensityRate)
    {
        JSONObject object = new JSONObject();
        object.put("primaryEmotion", analysisResult.getPrimaryEmotion());
        object.put("secondaryEmotion", analysisResult.getSecondaryEmotion());
        object.put("intensityRate", round(intensityRate));
        object.put("source", "rule-v1");
        return object.toJSONString();
    }

    /**
     * 根据主情绪获取向量模板
     * 
     * @param primaryEmotion 主情绪
     * @return 向量模板
     */
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

    /**
     * 计算置信度分数
     * 
     * @param riskLevel 风险级别
     * @return 置信度分数
     */
    private double calculateConfidenceScore(Integer riskLevel)
    {
        int risk = riskLevel == null ? 0 : Math.max(0, Math.min(3, riskLevel));
        return Math.max(0.35D, 0.92D - risk * 0.18D);
    }

    /**
     * 标准化情绪强度比率
     * 
     * @param intensity 情绪强度
     * @return 标准化后的比率
     */
    private double normalizeIntensityRate(Integer intensity)
    {
        if (intensity == null)
        {
            return 0.5D;
        }
        double value = Math.max(1D, Math.min(10D, intensity));
        return value / 10D;
    }

    /**
     * 将double转换为BigDecimal
     * 
     * @param value 原始值
     * @return BigDecimal值
     */
    private BigDecimal dec(double value)
    {
        return BigDecimal.valueOf(round(Math.max(0D, Math.min(1D, value))));
    }

    /**
     * 四舍五入到指定小数位
     * 
     * @param value 原始值
     * @return 四舍五入后的值
     */
    private double round(double value)
    {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 检查记录ID
     * 
     * @param recordId 记录ID
     */
    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }

    /**
     * 获取默认用户名
     * 
     * @param username 用户名
     * @return 默认用户名
     */
    private String defaultUsername(String username)
    {
        return StringUtils.isEmpty(username) ? "system" : username;
    }

    /**
     * 情绪向量模板内部类
     */
    private static class VectorTemplate
    {
        private final double valence; // 效价
        private final double arousal; // 唤醒度
        private final double anxiety; // 焦虑
        private final double calmness; // 平静度
        private final double loneliness; // 孤独感
        private final double fatigue; // 疲劳度
        private final double anger; // 愤怒
        private final double hope; // 希望
        private final double confidence; // 自信

        /**
         * 构造函数
         * 
         * @param valence 效价
         * @param arousal 唤醒度
         * @param anxiety 焦虑
         * @param calmness 平静度
         * @param loneliness 孤独感
         * @param fatigue 疲劳度
         * @param anger 愤怒
         * @param hope 希望
         * @param confidence 自信
         */
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

        /**
         * 获取中性情绪模板
         * 
         * @return 中性情绪向量模板
         */
        private static VectorTemplate neutral()
        {
            return new VectorTemplate(0.50D, 0.50D, 0.40D, 0.45D, 0.40D, 0.45D, 0.30D, 0.45D, 0.50D);
        }
    }
}
