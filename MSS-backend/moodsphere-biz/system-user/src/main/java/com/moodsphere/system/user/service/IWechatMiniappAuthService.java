package com.moodsphere.system.user.service;

import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;

/**
 * 微信小程序认证服务
 *
 * @author ruoyi
 */
public interface IWechatMiniappAuthService
{
    /**
     * 使用微信小程序 code 登录
     *
     * @param loginBody 登录参数
     * @return 登录结果
     */
    WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody);

    /**
     * 查询当前登录用户信息
     *
     * @return 用户信息
     */
    WechatMiniappLoginVo getCurrentUserInfo();
}