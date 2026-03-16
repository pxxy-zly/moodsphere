package com.moodsphere.system.user.service.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.moodsphere.common.constant.Constants;
import com.moodsphere.common.core.domain.entity.SysRole;
import com.moodsphere.common.core.domain.entity.SysUser;
import com.moodsphere.common.core.domain.model.LoginUser;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.framework.web.service.TokenService;
import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;
import com.moodsphere.system.user.service.IWechatMiniappAuthService;

/**
 * 微信小程序认证服务模拟实现
 *
 * @author ruoyi
 */
@Service
public class WechatMiniappAuthServiceImpl implements IWechatMiniappAuthService
{
    private static final String AUTH_TYPE_WECHAT_MP = "wechat_mp";

    private static final String DEFAULT_ROLE_KEY = "app_user";

    private static final Long MOCK_USER_ID = 10001L;

    private static final Long MOCK_DEPT_ID = 100L;

    private static final String DEFAULT_NICK_NAME = "mood_user";

    private static final String DEFAULT_AVATAR = "";

    @Autowired
    private TokenService tokenService;

    @Override
    public WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody)
    {
        SysUser sysUser = buildMockUser(loginBody);
        Set<String> permissions = new HashSet<>();
        permissions.add(Constants.ALL_PERMISSION);

        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissions);
        String token = tokenService.createToken(loginUser);

        WechatMiniappLoginVo result = new WechatMiniappLoginVo();
        result.setToken(token);
        result.setUser(sysUser);
        result.setRoles(extractRoles(sysUser));
        result.setPermissions(permissions);
        result.setAuthType(AUTH_TYPE_WECHAT_MP);
        result.setOpenid(buildMockOpenid(loginBody));
        result.setFirstLogin(Boolean.FALSE);
        result.setNeedBindPhone(Boolean.FALSE);
        return result;
    }

    @Override
    public WechatMiniappLoginVo getCurrentUserInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        WechatMiniappLoginVo result = new WechatMiniappLoginVo();
        result.setUser(loginUser.getUser());
        result.setRoles(extractRoles(loginUser.getUser()));
        result.setPermissions(loginUser.getPermissions());
        result.setAuthType(AUTH_TYPE_WECHAT_MP);
        result.setFirstLogin(Boolean.FALSE);
        result.setNeedBindPhone(Boolean.FALSE);
        return result;
    }

    private SysUser buildMockUser(WechatMiniappLoginBody loginBody)
    {
        SysUser user = new SysUser();
        user.setUserId(MOCK_USER_ID);
        user.setDeptId(MOCK_DEPT_ID);
        user.setUserName(buildUserName(loginBody));
        user.setNickName(buildNickName(loginBody));
        user.setAvatar(buildAvatar(loginBody));
        user.setStatus("0");
        user.setDelFlag("0");
        user.setSex("0");
        user.setRoles(buildMockRoles());
        return user;
    }

    private String buildUserName(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getCode()))
        {
            return "wx_mock_user";
        }

        String code = loginBody.getCode();
        int beginIndex = Math.max(code.length() - 6, 0);
        return "wx_" + code.substring(beginIndex).toLowerCase();
    }

    private String buildNickName(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getNickName()))
        {
            return DEFAULT_NICK_NAME;
        }
        return loginBody.getNickName();
    }

    private String buildAvatar(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getAvatarUrl()))
        {
            return DEFAULT_AVATAR;
        }
        return loginBody.getAvatarUrl();
    }

    private List<String> extractRoles(SysUser user)
    {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return Collections.singletonList(DEFAULT_ROLE_KEY);
        }
        return user.getRoles().stream().map(SysRole::getRoleKey).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    private List<SysRole> buildMockRoles()
    {
        SysRole role = new SysRole();
        role.setRoleId(100L);
        role.setRoleName("app user");
        role.setRoleKey(DEFAULT_ROLE_KEY);
        return Collections.singletonList(role);
    }

    private String buildMockOpenid(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getCode()))
        {
            return "mock_openid";
        }
        return "mock_openid_" + loginBody.getCode();
    }
}