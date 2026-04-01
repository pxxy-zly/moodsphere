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

/**
 * AI情绪分析服务实现类
 */
@Service
public class MoodAiAnalyzeServiceImpl implements IMoodAiAnalyzeService
{
    /** 分析状态：待分析 */
    private static final int ANALYZE_STATUS_PENDING = 0;

    /** 分析状态：分析成功 */
    private static final int ANALYZE_STATUS_SUCCESS = 1;

    /** 分析状态：分析失败 */
    private static final int ANALYZE_STATUS_FAIL = 2;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    /**
     * 执行AI情绪分析
     * 
     * @param recordId 记录ID
     * @return 分析结果
     */
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

    /**
     * 获取情绪分析结果
     * 
     * @param recordId 记录ID
     * @return 分析结果
     */
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

    /**
     * 构建结果VO
     * 
     * @param recordId 记录ID
     * @param analyzeStatus 分析状态
     * @param result 分析结果
     * @return 结果VO
     */
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
     * 
     * @param record 情绪记录
     * @return 分析结果
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

    /**
     * 检测情绪类型
     * 
     * @param text 文本内容
     * @return 情绪决策
     */
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

    /**
     * 检测场景
     * 
     * @param text 文本内容
     * @return 场景类型
     */
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

    /**
     * 构建情绪评分
     * 
     * @param decision 情绪决策
     * @param intensity 情绪强度
     * @return 情绪评分映射
     */
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

    /**
     * 构建分析摘要
     * 
     * @param decision 情绪决策
     * @param keywords 关键词
     * @return 分析摘要
     */
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
     * 
     * @param text 文本内容
     * @return 关键词
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

    /**
     * 检查文本是否包含指定关键词
     * 
     * @param text 文本内容
     * @param words 关键词列表
     * @return 是否包含
     */
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

    /**
     * 标准化强度比率
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
     * 安全处理风险级别
     * 
     * @param riskLevel 风险级别
     * @return 安全的风险级别
     */
    private int safeRisk(Integer riskLevel)
    {
        return riskLevel == null ? 0 : riskLevel;
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
     * 情绪决策类
     */
    private static class EmotionDecision
    {
        private final String primaryEmotion; // 主情绪
        private final String secondaryEmotion; // 次情绪
        private final Integer riskLevel; // 风险级别
        private final String riskReason; // 风险原因

        /**
         * 构造函数
         * 
         * @param primaryEmotion 主情绪
         * @param secondaryEmotion 次情绪
         * @param riskLevel 风险级别
         * @param riskReason 风险原因
         */
        private EmotionDecision(String primaryEmotion, String secondaryEmotion, Integer riskLevel, String riskReason)
        {
            this.primaryEmotion = primaryEmotion;
            this.secondaryEmotion = secondaryEmotion;
            this.riskLevel = riskLevel;
            this.riskReason = riskReason;
        }
    }
}