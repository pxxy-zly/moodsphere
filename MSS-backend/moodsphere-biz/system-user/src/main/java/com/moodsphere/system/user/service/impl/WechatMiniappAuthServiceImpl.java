package com.moodsphere.system.user.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
 * 微信小程序登录认证服务实现类
 */
@Service
public class WechatMiniappAuthServiceImpl implements IWechatMiniappAuthService
{
    /** 认证类型：微信小程序 */
    private static final String AUTH_TYPE_WECHAT_MP = "wechat_mp";

    /** 默认角色标识 */
    private static final String DEFAULT_ROLE_KEY_APP_USER = "app_user";

    /** 默认角色ID */
    private static final Long DEFAULT_ROLE_ID = 100L;

    /** 默认昵称 */
    private static final String DEFAULT_NICK_NAME = "mood_user";

    /** 默认头像 */
    private static final String DEFAULT_AVATAR = "";

    /** 微信code2session接口地址 */
    private static final String WECHAT_CODE2SESSION_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /** 默认部门ID */
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

    /**
     * 微信小程序登录
     * 使用微信code换取登录凭证，首次登录会自动注册用户
     * 
     * @param loginBody 登录请求体
     * @return 登录结果
     */
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
                throw new ServiceException("注册用户失败，未返回用户ID");
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
            if (userId == null)
            {
                throw new ServiceException("用户授权数据异常，请联系管理员");
            }
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
            throw new ServiceException("登录失败，用户不存在");
        }

        Set<String> roleKeys = sysRoleService.selectRolePermissionByUserId(userId);
        Set<String> permissions = permissionService.getMenuPermission(sysUser);

        LoginUser loginUser = new LoginUser(sysUser.getUserId(), sysUser.getDeptId(), sysUser, permissions);
        String token = tokenService.createToken(loginUser);

        WechatMiniappLoginVo result = new WechatMiniappLoginVo();
        result.setToken(token);
        result.setUser(sysUser);
        result.setRoles(toRoleList(roleKeys));
        result.setPermissions(permissions);
        result.setAuthType(AUTH_TYPE_WECHAT_MP);
        result.setOpenid(session.getOpenid());
        result.setFirstLogin(firstLogin);
        result.setNeedBindPhone(Boolean.FALSE);
        return result;
    }

    /**
     * 获取当前登录用户信息
     * 
     * @return 当前登录用户信息
     */
    @Override
    public WechatMiniappLoginVo getCurrentUserInfo()
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null)
        {
            throw new ServiceException("未获取到当前登录信息");
        }

        Set<String> roleKeys = sysRoleService.selectRolePermissionByUserId(loginUser.getUserId());
        WechatMiniappLoginVo result = new WechatMiniappLoginVo();
        result.setUser(loginUser.getUser());
        result.setRoles(toRoleList(roleKeys));
        result.setPermissions(loginUser.getPermissions());
        result.setAuthType(AUTH_TYPE_WECHAT_MP);
        result.setFirstLogin(Boolean.FALSE);
        result.setNeedBindPhone(Boolean.FALSE);
        return result;
    }

    /**
     * 验证登录请求体
     * 
     * @param loginBody 登录请求体
     */
    private void validateLoginBody(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null)
        {
            throw new ServiceException("微信小程序登录失败，请求参数不能为空");
        }
        String code = loginBody.getCode() == null ? null : loginBody.getCode().trim();
        if (StringUtils.isEmpty(code))
        {
            throw new ServiceException("微信小程序登录失败，code不能为空");
        }
        loginBody.setCode(code);
    }

    /**
     * 使用微信code换取session信息
     * 
     * @param code 微信登录code
     * @return 微信session信息
     */
    private WechatSession exchangeCode(String code)
    {
        if (StringUtils.isEmpty(wechatMiniappProperties.getAppid()) || StringUtils.isEmpty(wechatMiniappProperties.getSecret()))
        {
            throw new ServiceException("微信小程序配置不完整，请检查appid和secret");
        }

        String resp = requestCode2Session(code);
        if (StringUtils.isEmpty(resp))
        {
            throw new ServiceException("调用微信接口失败，未获取到响应");
        }

        JSONObject jsonObject = JSON.parseObject(resp);
        Integer errcode = jsonObject.getInteger("errcode");
        if (errcode != null && errcode != 0)
        {
            String errmsg = jsonObject.getString("errmsg");
            throw new ServiceException("微信登录失败：" + (StringUtils.isEmpty(errmsg) ? "微信接口返回错误" : errmsg));
        }

        String openid = jsonObject.getString("openid");
        if (StringUtils.isEmpty(openid))
        {
            throw new ServiceException("微信登录失败：未获取到openid");
        }

        WechatSession session = new WechatSession();
        session.setOpenid(openid);
        session.setUnionid(jsonObject.getString("unionid"));
        session.setSessionKey(jsonObject.getString("session_key"));
        return session;
    }

    /**
     * 请求微信code2session接口
     * 
     * @param code 微信登录code
     * @return 接口响应内容
     */
    private String requestCode2Session(String code)
    {
        HttpURLConnection connection = null;
        try
        {
            String query = "appid=" + URLEncoder.encode(wechatMiniappProperties.getAppid(), StandardCharsets.UTF_8)
                    + "&secret=" + URLEncoder.encode(wechatMiniappProperties.getSecret(), StandardCharsets.UTF_8)
                    + "&js_code=" + URLEncoder.encode(code, StandardCharsets.UTF_8)
                    + "&grant_type=authorization_code";

            URL url = new URL(WECHAT_CODE2SESSION_URL + "?" + query);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            InputStream inputStream = status >= 200 && status < 400 ? connection.getInputStream() : connection.getErrorStream();
            if (inputStream == null)
            {
                return null;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)))
            {
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                {
                    result.append(line);
                }
                return result.toString();
            }
        }
        catch (Exception e)
        {
            throw new ServiceException("调用微信接口失败，请稍后重试");
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }

    /**
     * 构建新用户
     * 
     * @param loginBody 登录请求体
     * @param openid 微信openid
     * @return 新用户对象
     */
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

    /**
     * 构建用户认证信息
     * 
     * @param userId 用户ID
     * @param session 微信session
     * @param now 当前时间
     * @param loginIp 登录IP
     * @return 用户认证对象
     */
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

    /**
     * 构建用户信息
     * 
     * @param userId 用户ID
     * @param loginBody 登录请求体
     * @return 用户信息对象
     */
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

    /**
     * 插入默认角色
     * 
     * @param userId 用户ID
     */
    private void insertDefaultRole(Long userId)
    {
        SysRole defaultRole = sysRoleService.selectRoleById(DEFAULT_ROLE_ID);
        if (defaultRole == null || !"0".equals(defaultRole.getStatus()))
        {
            throw new ServiceException("默认角色无效，请检查角色ID=100是否存在且状态正常");
        }
        if (!DEFAULT_ROLE_KEY_APP_USER.equals(defaultRole.getRoleKey()))
        {
            throw new ServiceException("默认角色配置错误，请检查角色ID=100的roleKey是否为app_user");
        }
        sysUserService.insertUserAuth(userId, new Long[] { DEFAULT_ROLE_ID });
    }

    /**
     * 将角色key集合转换为列表
     * 
     * @param roleKeys 角色key集合
     * @return 角色列表
     */
    private List<String> toRoleList(Set<String> roleKeys)
    {
        if (roleKeys == null || roleKeys.isEmpty())
        {
            return Collections.singletonList(DEFAULT_ROLE_KEY_APP_USER);
        }
        List<String> roleList = roleKeys.stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (roleList.isEmpty())
        {
            return Collections.singletonList(DEFAULT_ROLE_KEY_APP_USER);
        }
        return roleList;
    }

    /**
     * 构建用户名
     * 
     * @param openid 微信openid
     * @return 用户名
     */
    private String buildUserName(String openid)
    {
        int beginIndex = Math.max(openid.length() - 24, 0);
        return "wx_" + openid.substring(beginIndex).toLowerCase();
    }

    /**
     * 构建昵称
     * 
     * @param loginBody 登录请求体
     * @return 昵称
     */
    private String buildNickName(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getNickName()))
        {
            return DEFAULT_NICK_NAME;
        }
        return loginBody.getNickName().trim();
    }

    /**
     * 构建头像
     * 
     * @param loginBody 登录请求体
     * @return 头像URL
     */
    private String buildAvatar(WechatMiniappLoginBody loginBody)
    {
        if (loginBody == null || StringUtils.isEmpty(loginBody.getAvatarUrl()))
        {
            return DEFAULT_AVATAR;
        }
        return loginBody.getAvatarUrl().trim();
    }

    /**
     * 微信Session信息内部类
     */
    private static class WechatSession
    {
        /** 微信openid */
        private String openid;

        /** 微信unionid */
        private String unionid;

        /** 微信session_key */
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