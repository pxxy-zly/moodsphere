package com.moodsphere.system.user.service.impl;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.moodsphere.common.core.domain.entity.SysRole;
import com.moodsphere.common.core.domain.entity.SysUser;
import com.moodsphere.common.core.domain.model.LoginUser;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.SecurityUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.common.utils.http.HttpUtils;
import com.moodsphere.common.utils.ip.IpUtils;
import com.moodsphere.framework.web.service.SysPermissionService;
import com.moodsphere.framework.web.service.TokenService;
import com.moodsphere.system.service.ISysRoleService;
import com.moodsphere.system.service.ISysUserService;
import com.moodsphere.system.user.config.WechatMiniappProperties;
import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.entity.BizUserAuth;
import com.moodsphere.system.user.domain.entity.BizUserInfo;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;
import com.moodsphere.system.user.mapper.BizUserAuthMapper;
import com.moodsphere.system.user.mapper.BizUserInfoMapper;
import com.moodsphere.system.user.service.IWechatMiniappAuthService;

/**
 * 微信小程序认证服务实现
 *
 * @author ruoyi
 */
@Service
public class WechatMiniappAuthServiceImpl implements IWechatMiniappAuthService
{
    private static final String AUTH_TYPE_WECHAT_MP = "wechat_mp";

    private static final String DEFAULT_ROLE_KEY = "app_user";

    private static final String DEFAULT_ROLE_KEY_COMMON = "common";

    private static final Long DEFAULT_ROLE_ID = 2L;

    private static final String DEFAULT_NICK_NAME = "mood_user";

    private static final String DEFAULT_AVATAR = "";

    private static final String WECHAT_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    private static final Long DEFAULT_DEPT_ID = 100L;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private BizUserAuthMapper bizUserAuthMapper;

    @Autowired
    private BizUserInfoMapper bizUserInfoMapper;

