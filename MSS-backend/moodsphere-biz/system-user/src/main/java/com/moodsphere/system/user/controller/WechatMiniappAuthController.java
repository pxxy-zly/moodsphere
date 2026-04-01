package com.moodsphere.system.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moodsphere.common.annotation.Anonymous;
import com.moodsphere.common.constant.Constants;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;
import com.moodsphere.system.user.service.IWechatMiniappAuthService;

/**
 * 微信小程序登录认证控制器
 * 提供微信小程序登录和用户信息获取接口
 */
@RestController
@RequestMapping("/app/auth/wechat/miniapp")
public class WechatMiniappAuthController
{
    @Autowired
    private IWechatMiniappAuthService wechatMiniappAuthService;

    /**
     * 微信小程序登录
     * 支持匿名访问，使用微信code换取登录凭证
     * 
     * @param loginBody 登录请求体，包含微信code等信息
     * @return 登录结果，包含token、用户信息、角色权限等
     */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody(required = false) WechatMiniappLoginBody loginBody)
    {
        WechatMiniappLoginVo loginResult = wechatMiniappAuthService.login(loginBody);
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, loginResult.getToken());
        ajax.put("user", loginResult.getUser());
        ajax.put("roles", loginResult.getRoles());
        ajax.put("permissions", loginResult.getPermissions());
        ajax.put("authType", loginResult.getAuthType());
        ajax.put("openid", loginResult.getOpenid());
        ajax.put("firstLogin", loginResult.getFirstLogin());
        ajax.put("needBindPhone", loginResult.getNeedBindPhone());
        return ajax;
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 当前登录用户信息
     */
    @GetMapping("/info")
    public AjaxResult getInfo()
    {
        WechatMiniappLoginVo info = wechatMiniappAuthService.getCurrentUserInfo();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", info.getUser());
        ajax.put("roles", info.getRoles());
        ajax.put("permissions", info.getPermissions());
        ajax.put("authType", info.getAuthType());
        ajax.put("firstLogin", info.getFirstLogin());
        ajax.put("needBindPhone", info.getNeedBindPhone());
        return ajax;
    }
}