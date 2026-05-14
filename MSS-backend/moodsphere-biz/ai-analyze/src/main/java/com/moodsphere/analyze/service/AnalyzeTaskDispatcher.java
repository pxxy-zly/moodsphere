package com.moodsphere.analyze.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * AI 分析任务投递器
 */
@Component
public class AnalyzeTaskDispatcher
{
    private final ThreadPoolTaskExecutor executor;

    private final AnalyzeTaskExecutor analyzeTaskExecutor;

    public AnalyzeTaskDispatcher(@Qualifier("threadPoolTaskExecutor") ThreadPoolTaskExecutor executor,
            AnalyzeTaskExecutor analyzeTaskExecutor)
    {
        this.executor = executor;
        this.analyzeTaskExecutor = analyzeTaskExecutor;
    }

    public void dispatch(Long taskId)
    {
        executor.execute(() -> analyzeTaskExecutor.executeTask(taskId));
    }
}
