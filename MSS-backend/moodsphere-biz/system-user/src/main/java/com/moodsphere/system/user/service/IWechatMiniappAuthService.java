package com.moodsphere.system.user.service;

import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;

/**
 * 微信小程序登录认证服务接口
 */
public interface IWechatMiniappAuthService
{
    /**
     * 微信小程序登录
     * 使用微信code换取登录凭证，首次登录会自动注册用户
     * 
     * @param loginBody 登录请求体
     * @return 登录结果
     */
    WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody);

    /**
     * 获取当前登录用户信息
     * 
     * @return 当前登录用户信息
     */
    WechatMiniappLoginVo getCurrentUserInfo();
}