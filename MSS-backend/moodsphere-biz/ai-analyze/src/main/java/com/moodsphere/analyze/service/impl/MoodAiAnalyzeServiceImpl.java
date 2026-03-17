package com.moodsphere.analyze.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.analyze.domain.vo.MoodAnalyzeResultVo;
import com.moodsphere.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.analyze.service.IMoodAiAnalyzeService;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;

@Service
public class MoodAiAnalyzeServiceImpl implements IMoodAiAnalyzeService
{
    private static final int ANALYZE_STATUS_PENDING = 0;

    private static final int ANALYZE_STATUS_SUCCESS = 1;

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
        String username = defaultUsername(SecurityUtils.getUsername());
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }
        if (StringUtils.isEmpty(record.getContentText()))
        {
            throw new ServiceException("记录内容为空，无法分析");
        }

        long startMillis = System.currentTimeMillis();
        try
        {
            BizAiAnalysisResult result = buildMockResult(record);
            Date now = new Date();
            result.setRecordId(recordId);
            result.setStatus(1);
            result.setDelFlag(0);
            result.setAnalysisAt(now);
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
            bizMoodRecordMapper.updateAnalyzeResult(recordId, ANALYZE_STATUS_SUCCESS, result.getRiskLevel(), username, now);

            BizAiAnalysisResult latest = bizAiAnalysisResultMapper.selectByRecordId(recordId);
            return buildResultVo(recordId, ANALYZE_STATUS_SUCCESS, latest == null ? result : latest);
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

    /**
     * 当前阶段先用规则引擎模拟 AI 返回，保证链路可联调。
     */
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
        if (containsAny(text, "suicide", "kill myself", "self harm", "自杀", "想死", "轻生", "伤害自己"))
        {
            return new EmotionDecision("sad", "anxious", 3, "检测到高风险表达");
        }
        if (containsAny(text, "anxious", "panic", "nervous", "worry", "焦虑", "紧张", "担心", "恐慌"))
        {
            return new EmotionDecision("anxious", "tired", 1, "检测到焦虑倾向");
        }
        if (containsAny(text, "sad", "depressed", "down", "难过", "低落", "抑郁", "伤心"))
        {
            return new EmotionDecision("sad", "lonely", 1, "检测到低落倾向");
        }
        if (containsAny(text, "angry", "mad", "irritable", "生气", "烦躁", "愤怒"))
        {
            return new EmotionDecision("irritable", "anxious", 1, "检测到烦躁倾向");
        }
        if (containsAny(text, "happy", "great", "good", "开心", "愉快", "高兴"))
        {
            return new EmotionDecision("happy", "calm", 0, "积极情绪");
        }
        return new EmotionDecision("calm", "confused", 0, "情绪整体平稳");
    }

    private String detectScene(String text)
    {
        if (containsAny(text, "work", "office", "meeting", "工作", "公司", "开会"))
        {
            return "work";
        }
        if (containsAny(text, "study", "school", "exam", "学习", "学校", "考试"))
        {
            return "study";
        }
        if (containsAny(text, "family", "parent", "home", "家庭", "父母", "家里"))
        {
            return "family";
        }
        if (containsAny(text, "love", "relationship", "感情", "恋爱"))
        {
            return "love";
        }
        if (containsAny(text, "sleep", "insomnia", "睡眠", "失眠"))
        {
            return "sleep";
        }
        if (containsAny(text, "health", "sick", "illness", "健康", "生病"))
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
        summary.append("主情绪为").append(decision.primaryEmotion)
                .append("，次情绪为").append(decision.secondaryEmotion).append("。");
        if (StringUtils.isNotEmpty(keywords))
        {
            summary.append(" 关键词：").append(keywords).append("。");
        }
        if (decision.riskLevel != null && decision.riskLevel > 0)
        {
            summary.append(" 检测到潜在风险信号，建议及时关注自身状态并寻求帮助。");
        }
        else
        {
            summary.append(" 整体情绪风险较低。");
        }
        return summary.toString();
    }

    /**
     * 兼容中英文标点，提取最多 5 个关键词。
     */
    private String extractKeywords(String text)
    {
        String normalized = text.replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("[,?!;:，。！？；：、]", " ");
        String[] items = normalized.split("\\s+");
        List<String> words = new ArrayList<>();
        Set<String> deduplicate = new LinkedHashSet<>();
        for (String item : items)
        {
            if (StringUtils.isEmpty(item))
            {
                continue;
            }
            String word = item.trim();
            if (word.length() <= 1 || deduplicate.contains(word))
            {
                continue;
            }
            deduplicate.add(word);
            words.add(word.length() > 12 ? word.substring(0, 12) : word);
            if (words.size() >= 5)
            {
                break;
            }
        }
        if (!words.isEmpty())
        {
            return String.join("、", words);
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

    private String defaultUsername(String username)
    {
        return StringUtils.isEmpty(username) ? "system" : username;
    }

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