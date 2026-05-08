package com.moodsphere.asset.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moodsphere.asset.service.IMoodAssetService;
import com.moodsphere.common.core.domain.AjaxResult;

@RestController
@RequestMapping("/app/mood/asset")
public class MoodAssetController
{
    @Autowired
    private IMoodAssetService moodAssetService;

    @PostMapping("/upload/image")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file)
    {
        return AjaxResult.success(moodAssetService.uploadImage(file));
    }

    @PostMapping("/upload/voice")
    public AjaxResult uploadVoice(@RequestParam("file") MultipartFile file,
            @RequestParam(value = "duration", required = false) Integer duration)
    {
        return AjaxResult.success(moodAssetService.uploadVoice(file, duration));
    }
}
