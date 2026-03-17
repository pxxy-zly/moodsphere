package com.moodsphere.mood.analyze.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.mood.analyze.domain.dto.MoodAnalyzeRunBody;
import com.moodsphere.mood.analyze.service.IMoodAiAnalyzeService;

/**
 * AI分析控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/mood/analyze")
public class MoodAiAnalyzeController
{
    @Autowired
    private IMoodAiAnalyzeService moodAiAnalyzeService;

    /**
     * 触发AI分析
     *
     * @param body 请求参数
     * @return 结果
     */
    @PostMapping("/run")
    public AjaxResult run(@RequestBody(required = false) MoodAnalyzeRunBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodAiAnalyzeService.runAnalyze(recordId));
    }

    /**
     * 查询分析结果
     *
     * @param recordId 记录ID
     * @return 结果
     */
    @GetMapping("/result/{recordId}")
    public AjaxResult result(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodAiAnalyzeService.getAnalyzeResult(recordId));
    }
}
