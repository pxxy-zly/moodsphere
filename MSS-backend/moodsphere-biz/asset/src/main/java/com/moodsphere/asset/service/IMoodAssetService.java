package com.moodsphere.asset.service;

import java.util.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.moodsphere.asset.domain.vo.MoodAssetBindSummaryVo;
import com.moodsphere.asset.domain.vo.MoodAssetUploadVo;

public interface IMoodAssetService
{
    MoodAssetUploadVo uploadImage(MultipartFile file);

    MoodAssetUploadVo uploadVoice(MultipartFile file, Integer duration);

    MoodAssetBindSummaryVo summarizeAssets(List<Long> assetIds, String createBy);

    MoodAssetBindSummaryVo bindAssetsToRecord(Long recordId, List<Long> assetIds, String createBy, Date now);
}
