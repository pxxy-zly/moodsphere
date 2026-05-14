package com.moodsphere.analyze.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.alibaba.fastjson2.JSON;
import com.moodsphere.analyze.config.AiAnalyzeTaskProperties;
import com.moodsphere.analyze.constant.AnalyzeTaskStatusConstants;
import com.moodsphere.analyze.domain.dto.PythonAnalyzeMediaAsset;
import com.moodsphere.analyze.domain.dto.PythonAnalyzeRequest;
import com.moodsphere.analyze.domain.entity.BizAiAnalysisResult;
import com.moodsphere.analyze.domain.entity.BizAiAnalyzeTask;
import com.moodsphere.analyze.domain.vo.MoodAnalyzeTaskVo;
import com.moodsphere.analyze.mapper.BizAiAnalysisResultMapper;
import com.moodsphere.analyze.mapper.BizAiAnalyzeTaskMapper;
import com.moodsphere.analyze.service.AnalyzeTaskDispatcher;
import com.moodsphere.analyze.service.IMoodAiAnalyzeService;
import com.moodsphere.asset.domain.entity.BizMoodAsset;
import com.moodsphere.asset.mapper.BizMoodAssetMapper;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.common.utils.uuid.IdUtils;
import com.moodsphere.record.domain.entity.BizMoodRecord;
import com.moodsphere.record.mapper.BizMoodRecordMapper;

/**
 * AI情绪分析服务实现类
 */
@Service
public class MoodAiAnalyzeServiceImpl implements IMoodAiAnalyzeService
{
    private static final int ANALYZE_STATUS_PENDING = 0;

    private static final int ANALYZE_STATUS_SUCCESS = 1;

    private static final int ANALYZE_STATUS_FAIL = 2;

    private static final String SUBMIT_LOCK_PREFIX = "mood:analyze:submit:";

    private static final int SUBMIT_LOCK_SECONDS = 10;

    @Autowired
    private BizMoodRecordMapper bizMoodRecordMapper;

    @Autowired
    private BizMoodAssetMapper bizMoodAssetMapper;

    @Autowired
    private BizAiAnalysisResultMapper bizAiAnalysisResultMapper;

    @Autowired
    private BizAiAnalyzeTaskMapper bizAiAnalyzeTaskMapper;

    @Autowired
    private AnalyzeTaskDispatcher analyzeTaskDispatcher;

    @Autowired
    private AiAnalyzeTaskProperties aiAnalyzeTaskProperties;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MoodAnalyzeTaskVo submitAnalyzeTask(Long recordId)
    {
        checkRecordId(recordId);
        Long userId = SecurityUtils.getUserId();
        String username = defaultUsername(SecurityUtils.getUsername());
        String submitLockKey = buildSubmitLockKey(userId, recordId);
        if (!acquireSubmitLock(submitLockKey))
        {
            throw new ServiceException("分析任务提交过于频繁，请稍后重试");
        }
        try
        {
            BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
            if (record == null)
            {
                throw new ServiceException("记录不存在或无权限");
            }
            List<BizMoodAsset> assets = loadAssets(recordId);
            validateAnalyzeInput(record, assets);

            BizAiAnalyzeTask activeTask = bizAiAnalyzeTaskMapper.selectActiveByRecordIdAndUserId(recordId, userId);
            if (activeTask != null)
            {
                return buildTaskVo(activeTask, null);
            }

            Date now = new Date();
            BizAiAnalyzeTask task = buildQueuedTask(record, assets, userId, username, now);
            bizAiAnalyzeTaskMapper.insertBizAiAnalyzeTask(task);
            bizMoodRecordMapper.markAnalyzeQueued(recordId, ANALYZE_STATUS_PENDING, username, now);
            registerDispatchAfterCommit(task.getId());
            return buildTaskVo(task, null);
        }
        finally
        {
            redisTemplate.delete(submitLockKey);
        }
    }

    @Override
    public MoodAnalyzeTaskVo getAnalyzeTaskByTaskId(Long taskId)
    {
        checkRecordId(taskId);
        BizAiAnalyzeTask task = bizAiAnalyzeTaskMapper.selectByIdAndUserId(taskId, SecurityUtils.getUserId());
        if (task == null)
        {
            throw new ServiceException("任务不存在或无权限");
        }
        BizAiAnalysisResult result = isTaskSuccess(task) ? bizAiAnalysisResultMapper.selectByRecordId(task.getRecordId()) : null;
        return buildTaskVo(task, result);
    }

