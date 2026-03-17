package com.moodsphere.mood.analyze.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.mood.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.mood.analyze.domain.vo.MoodAnalyzeResultVo;
import com.moodsphere.mood.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.mood.analyze.service.IMoodAiAnalyzeService;
import com.moodsphere.mood.record.domain.entity.BizMoodRecord;
import com.moodsphere.mood.record.mapper.BizMoodRecordMapper;

/**
 * AI分析服务实现（P0采用规则Mock）
 *
 * @author ruoyi
 */
@Service
public class MoodAiAnalyzeServiceImpl implements IMoodAiAnalyzeService
{
    /** 待分析 */
    private static final int ANALYZE_STATUS_PENDING = 0;

    /** 分析成功 */
    private static final int ANALYZE_STATUS_SUCCESS = 1;

    /** 分析失败 */
    private static final int ANALYZE_STATUS_FAIL = 2;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MoodAnalyzeResultVo runAnalyze(Long recordId)
    {
        checkRecordId(recordId);
        Long userId = SecurityUtils.getUserId();
        String username = SecurityUtils.getUsername();
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        if (StringUtils.isEmpty(record.getContentText()))
        {
            throw new ServiceException("记录内容为空，无法分析");
        }

        Date now = new Date();
        long startMillis = System.currentTimeMillis();
        try
        {
            BizAiAnalysisResult result = buildMockResult(record);
            result.setRecordId(recordId);
            result.setStatus(1);
            result.setDelFlag(0);
            result.setAnalysisAt(new Date());
            result.setAnalysisCostMs((int) Math.max(1L, System.currentTimeMillis() - startMillis));
            result.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            result.setProvider("mock-provider");
            result.setModelName("rule-engine-v1");
            result.setModelVersion("1.0.0");
            result.setPromptVersion("p0-mock-v1");
            result.setCreateBy(username);
            result.setCreateTime(now);
            result.setUpdateBy(username);
            result.setUpdateTime(now);

            bizAiAnalysisResultMapper.upsertBizAiAnalysisResult(result);
            bizMoodRecordMapper.updateAnalyzeResult(recordId, ANALYZE_STATUS_SUCCESS, result.getRiskLevel(), username, new Date());
            return buildResultVo(recordId, ANALYZE_STATUS_SUCCESS, bizAiAnalysisResultMapper.selectByRecordId(recordId));
        }
        catch (ServiceException e)
        {
            bizMoodRecordMapper.updateAnalyzeResult(recordId, ANALYZE_STATUS_FAIL, safeRisk(record.getRiskLevel()), username, new Date());
            throw e;
        }
        catch (Exception e)
        {
            bizMoodRecordMapper.updateAnalyzeResult(recordId, ANALYZE_STATUS_FAIL, safeRisk(record.getRiskLevel()), username, new Date());
            throw new ServiceException("AI分析失败，请稍后重试");
        }
    }

