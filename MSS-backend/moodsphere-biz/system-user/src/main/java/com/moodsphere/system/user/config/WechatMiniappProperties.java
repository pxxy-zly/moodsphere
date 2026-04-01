package com.moodsphere.system.user.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序配置属性类
 * 从配置文件中读取微信小程序相关配置
 */
@Component
@ConfigurationProperties(prefix = "wechat.miniapp")
public class WechatMiniappProperties
{
    /** 微信小程序AppID */
    private String appid;

    /** 微信小程序AppSecret */
    private String secret;

    public String getAppid()
    {
        return appid;
    }

    public void setAppid(String appid)
    {
        this.appid = appid;
    }

    public String getSecret()
    {
        return secret;
    }

    public void setSecret(String secret)
    {
        this.secret = secret;
    }
}
