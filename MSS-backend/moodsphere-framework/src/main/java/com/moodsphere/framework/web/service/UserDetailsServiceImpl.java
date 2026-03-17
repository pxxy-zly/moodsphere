package com.moodsphere.framework.web.service;

import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.moodsphere.common.core.domain.entity.SysUser;
import com.moodsphere.common.core.domain.model.LoginUser;
import com.moodsphere.common.enums.UserStatus;
import com.moodsphere.common.exception.ServiceException;
import com.moodsphere.common.utils.MessageUtils;
import com.moodsphere.common.utils.StringUtils;
import com.moodsphere.system.service.ISysRoleService;
import com.moodsphere.system.service.ISysUserService;

/**
 * 用户验证处理
 *
 * @author ruoyi
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService
{
    private static final Logger log = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    @Autowired
    private ISysUserService userService;
    
    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private SysPermissionService permissionService;

    @Autowired
    private ISysRoleService roleService;

    @Autowired
    private PcLoginSecurityProperties pcLoginSecurityProperties;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        SysUser user = userService.selectUserByUserName(username);
        if (StringUtils.isNull(user))
        {
            log.info("登录用户：{} 不存在.", username);
            throw new ServiceException(MessageUtils.message("user.not.exists"));
        }
        else if (UserStatus.DELETED.getCode().equals(user.getDelFlag()))
        {
            log.info("登录用户：{} 已被删除.", username);
            throw new ServiceException(MessageUtils.message("user.password.delete"));
        }
        else if (UserStatus.DISABLE.getCode().equals(user.getStatus()))
        {
            log.info("登录用户：{} 已被停用.", username);
            throw new ServiceException(MessageUtils.message("user.blocked"));
        }

        checkPcLoginAccess(user);
        passwordService.validate(user);

        return createLoginUser(user);
    }

    private void checkPcLoginAccess(SysUser user)
    {
        if (!pcLoginSecurityProperties.isBlockAppUser())
        {
            return;
        }

        Set<String> roleKeys = roleService.selectRolePermissionByUserId(user.getUserId());
        if (roleKeys == null || roleKeys.isEmpty() || !roleKeys.contains("app_user"))
        {
            return;
        }

        List<String> adminRoleKeys = pcLoginSecurityProperties.getAdminRoleKeys();
        if (adminRoleKeys != null)
        {
            for (String roleKey : adminRoleKeys)
            {
                if (StringUtils.isNotEmpty(roleKey) && roleKeys.contains(roleKey))
                {
                    return;
                }
            }
        }
        throw new ServiceException("移动端账号不允许登录管理后台");
    }

    public UserDetails createLoginUser(SysUser user)
    {
        return new LoginUser(user.getUserId(), user.getDeptId(), user, permissionService.getMenuPermission(user));
    }
}
