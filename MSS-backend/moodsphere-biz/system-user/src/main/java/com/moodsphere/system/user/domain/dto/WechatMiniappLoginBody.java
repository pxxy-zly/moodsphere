package com.moodsphere.system.user.domain.dto;

/**
 * 瀵邦喕淇婄亸蹇曗柤鎼村繋绔撮柨顔炬瑜版洝顕Ч鍌氬棘閺? *
 * @author ruoyi
 */
public class WechatMiniappLoginBody
{
    /**
     * uni.login 閼惧嘲褰囬惃鍕閺?code
     */
    private String code;

    /**
     * 瀵邦喕淇婇崝鐘茬槕閻劍鍩涢弫鐗堝祦
     */
    private String encryptedData;

    /**
     * 閸旂姴鐦戦崥鎴﹀櫤
     */
    private String iv;

    /**
     * 鐎广垺鍩涚粩顖欑瑐闁焦妯€缁?     */
    private String nickName;

    /**
     * 鐎广垺鍩涚粩顖欑瑐闁礁銇旈崓蹇撴勾閸р偓
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