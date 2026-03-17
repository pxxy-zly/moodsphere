package com.moodsphere.system.user.mapper;

import com.moodsphere.system.user.domain.entity.BizUserInfo;

public interface BizUserInfoMapper
{
    BizUserInfo selectByUserId(Long userId);

    int insertBizUserInfo(BizUserInfo bizUserInfo);
}
