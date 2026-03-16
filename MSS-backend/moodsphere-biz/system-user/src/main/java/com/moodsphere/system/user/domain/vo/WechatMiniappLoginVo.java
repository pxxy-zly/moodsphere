package com.moodsphere.system.user.domain.vo;

import java.util.List;
import java.util.Set;
import com.moodsphere.common.core.domain.entity.SysUser;

/**
 * 微信小程序一键登录响应对象
 *
 * @author ruoyi
 */
public class WechatMiniappLoginVo
{
    /**
     * 登录 token
     */
    private String token;

    /**
     * 当前用户
     */
    private SysUser user;

    /**
     * 当前角色标识列表
     */
    private List<String> roles;

    /**
     * 当前权限标识集合
     */
    private Set<String> permissions;

    /**
     * 登录认证类型
     */
    private String authType;

    /**
     * 微信 openid
     */
    private String openid;

    /**
     * 是否首次登录
     */
    private Boolean firstLogin;

    /**
     * 是否需要绑定手机号
     */
    private Boolean needBindPhone;

    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

    public SysUser getUser()
    {
        return user;
    }

    public void setUser(SysUser user)
    {
        this.user = user;
    }

    public List<String> getRoles()
    {
        return roles;
    }

    public void setRoles(List<String> roles)
    {
        this.roles = roles;
    }

    public Set<String> getPermissions()
    {
        return permissions;
    }

    public void setPermissions(Set<String> permissions)
    {
        this.permissions = permissions;
    }

    public String getAuthType()
    {
        return authType;
    }

    public void setAuthType(String authType)
    {
        this.authType = authType;
    }

    public String getOpenid()
    {
        return openid;
    }

    public void setOpenid(String openid)
    {
        this.openid = openid;
    }

    public Boolean getFirstLogin()
    {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin)
    {
        this.firstLogin = firstLogin;
    }

    public Boolean getNeedBindPhone()
    {
        return needBindPhone;
    }

    public void setNeedBindPhone(Boolean needBindPhone)
    {
        this.needBindPhone = needBindPhone;
    }
}