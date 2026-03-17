package com.moodsphere.mood.vector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.mood.vector.domain.dto.MoodVectorBuildBody;
import com.moodsphere.mood.vector.service.IMoodVectorService;

/**
 * 向量控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/mood/vector")
public class MoodVectorController
{
    @Autowired
    private IMoodVectorService moodVectorService;

    /**
     * 构建向量
     *
     * @param body 请求参数
     * @return 向量结果
     */
    @PostMapping("/build")
    public AjaxResult build(@RequestBody(required = false) MoodVectorBuildBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodVectorService.buildVector(recordId));
    }

    /**
     * 查询向量
     *
     * @param recordId 记录ID
     * @return 向量结果
     */
    @GetMapping("/{recordId}")
    public AjaxResult get(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodVectorService.getVector(recordId));
    }
}
