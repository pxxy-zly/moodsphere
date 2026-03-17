package com.moodsphere.system.user.service;

import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;

/**
 * 微信小程序登录认证服务。
 */
public interface IWechatMiniappAuthService
{
    /**
     * 使用小程序登录 code 登录。
     */
    WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody);

    /**
     * 获取当前登录用户信息。
     */
    WechatMiniappLoginVo getCurrentUserInfo();
}