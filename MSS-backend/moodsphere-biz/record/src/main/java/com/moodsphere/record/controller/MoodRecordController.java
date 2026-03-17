package com.moodsphere.record.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.record.service.IMoodRecordService;
@RestController
@RequestMapping("/app/mood/record")
public class MoodRecordController
{
    @Autowired
    private IMoodRecordService moodRecordService;

    @PostMapping("/create")
    public AjaxResult create(@RequestBody(required = false) MoodRecordCreateBody body)
    {
        Long recordId = moodRecordService.createRecord(body);
        return AjaxResult.success().put("recordId", recordId);
    }

    @GetMapping("/{recordId}")
    public AjaxResult detail(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodRecordService.getRecord(recordId));
    }

    @GetMapping("/latest")
    public AjaxResult latest()
    {
        return AjaxResult.success(moodRecordService.getLatestRecord());
    }

    @PostMapping("/{recordId}/submit")
    public AjaxResult submit(@PathVariable Long recordId)
    {
        moodRecordService.submitRecord(recordId);
        return AjaxResult.success();
    }
}

