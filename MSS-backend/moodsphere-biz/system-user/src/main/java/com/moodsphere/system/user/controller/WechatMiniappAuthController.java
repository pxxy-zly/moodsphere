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
 * 微信小程序认证控制器
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/auth/wechat/miniapp")
public class WechatMiniappAuthController
{
    @Autowired
    private IWechatMiniappAuthService wechatMiniappAuthService;

    /**
     * 微信一键登录
     *
     * @param loginBody 登录参数
     * @return token 和用户信息
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
     * 查询当前用户信息
     *
     * @return 当前用户信息
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