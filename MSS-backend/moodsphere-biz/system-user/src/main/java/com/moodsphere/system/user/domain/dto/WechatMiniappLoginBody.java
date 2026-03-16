package com.moodsphere.system.user.domain.dto;

/**
 * 微信小程序一键登录请求参数
 *
 * @author ruoyi
 */
public class WechatMiniappLoginBody
{
    /**
     * uni.login 获取的临时 code
     */
    private String code;

    /**
     * 微信加密用户数据
     */
    private String encryptedData;

    /**
     * 加密向量
     */
    private String iv;

    /**
     * 客户端上送昵称
     */
    private String nickName;

    /**
     * 客户端上送头像地址
     */
    private String avatarUrl;

    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getEncryptedData()
    {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData)
    {
        this.encryptedData = encryptedData;
    }

    public String getIv()
    {
        return iv;
    }

    public void setIv(String iv)
    {
        this.iv = iv;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getAvatarUrl()
    {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl)
    {
        this.avatarUrl = avatarUrl;
    }
}