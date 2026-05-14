package com.moodsphere.analyze.service;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import com.alibaba.fastjson2.JSON;
import com.moodsphere.analyze.config.AiAnalyzeTaskProperties;
import com.moodsphere.analyze.config.PythonAiAnalyzeProperties;
import com.moodsphere.analyze.constant.AnalyzeTaskStatusConstants;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.analyze.domain.entity.BizAiAnalyzeTask;
import com.moodsphere.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.analyze.mapper.BizAiAnalyzeTaskMapper;
import com.moodsphere.analyze.service.client.PythonMoodAnalyzeClient;
import com.moodsphere.asset.domain.entity.BizMoodAsset;
import com.moodsphere.asset.mapper.BizMoodAssetMapper;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;

/**
 * AI 分析任务执行器
 */
@Component
public class AnalyzeTaskExecutor
{
    private static final Logger log = LoggerFactory.getLogger(AnalyzeTaskExecutor.class);

    private static final int ANALYZE_STATUS_SUCCESS = 1;

    private static final int ANALYZE_STATUS_FAIL = 2;

    private static final String EXECUTE_LOCK_PREFIX = "mood:analyze:task:lock:";

    private final BizAiAnalyzeTaskMapper bizAiAnalyzeTaskMapper;

    private final BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    private final BizMoodRecordMapper bizMoodRecordMapper;

    private final BizMoodAssetMapper bizMoodAssetMapper;

    private final PythonMoodAnalyzeClient pythonMoodAnalyzeClient;

    private final TransactionTemplate transactionTemplate;

    private final RedisTemplate<Object, Object> redisTemplate;

    private final AiAnalyzeTaskProperties aiAnalyzeTaskProperties;

    private final PythonAiAnalyzeProperties pythonAiAnalyzeProperties;

    private final List<AnalyzeTaskPostProcessor> analyzeTaskPostProcessors;

    public AnalyzeTaskExecutor(BizAiAnalyzeTaskMapper bizAiAnalyzeTaskMapper, BizAiAnalysisResultMapper bizAiAnalysisResultMapper,
            BizMoodRecordMapper bizMoodRecordMapper, BizMoodAssetMapper bizMoodAssetMapper, PythonMoodAnalyzeClient pythonMoodAnalyzeClient,
            TransactionTemplate transactionTemplate, RedisTemplate<Object, Object> redisTemplate,
            AiAnalyzeTaskProperties aiAnalyzeTaskProperties, PythonAiAnalyzeProperties pythonAiAnalyzeProperties,
            List<AnalyzeTaskPostProcessor> analyzeTaskPostProcessors)
    {
        this.bizAiAnalyzeTaskMapper = bizAiAnalyzeTaskMapper;
        this.bizAiAnalysisResultMapper = bizAiAnalysisResultMapper;
        this.bizMoodRecordMapper = bizMoodRecordMapper;
        this.bizMoodAssetMapper = bizMoodAssetMapper;
        this.pythonMoodAnalyzeClient = pythonMoodAnalyzeClient;
        this.transactionTemplate = transactionTemplate;
        this.redisTemplate = redisTemplate;
        this.aiAnalyzeTaskProperties = aiAnalyzeTaskProperties;
        this.pythonAiAnalyzeProperties = pythonAiAnalyzeProperties;
        this.analyzeTaskPostProcessors = analyzeTaskPostProcessors;
    }

