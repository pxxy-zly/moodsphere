package com.moodsphere.system.user.domain.vo;

import java.util.List;
import java.util.Set;
import com.moodsphere.common.core.domain.entity.SysUser;

/**
 * 瀵邦喕淇婄亸蹇曗柤鎼村繋绔撮柨顔炬瑜版洖鎼锋惔鏂款嚠鐠? *
 * @author ruoyi
 */
public class WechatMiniappLoginVo
{
    /**
     * 閻ц缍?token
     */
    private String token;

    /**
     * 瑜版挸澧犻悽銊﹀煕
     */
    private SysUser user;

    /**
     * 瑜版挸澧犵憴鎺曞閺嶅洩鐦戦崚妤勩€?     */
    private List<String> roles;

    /**
     * 瑜版挸澧犻弶鍐閺嶅洩鐦戦梿鍡楁値
     */
    private Set<String> permissions;

    /**
     * 閻ц缍嶇拋銈堢槈缁鐎?     */
    private String authType;

    /**
     * 瀵邦喕淇?openid
     */
    private String openid;

    /**
     * 閺勵垰鎯佹＃鏍偧閻ц缍?     */
    private Boolean firstLogin;

    /**
     * 閺勵垰鎯侀棁鈧憰浣虹拨鐎规碍澧滈張鍝勫娇
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