    @Override
    public MoodAnalyzeResultVo getAnalyzeResult(Long recordId)
    {
        checkRecordId(recordId);
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, SecurityUtils.getUserId());
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        BizAiAnalysisResult result = bizAiAnalysisResultMapper.selectByRecordId(recordId);
        Integer analyzeStatus = record.getAnalyzeStatus();
        if (analyzeStatus == null)
        {
            analyzeStatus = result == null ? ANALYZE_STATUS_PENDING : ANALYZE_STATUS_SUCCESS;
        }
        return buildResultVo(recordId, analyzeStatus, result);
    }

    private MoodAnalyzeResultVo buildResultVo(Long recordId, Integer analyzeStatus, BizAiAnalysisResult result)
    {
        MoodAnalyzeResultVo vo = new MoodAnalyzeResultVo();
        vo.setRecordId(recordId);
        vo.setAnalyzeStatus(analyzeStatus);
        vo.setResult(result);
        return vo;
    }

    private BizAiAnalysisResult buildMockResult(BizMoodRecord record)
    {
        String text = record.getContentText().trim();
        String normalized = text.toLowerCase(Locale.ROOT);
        EmotionDecision decision = detectEmotion(normalized);
        String keywords = extractKeywords(text);
        String scene = detectScene(normalized);
        String summary = buildSummary(decision, keywords);

        Map<String, Double> scoreMap = buildEmotionScores(decision, record.getEmotionIntensity());
        JSONObject raw = new JSONObject();
        raw.put("mode", "mock-rule");
        raw.put("primaryEmotion", decision.primaryEmotion);
        raw.put("secondaryEmotion", decision.secondaryEmotion);
        raw.put("riskLevel", decision.riskLevel);
        raw.put("scene", scene);
        raw.put("keywords", keywords);
        raw.put("scores", scoreMap);

        BizAiAnalysisResult result = new BizAiAnalysisResult();
        result.setPrimaryEmotion(decision.primaryEmotion);
        result.setSecondaryEmotion(decision.secondaryEmotion);
        result.setEmotionKeywords(keywords);
        result.setEmotionScores(JSON.toJSONString(scoreMap));
        result.setSceneRecognition(scene);
        result.setRiskLevel(decision.riskLevel);
        result.setRiskReason(decision.riskReason);
        result.setAiSummary(summary);
        result.setRawResponse(raw.toJSONString());
        return result;
    }

    private EmotionDecision detectEmotion(String text)
    {
        if (containsAny(text, "不想活", "结束生命", "自杀", "suicide", "kill myself"))
        {
            return new EmotionDecision("sad", "anxious", 3, "文本包含高风险表达");
        }
        if (containsAny(text, "焦虑", "紧张", "担心", "anxious", "panic"))
        {
            return new EmotionDecision("anxious", "tired", 1, "文本包含焦虑倾向");
        }
        if (containsAny(text, "伤心", "难过", "失落", "sad", "depressed"))
        {
            return new EmotionDecision("sad", "lonely", 1, "文本包含低落情绪");
        }
        if (containsAny(text, "生气", "愤怒", "烦躁", "angry", "mad"))
        {
            return new EmotionDecision("irritable", "anxious", 1, "文本包含激惹情绪");
        }
        if (containsAny(text, "开心", "快乐", "高兴", "happy", "great"))
        {
            return new EmotionDecision("happy", "calm", 0, "情绪整体积极");
        }
        return new EmotionDecision("calm", "confused", 0, "情绪整体平稳");
    }

    private String detectScene(String text)
    {
        if (containsAny(text, "工作", "加班", "开会", "work", "office"))
        {
            return "work";
        }
        if (containsAny(text, "学习", "考试", "作业", "study", "school"))
        {
            return "study";
        }
        if (containsAny(text, "家里", "家庭", "父母", "family"))
        {
            return "family";
        }
        if (containsAny(text, "恋爱", "感情", "对象", "love"))
        {
            return "love";
        }
        if (containsAny(text, "睡不着", "失眠", "睡觉", "sleep"))
        {
            return "sleep";
        }
        if (containsAny(text, "身体", "生病", "疼", "health"))
        {
            return "health";
        }
        return "general";
    }

    private Map<String, Double> buildEmotionScores(EmotionDecision decision, Integer intensity)
    {
        double intensityRate = normalizeIntensityRate(intensity);
        double primaryScore = round(0.58 + intensityRate * 0.34);
        double secondaryScore = round(0.36 + intensityRate * 0.24);
        double baseline = round(0.08 + (1 - intensityRate) * 0.10);

        Map<String, Double> scoreMap = new LinkedHashMap<>();
        scoreMap.put("happy", baseline);
        scoreMap.put("anxious", baseline);
        scoreMap.put("tired", baseline);
        scoreMap.put("calm", baseline);
        scoreMap.put("wronged", baseline);
        scoreMap.put("expect", baseline);
        scoreMap.put("lonely", baseline);
        scoreMap.put("irritable", baseline);
        scoreMap.put("sad", baseline);
        scoreMap.put("warm", baseline);
        scoreMap.put("confused", baseline);
        scoreMap.put("hopeful", baseline);
        scoreMap.put(decision.primaryEmotion, primaryScore);
        scoreMap.put(decision.secondaryEmotion, secondaryScore);
        return scoreMap;
    }

    private String buildSummary(EmotionDecision decision, String keywords)
    {
        StringBuilder summary = new StringBuilder();
        summary.append("当前主要情绪为").append(decision.primaryEmotion).append("，次级情绪为").append(decision.secondaryEmotion).append("。");
        if (StringUtils.isNotEmpty(keywords))
        {
            summary.append("关键词：").append(keywords).append("。");
        }
        if (decision.riskLevel != null && decision.riskLevel > 0)
        {
            summary.append("检测到轻度风险信号，建议关注自我状态并及时求助。");
        }
        else
        {
            summary.append("整体情绪风险较低。");
        }
        return summary.toString();
    }

    private String extractKeywords(String text)
    {
        String normalized = text.replaceAll("[\\r\\n\\t]", " ").replaceAll("[,，。.!！？;；:：]", " ");
        String[] items = normalized.split("\\s+");
        List<String> words = new ArrayList<>();
        for (String item : items)
        {
            if (StringUtils.isEmpty(item))
            {
                continue;
            }
            String word = item.trim();
            if (word.length() <= 1 || words.contains(word))
            {
                continue;
            }
            words.add(word);
            if (words.size() >= 5)
            {
                break;
            }
        }
        if (!words.isEmpty())
        {
            return String.join(",", words);
        }
        return text.length() <= 12 ? text : text.substring(0, 12);
    }

    private boolean containsAny(String text, String... words)
    {
        for (String word : words)
        {
            if (text.contains(word))
            {
                return true;
            }
        }
        return false;
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

    private double round(double value)
    {
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private int safeRisk(Integer riskLevel)
    {
        return riskLevel == null ? 0 : riskLevel;
    }

    private void checkRecordId(Long recordId)
    {
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
    }

    /**
     * 情绪识别结果
     */
    private static class EmotionDecision
    {
        private final String primaryEmotion;

        private final String secondaryEmotion;

        private final Integer riskLevel;

        private final String riskReason;

        private EmotionDecision(String primaryEmotion, String secondaryEmotion, Integer riskLevel, String riskReason)
        {
            this.primaryEmotion = primaryEmotion;
            this.secondaryEmotion = secondaryEmotion;
            this.riskLevel = riskLevel;
            this.riskReason = riskReason;
        }
    }
}