    public void executeTask(Long taskId)
    {
        if (taskId == null || taskId <= 0)
        {
            return;
        }
        String lockKey = EXECUTE_LOCK_PREFIX + taskId;
        if (!acquireLock(lockKey, taskId))
        {
            return;
        }
        try
        {
            int updated = transactionTemplate.execute(status -> {
                Date now = new Date();
                return bizAiAnalyzeTaskMapper.markRunning(taskId, AnalyzeTaskStatusConstants.QUEUED, AnalyzeTaskStatusConstants.RUNNING, now,
                        "system", now);
            });
            if (updated <= 0)
            {
                return;
            }

            BizAiAnalyzeTask task = bizAiAnalyzeTaskMapper.selectById(taskId);
            if (task == null)
            {
                return;
            }

            BizMoodRecord record = bizMoodRecordMapper.selectById(task.getRecordId());
            if (record == null || task.getUserId() == null || !task.getUserId().equals(record.getUserId()))
            {
                markTaskFail(task, 0, "INVALID_RECORD", "记录不存在、已删除或不属于当前用户");
                return;
            }

            List<BizMoodAsset> assets = bizMoodAssetMapper.selectByRecordId(task.getRecordId());
            if (!hasAnalyzeInput(record, assets))
            {
                markTaskFail(task, safeRisk(record.getRiskLevel()), "EMPTY_INPUT", "记录内容和素材都为空，无法分析");
                return;
            }

            long startMillis = System.currentTimeMillis();
            try
            {
                BizAiAnalysisResult result = pythonMoodAnalyzeClient.analyze(record, task.getUserId());
                if (result == null)
                {
                    throw new ServiceException("Python AI未返回结果");
                }
                Date now = new Date();
                fillResultMetadata(result, record.getId(), now, startMillis);

                BizAiAnalyzeTask successTask = new BizAiAnalyzeTask();
                successTask.setId(task.getId());
                successTask.setTaskStatus(AnalyzeTaskStatusConstants.SUCCESS);
                successTask.setResponseSnapshot(JSON.toJSONString(result));
                successTask.setProvider(result.getProvider());
                successTask.setModelName(result.getModelName());
                successTask.setModelVersion(result.getModelVersion());
                successTask.setPromptVersion(result.getPromptVersion());
                successTask.setRequestId(result.getRequestId());
                successTask.setAnalysisCostMs(result.getAnalysisCostMs());
                successTask.setFinishedAt(now);
                successTask.setUpdateBy("system");
                successTask.setUpdateTime(now);

                transactionTemplate.executeWithoutResult(status -> {
                    bizAiAnalysisResultMapper.upsertBizAiAnalysisResult(result);
                });

                executePostProcessors(record.getId(), task.getUserId(), "system");

                transactionTemplate.executeWithoutResult(status -> {
                    bizAiAnalyzeTaskMapper.markSuccess(successTask);
                    bizMoodRecordMapper.updateAnalyzeResult(record.getId(), ANALYZE_STATUS_SUCCESS, safeRisk(result.getRiskLevel()), "system",
                            now);
                });
            }
            catch (Exception ex)
            {
                log.warn("AI analyze task failed, taskId={}, recordId={}, message={}", taskId, record.getId(), ex.getMessage());
                markTaskFail(task, safeRisk(record.getRiskLevel()), classifyFailCode(ex), buildFailMessage(ex));
            }
        }
        finally
        {
            redisTemplate.delete(lockKey);
        }
    }

    private void markTaskFail(BizAiAnalyzeTask task, int riskLevel, String failCode, String failMessage)
    {
        Date now = new Date();
        BizAiAnalyzeTask failTask = new BizAiAnalyzeTask();
        failTask.setId(task.getId());
        failTask.setTaskStatus(AnalyzeTaskStatusConstants.FAIL);
        failTask.setFailCode(failCode);
        failTask.setFailMessage(failMessage);
        failTask.setRequestId(task.getRequestId());
        failTask.setAnalysisCostMs(null);
        failTask.setFinishedAt(now);
        failTask.setUpdateBy("system");
        failTask.setUpdateTime(now);
        transactionTemplate.executeWithoutResult(status -> {
            bizAiAnalyzeTaskMapper.markFail(failTask);
            bizMoodRecordMapper.updateAnalyzeResult(task.getRecordId(), ANALYZE_STATUS_FAIL, riskLevel, "system", now);
        });
    }

