package com.moodsphere.mood.record.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.mood.record.domain.dto.MoodRecordCreateBody;
import com.moodsphere.mood.record.service.IMoodRecordService;

/**
 * 情绪记录控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/mood/record")
public class MoodRecordController
{
    @Autowired
    private IMoodRecordService moodRecordService;

    /**
     * 创建记录（文本优先）
     *
     * @param body 请求参数
     * @return 记录ID
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody(required = false) MoodRecordCreateBody body)
    {
        Long recordId = moodRecordService.createRecord(body);
        return AjaxResult.success().put("recordId", recordId);
    }

    /**
     * 记录详情
     *
     * @param recordId 记录ID
     * @return 详情
     */
    @GetMapping("/{recordId}")
    public AjaxResult detail(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodRecordService.getRecord(recordId));
    }

    /**
     * 最近一条记录
     *
     * @return 记录
     */
    @GetMapping("/latest")
    public AjaxResult latest()
    {
        return AjaxResult.success(moodRecordService.getLatestRecord());
    }

    /**
     * 提交记录并推进状态
     *
     * @param recordId 记录ID
     * @return 结果
     */
    @PostMapping("/{recordId}/submit")
    public AjaxResult submit(@PathVariable Long recordId)
    {
        moodRecordService.submitRecord(recordId);
        return AjaxResult.success();
    }
}
