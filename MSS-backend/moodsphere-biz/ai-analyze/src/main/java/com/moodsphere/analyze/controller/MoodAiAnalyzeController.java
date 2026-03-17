package com.moodsphere.analyze.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.analyze.domain.dto.MoodAnalyzeRunBody;
import com.moodsphere.analyze.service.IMoodAiAnalyzeService;

@RestController
@RequestMapping("/app/mood/analyze")
public class MoodAiAnalyzeController
{
    @Autowired
    private IMoodAiAnalyzeService moodAiAnalyzeService;

    @PostMapping("/run")
    public AjaxResult run(@RequestBody(required = false) MoodAnalyzeRunBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodAiAnalyzeService.runAnalyze(recordId));
    }

    @GetMapping("/result/{recordId}")
    public AjaxResult result(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodAiAnalyzeService.getAnalyzeResult(recordId));
    }
}