    @Override
    public MoodAnalyzeTaskVo getAnalyzeTaskByRecordId(Long recordId)
    {
        checkRecordId(recordId);
        Long userId = SecurityUtils.getUserId();
        BizMoodRecord record = bizMoodRecordMapper.selectByIdAndUserId(recordId, userId);
        if (record == null)
        {
            throw new ServiceException("记录不存在或无权限");
        }

        BizAiAnalyzeTask latestTask = bizAiAnalyzeTaskMapper.selectLatestByRecordIdAndUserId(recordId, userId);
        if (latestTask != null)
        {
            BizAiAnalysisResult result = isTaskSuccess(latestTask) ? bizAiAnalysisResultMapper.selectByRecordId(recordId) : null;
            return buildTaskVo(latestTask, result);
        }

        BizAiAnalysisResult result = bizAiAnalysisResultMapper.selectByRecordId(recordId);
        return buildLegacyVo(recordId, record.getAnalyzeStatus(), result);
    }

    private BizAiAnalyzeTask buildQueuedTask(BizMoodRecord record, List<BizMoodAsset> assets, Long userId, String username, Date now)
    {
        BizAiAnalyzeTask task = new BizAiAnalyzeTask();
        task.setTaskNo(buildTaskNo());
        task.setRecordId(record.getId());
        task.setUserId(userId);
        task.setTaskStatus(AnalyzeTaskStatusConstants.QUEUED);
        task.setRetryCount(0);
        task.setRequestSnapshot(JSON.toJSONString(buildAnalyzeRequest(record, assets, userId, task.getTaskNo())));
        task.setQueuedAt(now);
        task.setCreateBy(username);
        task.setCreateTime(now);
        task.setUpdateBy(username);
        task.setUpdateTime(now);
        task.setDelFlag(0);
        return task;
    }

    private MoodAnalyzeTaskVo buildTaskVo(BizAiAnalyzeTask task, BizAiAnalysisResult result)
    {
        MoodAnalyzeTaskVo vo = new MoodAnalyzeTaskVo();
        vo.setTaskId(task.getId());
        vo.setTaskNo(task.getTaskNo());
        vo.setRecordId(task.getRecordId());
        vo.setTaskStatus(toTaskStatusName(task.getTaskStatus()));
        vo.setAnalyzeStatus(toAnalyzeStatus(task.getTaskStatus()));
        vo.setPollIntervalMs(aiAnalyzeTaskProperties.getQueuePollIntervalMs());
        vo.setResult(isTaskSuccess(task) ? result : null);
        vo.setErrorCode(task.getFailCode());
        vo.setErrorMessage(task.getFailMessage());
        vo.setRequestId(task.getRequestId());
        vo.setProvider(task.getProvider());
        vo.setModelName(task.getModelName());
        vo.setModelVersion(task.getModelVersion());
        vo.setPromptVersion(task.getPromptVersion());
        vo.setAnalysisCostMs(task.getAnalysisCostMs());
        vo.setQueuedAt(task.getQueuedAt());
        vo.setStartedAt(task.getStartedAt());
        vo.setFinishedAt(task.getFinishedAt());
        return vo;
    }

    private MoodAnalyzeTaskVo buildLegacyVo(Long recordId, Integer analyzeStatus, BizAiAnalysisResult result)
    {
        MoodAnalyzeTaskVo vo = new MoodAnalyzeTaskVo();
        vo.setRecordId(recordId);
        vo.setTaskStatus(toLegacyTaskStatusName(analyzeStatus, result));
        vo.setAnalyzeStatus(normalizeAnalyzeStatus(analyzeStatus, result));
        vo.setPollIntervalMs(aiAnalyzeTaskProperties.getQueuePollIntervalMs());
        vo.setResult(vo.getAnalyzeStatus() != null && vo.getAnalyzeStatus() == ANALYZE_STATUS_SUCCESS ? result : null);
        if (result != null)
        {
            vo.setRequestId(result.getRequestId());
            vo.setProvider(result.getProvider());
            vo.setModelName(result.getModelName());
            vo.setModelVersion(result.getModelVersion());
            vo.setPromptVersion(result.getPromptVersion());
            vo.setAnalysisCostMs(result.getAnalysisCostMs());
            vo.setFinishedAt(result.getAnalysisAt());
        }
        return vo;
    }

    private String buildTaskNo()
    {
        return "AAT" + IdUtils.fastSimpleUUID().substring(0, 29);
    }

    private PythonAnalyzeRequest buildAnalyzeRequest(BizMoodRecord record, List<BizMoodAsset> assets, Long userId, String traceId)
    {
        PythonAnalyzeRequest request = new PythonAnalyzeRequest();
        request.setRecordId(record.getId());
        request.setUserId(userId);
        request.setContentText(record.getContentText());
        request.setEmotionIntensity(record.getEmotionIntensity());
        request.setSourceType(record.getSourceType());
        request.setRecordTime(record.getRecordTime());
        request.setTraceId(traceId);
        request.setMediaAssets(toMediaAssets(assets));
        return request;
    }

