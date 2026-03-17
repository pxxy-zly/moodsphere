package com.moodsphere.framework.web.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.pc-login")
public class PcLoginSecurityProperties
{
    private boolean blockAppUser = true;

    private List<String> adminRoleKeys = new ArrayList<>();

    public boolean isBlockAppUser()
    {
        return blockAppUser;
    }

    public void setBlockAppUser(boolean blockAppUser)
    {
        this.blockAppUser = blockAppUser;
    }

    public List<String> getAdminRoleKeys()
    {
        return adminRoleKeys;
    }

    public void setAdminRoleKeys(List<String> adminRoleKeys)
    {
        this.adminRoleKeys = adminRoleKeys;
    }
}
