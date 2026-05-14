package com.moodsphere.analyze.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodsphere.analyze.domain.dto.MoodAnalyzeRunBody;
import com.moodsphere.analyze.service.IMoodAiAnalyzeService;
import com.moodsphere.common.core.domain.AjaxResult;

/**
 * AI 情绪分析任务控制器
 */
@RestController
@RequestMapping("/app/mood/analyze/tasks")
public class MoodAiAnalyzeTaskController
{
    @Autowired
    private IMoodAiAnalyzeService moodAiAnalyzeService;

    @PostMapping
    public AjaxResult createTask(@RequestBody(required = false) MoodAnalyzeRunBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodAiAnalyzeService.submitAnalyzeTask(recordId));
    }

    @GetMapping("/{taskId}")
    public AjaxResult getTask(@PathVariable Long taskId)
    {
        return AjaxResult.success(moodAiAnalyzeService.getAnalyzeTaskByTaskId(taskId));
    }
}
