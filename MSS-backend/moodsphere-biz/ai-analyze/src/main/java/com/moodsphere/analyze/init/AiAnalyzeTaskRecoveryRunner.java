package com.moodsphere.analyze.init;

import java.util.Date;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.moodsphere.analyze.config.AiAnalyzeTaskProperties;
import com.moodsphere.analyze.constant.AnalyzeTaskStatusConstants;
import com.moodsphere.analyze.domain.entity.BizAiAnalyzeTask;
import com.moodsphere.analyze.mapper.BizAiAnalyzeTaskMapper;
import com.moodsphere.analyze.service.AnalyzeTaskDispatcher;

/**
 * 启动时恢复 AI 分析任务
 */
@Component
public class AiAnalyzeTaskRecoveryRunner implements ApplicationRunner
{
    private static final String SYSTEM_RECOVERY = "system-recovery";

    private final BizAiAnalyzeTaskMapper bizAiAnalyzeTaskMapper;

    private final AiAnalyzeTaskProperties taskProperties;

    private final AnalyzeTaskDispatcher analyzeTaskDispatcher;

    public AiAnalyzeTaskRecoveryRunner(BizAiAnalyzeTaskMapper bizAiAnalyzeTaskMapper, AiAnalyzeTaskProperties taskProperties,
            AnalyzeTaskDispatcher analyzeTaskDispatcher)
    {
        this.bizAiAnalyzeTaskMapper = bizAiAnalyzeTaskMapper;
        this.taskProperties = taskProperties;
        this.analyzeTaskDispatcher = analyzeTaskDispatcher;
    }

    @Override
    public void run(ApplicationArguments args)
    {
        Date now = new Date();
        Date deadline = new Date(now.getTime() - Math.max(60, taskProperties.getStaleRunningSeconds()) * 1000L);
        bizAiAnalyzeTaskMapper.markStaleRunningFailed(AnalyzeTaskStatusConstants.RUNNING, AnalyzeTaskStatusConstants.FAIL,
                "SYSTEM_RESTARTED", "任务执行期间服务重启，请重新发起分析", deadline, SYSTEM_RECOVERY, now);

        List<BizAiAnalyzeTask> queuedTasks = bizAiAnalyzeTaskMapper.selectQueuedTasks(taskProperties.getStartupRecoveryBatchSize());
        for (BizAiAnalyzeTask task : queuedTasks)
        {
            analyzeTaskDispatcher.dispatch(task.getId());
        }
    }
}
