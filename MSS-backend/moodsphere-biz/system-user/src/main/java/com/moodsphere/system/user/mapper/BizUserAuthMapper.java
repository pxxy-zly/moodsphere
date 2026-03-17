package com.moodsphere.system.user.mapper;

import org.apache.ibatis.annotations.Param;
import com.moodsphere.system.user.domain.entity.BizUserAuth;

public interface BizUserAuthMapper
{
    BizUserAuth selectByAuthTypeAndOpenid(@Param("authType") String authType, @Param("openid") String openid);

    int insertBizUserAuth(BizUserAuth bizUserAuth);

    int updateLoginInfo(BizUserAuth bizUserAuth);
}