    private List<PythonAnalyzeMediaAsset> toMediaAssets(List<BizMoodAsset> assets)
    {
        List<PythonAnalyzeMediaAsset> result = new ArrayList<>();
        if (assets == null)
        {
            return result;
        }
        for (BizMoodAsset asset : assets)
        {
            if (asset == null || StringUtils.isEmpty(asset.getFileUrl()))
            {
                continue;
            }
            PythonAnalyzeMediaAsset mediaAsset = new PythonAnalyzeMediaAsset();
            mediaAsset.setId(asset.getId());
            mediaAsset.setAssetType(asset.getAssetType());
            mediaAsset.setFileUrl(asset.getFileUrl());
            mediaAsset.setFileSize(asset.getFileSize());
            mediaAsset.setMimeType(asset.getMimeType());
            mediaAsset.setDuration(asset.getDuration());
            mediaAsset.setThumbnailUrl(asset.getThumbnailUrl());
            mediaAsset.setWidth(asset.getWidth());
            mediaAsset.setHeight(asset.getHeight());
            result.add(mediaAsset);
        }
        return result;
    }

    private List<BizMoodAsset> loadAssets(Long recordId)
    {
        List<BizMoodAsset> assets = bizMoodAssetMapper.selectByRecordId(recordId);
        return assets == null ? new ArrayList<>() : assets;
    }

    private void validateAnalyzeInput(BizMoodRecord record, List<BizMoodAsset> assets)
    {
        if (record == null)
        {
            throw new ServiceException("记录不存在");
        }
        if (StringUtils.isNotEmpty(record.getContentText()))
        {
            return;
        }
        for (BizMoodAsset asset : assets)
        {
            if (asset != null && StringUtils.isNotEmpty(asset.getFileUrl()))
            {
                return;
            }
        }
        throw new ServiceException("记录内容和素材都为空，无法分析");
    }

    private boolean acquireSubmitLock(String key)
    {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", SUBMIT_LOCK_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    private void registerDispatchAfterCommit(final Long taskId)
    {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization()
        {
            @Override
            public void afterCommit()
            {
                analyzeTaskDispatcher.dispatch(taskId);
            }
        });
    }

    private String buildSubmitLockKey(Long userId, Long recordId)
    {
        return SUBMIT_LOCK_PREFIX + userId + ":" + recordId;
    }

    private int toAnalyzeStatus(Integer taskStatus)
    {
        if (taskStatus == null)
        {
            return ANALYZE_STATUS_PENDING;
        }
        if (taskStatus == AnalyzeTaskStatusConstants.SUCCESS)
        {
            return ANALYZE_STATUS_SUCCESS;
        }
        if (taskStatus == AnalyzeTaskStatusConstants.FAIL)
        {
            return ANALYZE_STATUS_FAIL;
        }
        return ANALYZE_STATUS_PENDING;
    }

    private Integer normalizeAnalyzeStatus(Integer analyzeStatus, BizAiAnalysisResult result)
    {
        if (analyzeStatus != null)
        {
            return analyzeStatus;
        }
        return result == null ? ANALYZE_STATUS_PENDING : ANALYZE_STATUS_SUCCESS;
    }

    private String toLegacyTaskStatusName(Integer analyzeStatus, BizAiAnalysisResult result)
    {
        Integer normalized = normalizeAnalyzeStatus(analyzeStatus, result);
        if (normalized == ANALYZE_STATUS_SUCCESS)
        {
            return "SUCCESS";
        }
        if (normalized == ANALYZE_STATUS_FAIL)
        {
            return "FAIL";
        }
        return "PENDING";
    }

    private String toTaskStatusName(Integer taskStatus)
    {
        if (taskStatus == null)
        {
            return "PENDING";
        }
        if (taskStatus == AnalyzeTaskStatusConstants.QUEUED)
        {
            return "QUEUED";
        }
        if (taskStatus == AnalyzeTaskStatusConstants.RUNNING)
        {
            return "RUNNING";
        }
        if (taskStatus == AnalyzeTaskStatusConstants.SUCCESS)
        {
            return "SUCCESS";
        }
        if (taskStatus == AnalyzeTaskStatusConstants.FAIL)
        {
            return "FAIL";
        }
        return "PENDING";
    }

    private boolean isTaskSuccess(BizAiAnalyzeTask task)
    {
        return task != null && task.getTaskStatus() != null && task.getTaskStatus() == AnalyzeTaskStatusConstants.SUCCESS;
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
}
