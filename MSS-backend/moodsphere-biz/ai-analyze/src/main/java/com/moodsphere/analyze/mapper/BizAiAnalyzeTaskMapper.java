package com.moodsphere.analyze.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.moodsphere.analyze.domain.entity.BizAiAnalyzeTask;

public interface BizAiAnalyzeTaskMapper
{
    int insertBizAiAnalyzeTask(BizAiAnalyzeTask task);

    BizAiAnalyzeTask selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    BizAiAnalyzeTask selectLatestByRecordIdAndUserId(@Param("recordId") Long recordId, @Param("userId") Long userId);

    BizAiAnalyzeTask selectActiveByRecordIdAndUserId(@Param("recordId") Long recordId, @Param("userId") Long userId);

    BizAiAnalyzeTask selectById(@Param("id") Long id);

    int markRunning(@Param("id") Long id, @Param("queuedStatus") Integer queuedStatus, @Param("runningStatus") Integer runningStatus,
            @Param("startedAt") Date startedAt, @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);

    int markSuccess(BizAiAnalyzeTask task);

    int markFail(BizAiAnalyzeTask task);

    List<BizAiAnalyzeTask> selectQueuedTasks(@Param("limit") Integer limit);

    int markStaleRunningFailed(@Param("runningStatus") Integer runningStatus, @Param("failStatus") Integer failStatus,
            @Param("failCode") String failCode, @Param("failMessage") String failMessage, @Param("deadline") Date deadline,
            @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);
}
