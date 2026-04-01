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
 * AI情绪分析控制器
 * 提供情绪分析相关的接口，包括执行分析和获取分析结果
 */
@RestController
@RequestMapping("/app/mood/analyze")
public class MoodAiAnalyzeController
{
    @Autowired
    private IMoodAiAnalyzeService moodAiAnalyzeService;

    /**
     * 执行AI情绪分析
     * 
     * @param body 请求体，包含记录ID
     * @return 分析结果
     */
    @PostMapping("/run")
    public AjaxResult run(@RequestBody(required = false) MoodAnalyzeRunBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodAiAnalyzeService.runAnalyze(recordId));
    }

    /**
     * 获取情绪分析结果
     * 
     * @param recordId 记录ID
     * @return 分析结果
     */
    @GetMapping("/result/{recordId}")
    public AjaxResult result(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodAiAnalyzeService.getAnalyzeResult(recordId));
    }
}