    private void fillResultMetadata(BizAiAnalysisResult result, Long recordId, Date now, long startMillis)
    {
        result.setRecordId(recordId);
        result.setStatus(ANALYZE_STATUS_SUCCESS);
        result.setDelFlag(0);
        result.setAnalysisAt(now);
        if (result.getAnalysisCostMs() == null || result.getAnalysisCostMs() <= 0)
        {
            result.setAnalysisCostMs((int) Math.max(1L, System.currentTimeMillis() - startMillis));
        }
        if (StringUtils.isEmpty(result.getRequestId()))
        {
            result.setRequestId(UUID.randomUUID().toString().replace("-", ""));
        }
        if (StringUtils.isEmpty(result.getProvider()))
        {
            result.setProvider("python-ai");
        }
        if (StringUtils.isEmpty(result.getModelName()))
        {
            result.setModelName("unknown-model");
        }
        if (StringUtils.isEmpty(result.getModelVersion()))
        {
            result.setModelVersion("unknown-version");
        }
        if (StringUtils.isEmpty(result.getPromptVersion()))
        {
            result.setPromptVersion("unknown-prompt");
        }
        result.setCreateBy("system");
        result.setCreateTime(now);
        result.setUpdateBy("system");
        result.setUpdateTime(now);
    }

    private boolean hasAnalyzeInput(BizMoodRecord record, List<BizMoodAsset> assets)
    {
        if (record != null && StringUtils.isNotEmpty(record.getContentText()))
        {
            return true;
        }
        if (assets == null)
        {
            return false;
        }
        for (BizMoodAsset asset : assets)
        {
            if (asset != null && StringUtils.isNotEmpty(asset.getFileUrl()))
            {
                return true;
            }
        }
        return false;
    }

    private boolean acquireLock(String lockKey, Long taskId)
    {
        int derivedSeconds = (int) Math.max(aiAnalyzeTaskProperties.getTaskLockSeconds(), pythonAiAnalyzeProperties.getTimeoutMs() / 1000L + 60L);
        Boolean success = redisTemplate.opsForValue().setIfAbsent(lockKey, String.valueOf(taskId), derivedSeconds, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    private String classifyFailCode(Exception ex)
    {
        if (ex == null)
        {
            return "SYSTEM_ERROR";
        }
        String message = String.valueOf(ex.getMessage()).toLowerCase(Locale.ROOT);
        if (message.contains("vector"))
        {
            return "VECTOR_BUILD_ERROR";
        }
        if (message.contains("weather"))
        {
            return "WEATHER_BUILD_ERROR";
        }
        if (message.contains("timeout") || message.contains("timed out"))
        {
            return "PYTHON_TIMEOUT";
        }
        if (message.contains("401") || message.contains("403") || message.contains("token"))
        {
            return "PYTHON_AUTH_ERROR";
        }
        if (message.contains("502") || message.contains("503") || message.contains("connection"))
        {
            return "PYTHON_SERVICE_ERROR";
        }
        if (ex instanceof ServiceException)
        {
            return "BIZ_ERROR";
        }
        return "SYSTEM_ERROR";
    }

    private String buildFailMessage(Exception ex)
    {
        String message = ex == null ? "AI分析失败，请稍后重试" : String.valueOf(ex.getMessage());
        if (StringUtils.isEmpty(message))
        {
            message = "AI分析失败，请稍后重试";
        }
        if (message.length() > 500)
        {
            return message.substring(0, 500);
        }
        return message;
    }

    private int safeRisk(Integer riskLevel)
    {
        return riskLevel == null ? 0 : riskLevel;
    }

    private void executePostProcessors(Long recordId, Long userId, String operator)
    {
        if (analyzeTaskPostProcessors == null || analyzeTaskPostProcessors.isEmpty())
        {
            return;
        }
        for (AnalyzeTaskPostProcessor postProcessor : analyzeTaskPostProcessors)
        {
            log.info("Run analyze post processor, recordId={}, userId={}, processor={}", recordId, userId, postProcessor.getName());
            postProcessor.process(recordId, userId, operator);
        }
    }
}
