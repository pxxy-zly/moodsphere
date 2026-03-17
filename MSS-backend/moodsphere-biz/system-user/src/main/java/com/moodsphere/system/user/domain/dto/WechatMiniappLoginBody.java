package com.moodsphere.system.user.domain.dto;

/**
 * 微信小程序登录请求体。
 */
public class WechatMiniappLoginBody
{
    /**
     * uni.login 返回的临时 code。
     */
    private String code;

    /**
     * 小程序用户敏感信息（预留）。
     */
    private String encryptedData;

    /**
     * 小程序解密向量（预留）。
     */
    private String iv;

    /**
     * 小程序昵称。
     */
    private String nickName;

    /**
     * 小程序头像地址。
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