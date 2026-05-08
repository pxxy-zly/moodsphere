package com.moodsphere.asset.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.moodsphere.asset.domain.entity.BizMoodAsset;

public interface BizMoodAssetMapper
{
    int insertBizMoodAsset(BizMoodAsset bizMoodAsset);

    List<BizMoodAsset> selectUnboundByIdsAndCreateBy(@Param("assetIds") List<Long> assetIds, @Param("createBy") String createBy);

    int bindAssetsToRecord(@Param("recordId") Long recordId, @Param("assetIds") List<Long> assetIds,
            @Param("createBy") String createBy, @Param("updateBy") String updateBy, @Param("updateTime") Date updateTime);
}
