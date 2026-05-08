package com.moodsphere.asset.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.moodsphere.asset.domain.entity.BizMoodAsset;
import com.moodsphere.asset.domain.vo.MoodAssetBindSummaryVo;
import com.moodsphere.asset.domain.vo.MoodAssetUploadVo;
import com.moodsphere.asset.mapper.BizMoodAssetMapper;
import com.moodsphere.asset.service.IMoodAssetService;
import com.moodsphere.common.config.RuoYiConfig;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.common.utils.file.FileUploadUtils;
import com.moodsphere.common.utils.file.MimeTypeUtils;
import com.moodsphere.framework.config.ServerConfig;

@Service
public class MoodAssetServiceImpl implements IMoodAssetService
{
    private static final int ASSET_TYPE_IMAGE = 1;

    private static final int ASSET_TYPE_VOICE = 2;

    private static final int RECORD_ID_UNBOUND = 0;

    private static final int STATUS_ENABLED = 1;

    private static final int DEL_FLAG_NORMAL = 0;

    private static final String[] VOICE_ALLOWED_EXTENSION = { "mp3", "wav", "m4a", "aac", "wma" };

    @Autowired
    private BizMoodAssetMapper bizMoodAssetMapper;

    @Autowired
    private ServerConfig serverConfig;

    @Override
    public MoodAssetUploadVo uploadImage(MultipartFile file)
    {
        return uploadAsset(file, null, ASSET_TYPE_IMAGE, MimeTypeUtils.IMAGE_EXTENSION);
    }

    @Override
    public MoodAssetUploadVo uploadVoice(MultipartFile file, Integer duration)
    {
        int normalizedDuration = duration == null || duration < 0 ? 0 : duration;
        return uploadAsset(file, normalizedDuration, ASSET_TYPE_VOICE, VOICE_ALLOWED_EXTENSION);
    }

    @Override
    public MoodAssetBindSummaryVo summarizeAssets(List<Long> assetIds, String createBy)
    {
        MoodAssetBindSummaryVo summary = new MoodAssetBindSummaryVo();
        if (assetIds == null || assetIds.isEmpty())
        {
            return summary;
        }
        List<BizMoodAsset> assets = bizMoodAssetMapper.selectUnboundByIdsAndCreateBy(assetIds, createBy);
        if (assets == null || assets.size() != assetIds.size())
        {
            throw new ServiceException("素材不存在、已绑定或无权限");
        }
        fillSummary(summary, assets);
        return summary;
    }

    @Override
    public MoodAssetBindSummaryVo bindAssetsToRecord(Long recordId, List<Long> assetIds, String createBy, Date now)
    {
        MoodAssetBindSummaryVo summary = new MoodAssetBindSummaryVo();
        if (assetIds == null || assetIds.isEmpty())
        {
            return summary;
        }
        if (recordId == null || recordId <= 0)
        {
            throw new ServiceException("recordId无效");
        }
        if (StringUtils.isEmpty(createBy))
        {
            throw new ServiceException("上传用户信息缺失");
        }

        List<BizMoodAsset> assets = bizMoodAssetMapper.selectUnboundByIdsAndCreateBy(assetIds, createBy);
        if (assets == null || assets.size() != assetIds.size())
        {
            throw new ServiceException("素材不存在、已绑定或无权限");
        }
        fillSummary(summary, assets);

        int rows = bizMoodAssetMapper.bindAssetsToRecord(recordId, assetIds, createBy, createBy, now);
        if (rows != assetIds.size())
        {
            throw new ServiceException("素材绑定失败，请稍后重试");
        }
        return summary;
    }

    private MoodAssetUploadVo uploadAsset(MultipartFile file, Integer duration, int assetType, String[] allowedExtension)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("上传文件不能为空");
        }

        String username = defaultUsername(SecurityUtils.getUsername());
        Date now = new Date();
        String fileName;
        try
        {
            fileName = FileUploadUtils.upload(RuoYiConfig.getUploadPath(), file, allowedExtension);
        }
        catch (Exception e)
        {
            throw new ServiceException(e.getMessage());
        }

        String fileUrl = serverConfig.getUrl() + fileName;
        BizMoodAsset asset = new BizMoodAsset();
        asset.setRecordId((long) RECORD_ID_UNBOUND);
        asset.setAssetType(assetType);
        asset.setFileUrl(fileUrl);
        asset.setFileSize(file.getSize());
        asset.setMimeType(file.getContentType());
        asset.setDuration(assetType == ASSET_TYPE_VOICE ? duration : null);
        asset.setThumbnailUrl(assetType == ASSET_TYPE_IMAGE ? fileUrl : null);
        asset.setStatus(STATUS_ENABLED);
        asset.setDelFlag(DEL_FLAG_NORMAL);
        asset.setCreateBy(username);
        asset.setCreateTime(now);
        asset.setUpdateBy(username);
        asset.setUpdateTime(now);
        asset.setRemark(file.getOriginalFilename());

        int rows = bizMoodAssetMapper.insertBizMoodAsset(asset);
        if (rows <= 0 || asset.getId() == null)
        {
            throw new ServiceException("素材保存失败");
        }

        MoodAssetUploadVo vo = new MoodAssetUploadVo();
        vo.setAssetId(asset.getId());
        vo.setFileUrl(fileUrl);
        vo.setFileName(fileName);
        vo.setOriginalFileName(file.getOriginalFilename());
        vo.setAssetType(assetType);
        vo.setDuration(asset.getDuration());
        vo.setThumbnailUrl(asset.getThumbnailUrl());
        return vo;
    }

    private String defaultUsername(String username)
    {
        return StringUtils.isEmpty(username) ? "system" : username;
    }

    private void fillSummary(MoodAssetBindSummaryVo summary, List<BizMoodAsset> assets)
    {
        int maxVoiceDuration = 0;
        for (BizMoodAsset asset : assets)
        {
            if (asset.getAssetType() != null && asset.getAssetType() == ASSET_TYPE_IMAGE)
            {
                summary.setHasImage(true);
            }
            if (asset.getAssetType() != null && asset.getAssetType() == ASSET_TYPE_VOICE)
            {
                summary.setHasVoice(true);
                if (asset.getDuration() != null)
                {
                    maxVoiceDuration = Math.max(maxVoiceDuration, asset.getDuration());
                }
            }
        }
        summary.setMaxVoiceDuration(maxVoiceDuration);
    }
}