    @Autowired
    private WechatMiniappProperties wechatMiniappProperties;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody)
    {
        validateLoginBody(loginBody);

        WechatSession session = exchangeCode(loginBody.getCode());
        Date now = new Date();
        String loginIp = IpUtils.getIpAddr();
        BizUserAuth userAuth = bizUserAuthMapper.selectByAuthTypeAndOpenid(AUTH_TYPE_WECHAT_MP, session.getOpenid());
        boolean firstLogin = false;
        Long userId;

        if (userAuth == null)
        {
            firstLogin = true;
            SysUser newUser = buildNewUser(loginBody, session.getOpenid());
            sysUserService.registerUser(newUser);
            userId = newUser.getUserId();
            if (userId == null)
            {
                throw new ServiceException("创建系统用户失败");
            }
            insertDefaultRole(userId);
            userAuth = buildUserAuth(userId, session, now, loginIp);
            bizUserAuthMapper.insertBizUserAuth(userAuth);
            BizUserInfo userInfo = buildUserInfo(userId, loginBody);
            bizUserInfoMapper.insertBizUserInfo(userInfo);
        }
        else
        {
            userId = userAuth.getUserId();
            userAuth.setSessionKey(session.getSessionKey());
            userAuth.setUnionid(session.getUnionid());
            userAuth.setLastLoginTime(now);
            userAuth.setLastLoginIp(loginIp);
            userAuth.setUpdateBy(AUTH_TYPE_WECHAT_MP);
            userAuth.setUpdateTime(now);
            bizUserAuthMapper.updateLoginInfo(userAuth);
        }

        SysUser sysUser = sysUserService.selectUserById(userId);
        if (sysUser == null)
        {
            throw new ServiceException("登录用户不存在");
        }

        List<SysRole> roles = sysRoleService.selectRolesByUserId(userId);
        sysUser.setRoles(roles);
        Set<String> permissions = permissionService.getMenuPermission(sysUser);

        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissions);
        String token = tokenService.createToken(loginUser);

        WechatMiniappLoginVo result = new WechatMiniappLoginVo();
        result.setToken(token);
        result.setUser(sysUser);
        result.setRoles(extractRoles(sysUser));
        result.setPermissions(permissions);
        result.setAuthType(AUTH_TYPE_WECHAT_MP);
        result.setOpenid(session.getOpenid());
        result.setFirstLogin(firstLogin);
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

    private void validateLoginBody(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getCode()))
        {
            throw new ServiceException("微信登录code不能为空");
        }
    }

    private WechatSession exchangeCode(String code)
    {
        if (StringUtils.isEmpty(wechatMiniappProperties.getAppid()) || StringUtils.isEmpty(wechatMiniappProperties.getSecret()))
        {
            throw new ServiceException("微信小程序配置不完整");
        }

        String param = "appid=" + wechatMiniappProperties.getAppid() + "&secret=" + wechatMiniappProperties.getSecret()
                + "&js_code=" + code + "&grant_type=authorization_code";
        String resp = HttpUtils.sendGet(WECHAT_CODE2SESSION_URL, param);
        if (StringUtils.isEmpty(resp))
        {
            throw new ServiceException("调用微信认证服务失败");
        }
        JSONObject jsonObject = JSON.parseObject(resp);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode != 0)
        {
            String errmsg = jsonObject.getString("errmsg");
            throw new ServiceException("微信登录失败: " + (StringUtils.isEmpty(errmsg) ? "未知错误" : errmsg));
        }

        String openid = jsonObject.getString("openid");
        if (StringUtils.isEmpty(openid))
        {
            throw new ServiceException("微信登录失败: 未获取到openid");
        }
        WechatSession session = new WechatSession();
        session.setOpenid(openid);
        session.setUnionid(jsonObject.getString("unionid"));
        session.setSessionKey(jsonObject.getString("session_key"));
        return session;
    }

    private SysUser buildNewUser(WechatMiniappLoginBody loginBody, String openid)
    {
        SysUser user = new SysUser();
        user.setDeptId(DEFAULT_DEPT_ID);
        user.setUserName(buildUserName(openid));
        user.setNickName(buildNickName(loginBody));
        user.setAvatar(buildAvatar(loginBody));
        user.setPassword(SecurityUtils.encryptPassword(UUID.randomUUID().toString()));
        user.setStatus("0");
        user.setDelFlag("0");
        user.setSex("2");
        user.setCreateBy(AUTH_TYPE_WECHAT_MP);
        return user;
    }

    private BizUserAuth buildUserAuth(Long userId, WechatSession session, Date now, String loginIp)
    {
        BizUserAuth userAuth = new BizUserAuth();
        userAuth.setUserId(userId);
        userAuth.setAuthType(AUTH_TYPE_WECHAT_MP);
        userAuth.setOpenid(session.getOpenid());
        userAuth.setUnionid(session.getUnionid());
        userAuth.setSessionKey(session.getSessionKey());
        userAuth.setAuthStatus(1);
        userAuth.setLastLoginTime(now);
        userAuth.setLastLoginIp(loginIp);
        userAuth.setCreateBy(AUTH_TYPE_WECHAT_MP);
        userAuth.setCreateTime(now);
        userAuth.setDelFlag("0");
        return userAuth;
    }

    private BizUserInfo buildUserInfo(Long userId, WechatMiniappLoginBody loginBody)
    {
        BizUserInfo userInfo = new BizUserInfo();
        userInfo.setUserId(userId);
        userInfo.setNickName(buildNickName(loginBody));
        userInfo.setAvatar(buildAvatar(loginBody));
        userInfo.setGender(0);
        userInfo.setEmotionLevel(1);
        userInfo.setRecordDays(0);
        userInfo.setTotalRecords(0);
        userInfo.setAnonymousProjection(1);
        userInfo.setCreateBy(AUTH_TYPE_WECHAT_MP);
        userInfo.setCreateTime(new Date());
        userInfo.setDelFlag("0");
        return userInfo;
    }

    private void insertDefaultRole(Long userId)
    {
        SysRole defaultRole = sysRoleService.selectRoleById(DEFAULT_ROLE_ID);
        if (defaultRole == null || !"0".equals(defaultRole.getStatus()))
        {
            return;
        }
        if (DEFAULT_ROLE_KEY_COMMON.equals(defaultRole.getRoleKey()))
        {
            sysUserService.insertUserAuth(userId, new Long[] { DEFAULT_ROLE_ID });
        }
    }

    private List<String> extractRoles(SysUser user)
    {
        if (user == null || user.getRoles() == null || user.getRoles().isEmpty())
        {
            return Collections.singletonList(DEFAULT_ROLE_KEY);
        }
        return user.getRoles().stream().map(SysRole::getRoleKey).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
    }

    private String buildUserName(String openid)
    {
        int beginIndex = Math.max(openid.length() - 24, 0);
        return "wx_" + openid.substring(beginIndex).toLowerCase();
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

    private static class WechatSession
    {
        private String openid;

        private String unionid;

        private String sessionKey;

        public String getOpenid()
        {
            return openid;
        }

        public void setOpenid(String openid)
        {
            this.openid = openid;
        }

        public String getUnionid()
        {
            return unionid;
        }

        public void setUnionid(String unionid)
        {
            this.unionid = unionid;
        }

        public String getSessionKey()
        {
            return sessionKey;
        }

        public void setSessionKey(String sessionKey)
        {
            this.sessionKey = sessionKey;
        }
    }
}
