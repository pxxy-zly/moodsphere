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

@RestController
@RequestMapping("/app/mood/vector")
public class MoodVectorController
{
    @Autowired
    private IMoodVectorService moodVectorService;

    @PostMapping("/build")
    public AjaxResult build(@RequestBody(required = false) MoodVectorBuildBody body)
    {
        Long recordId = body == null ? null : body.getRecordId();
        return AjaxResult.success(moodVectorService.buildVector(recordId));
    }

    @GetMapping("/{recordId}")
    public AjaxResult get(@PathVariable Long recordId)
    {
        return AjaxResult.success(moodVectorService.getVector(recordId));
    }
}


