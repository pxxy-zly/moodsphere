package com.moodsphere.analyze.service.client;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.alibaba.fastjson2.JSON;
import com.moodsphere.analyze.config.PythonAiAnalyzeProperties;
import com.moodsphere.analyze.domain.dto.PythonAnalyzeRequest;
import com.moodsphere.analyze.domain.dto.PythonAnalyzeResponse;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;

/**
 * Python AI 分析客户端
 */
@Component
public class PythonMoodAnalyzeClient
{
    private static final Logger log = LoggerFactory.getLogger(PythonMoodAnalyzeClient.class);

    private final WebClient.Builder webClientBuilder;

    private final PythonAiAnalyzeProperties properties;

    public PythonMoodAnalyzeClient(WebClient.Builder webClientBuilder, PythonAiAnalyzeProperties properties)
    {
        this.webClientBuilder = webClientBuilder;
        this.properties = properties;
    }

    /**
     * 调用 Python AI 分析服务
     * 
     * @param record 情绪记录
     * @param userId 用户ID
     * @return 分析结果；若失败且允许回退则返回 null
     */
    public BizAiAnalysisResult analyze(BizMoodRecord record, Long userId)
    {
        if (!properties.isEnabled())
        {
            return null;
        }
        if (StringUtils.isEmpty(properties.getBaseUrl()))
        {
            throw new ServiceException("已启用Python AI分析，但未配置服务地址");
        }

        String traceId = UUID.randomUUID().toString().replace("-", "");
        long startMillis = System.currentTimeMillis();

        PythonAnalyzeRequest request = new PythonAnalyzeRequest();
        request.setRecordId(record.getId());
        request.setUserId(userId);
        request.setContentText(record.getContentText());
        request.setEmotionIntensity(record.getEmotionIntensity());
        request.setSourceType(record.getSourceType());
        request.setRecordTime(record.getRecordTime());
        request.setTraceId(traceId);

        try
        {
            WebClient webClient = webClientBuilder.baseUrl(properties.getBaseUrl()).build();
            PythonAnalyzeResponse response = webClient.post()
                    .uri(properties.getAnalyzePath())
                    .headers(headers -> fillHeaders(headers, traceId))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PythonAnalyzeResponse.class)
                    .block(Duration.ofMillis(properties.getTimeoutMs()));
            if (response == null || StringUtils.isEmpty(response.getPrimaryEmotion()))
            {
                throw new ServiceException("Python AI响应为空或缺少主情绪");
            }
            return toEntity(response, traceId, startMillis);
        }
        catch (Exception ex)
        {
            log.warn("Python AI调用失败, traceId={}, message={}", traceId, ex.getMessage());
            if (properties.isFallbackToMock())
            {
                return null;
            }
            throw new ServiceException("Python AI服务调用失败，请稍后重试");
        }
    }

    private void fillHeaders(HttpHeaders headers, String traceId)
    {
        headers.set("X-Request-Id", traceId);
        if (StringUtils.isNotEmpty(properties.getToken()))
        {
            headers.setBearerAuth(properties.getToken());
        }
    }

    private BizAiAnalysisResult toEntity(PythonAnalyzeResponse response, String traceId, long startMillis)
    {
        BizAiAnalysisResult result = new BizAiAnalysisResult();
        result.setPrimaryEmotion(response.getPrimaryEmotion());
        result.setSecondaryEmotion(response.getSecondaryEmotion());
        result.setEmotionKeywords(joinKeywords(response.getEmotionKeywords()));
        result.setEmotionScores(response.getEmotionScores() == null ? null : JSON.toJSONString(response.getEmotionScores()));
        result.setSceneRecognition(response.getSceneRecognition());
        result.setRiskLevel(response.getRiskLevel());
        result.setRiskReason(response.getRiskReason());
        result.setAiSummary(response.getAiSummary());
        result.setRawResponse(response.getRawResponse() == null ? JSON.toJSONString(response) : JSON.toJSONString(response.getRawResponse()));
        result.setProvider(response.getProvider());
        result.setModelName(response.getModelName());
        result.setModelVersion(response.getModelVersion());
        result.setPromptVersion(response.getPromptVersion());
        result.setRequestId(StringUtils.isNotEmpty(response.getRequestId()) ? response.getRequestId() : traceId);
        if (response.getAnalysisCostMs() != null && response.getAnalysisCostMs() > 0)
        {
            result.setAnalysisCostMs(response.getAnalysisCostMs());
        }
        else
        {
            result.setAnalysisCostMs((int) Math.max(1L, System.currentTimeMillis() - startMillis));
        }
        return result;
    }

    private String joinKeywords(List<String> keywords)
    {
        if (keywords == null || keywords.isEmpty())
        {
            return null;
        }
        return String.join("、", keywords);
    }
}
