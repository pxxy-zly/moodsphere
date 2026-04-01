package com.moodsphere.vector.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.vector.domain.dto.MoodVectorBuildBody;
import com.moodsphere.vector.service.IMoodVectorService;

/**
 * 情绪向量控制器
 * 提供情绪向量的构建和查询接口
 */
@RestController
@RequestMapping("/app/mood/vector")
public class MoodVectorController
{
    @Autowired
    private IMoodVectorService moodVectorService;

    /**
     * 构建情绪向量
     * 根据AI分析结果生成情绪向量
     * 
     * @param body 构建请求体
     * @return 构建结果
     */
    @PostMapping("/build")
    public AjaxResult build(@RequestBody(required = false) MoodVectorBuildBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodVectorService.buildVector(recordId));
    }

    /**
     * 获取情绪向量
     * 
     * @param recordId 记录ID
     * @return 情绪向量
     */
    @GetMapping("/{recordId}")
    public AjaxResult get(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodVectorService.getVector(recordId));
    }
}


