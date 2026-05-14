package com.moodsphere.analyze.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 分析任务配置
 */
@Component
@ConfigurationProperties(prefix = "moodsphere.ai.task")
public class AiAnalyzeTaskProperties
{
    private int queuePollIntervalMs = 2000;

    private int taskLockSeconds = 120;

    private int staleRunningSeconds = 180;

    private int startupRecoveryBatchSize = 100;

    public int getQueuePollIntervalMs()
    {
        return queuePollIntervalMs;
    }

    public void setQueuePollIntervalMs(int queuePollIntervalMs)
    {
        this.queuePollIntervalMs = queuePollIntervalMs;
    }

    public int getTaskLockSeconds()
    {
        return taskLockSeconds;
    }

    public void setTaskLockSeconds(int taskLockSeconds)
    {
        this.taskLockSeconds = taskLockSeconds;
    }

    public int getStaleRunningSeconds()
    {
        return staleRunningSeconds;
    }

    public void setStaleRunningSeconds(int staleRunningSeconds)
    {
        this.staleRunningSeconds = staleRunningSeconds;
    }

    public int getStartupRecoveryBatchSize()
    {
        return startupRecoveryBatchSize;
    }

    public void setStartupRecoveryBatchSize(int startupRecoveryBatchSize)
    {
        this.startupRecoveryBatchSize = startupRecoveryBatchSize;
    }
}
