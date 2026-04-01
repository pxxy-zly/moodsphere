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

/**
 * 情绪记录控制器
 * 提供情绪记录的创建、查询、提交等接口
 */
@RestController
@RequestMapping("/app/mood/record")
public class MoodRecordController
{
    @Autowired
    private IMoodRecordService moodRecordService;

    /**
     * 创建情绪记录
     * 
     * @param body 创建请求体
     * @return 创建结果，包含记录ID
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody(required = false) MoodRecordCreateBody body)
    {
        Long recordId = moodRecordService.createRecord(body);
        return AjaxResult.success().put("recordId", recordId);
    }

    /**
     * 获取记录详情
     * 
     * @param recordId 记录ID
     * @return 记录详情
     */
    @GetMapping("/{recordId}")
    public AjaxResult detail(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodRecordService.getRecord(recordId));
    }

    /**
     * 获取最新记录
     * 
     * @return 最新记录
     */
    @GetMapping("/latest")
    public AjaxResult latest()
    {
        return AjaxResult.success(moodRecordService.getLatestRecord());
    }

    /**
     * 提交记录
     * 
     * @param recordId 记录ID
     * @return 提交结果
     */
    @PostMapping("/{recordId}/submit")
    public AjaxResult submit(@PathVariable Long recordId)
    {
        moodRecordService.submitRecord(recordId);
        return AjaxResult.success();
    }
}

