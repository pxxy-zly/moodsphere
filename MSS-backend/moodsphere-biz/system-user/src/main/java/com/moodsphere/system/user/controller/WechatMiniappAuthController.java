package com.moodsphere.system.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.moodsphere.common.annotation.Anonymous;
import com.moodsphere.common.constant.Constants;
import com.moodsphere.common.core.domain.AjaxResult;
import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;
import com.moodsphere.system.user.service.IWechatMiniappAuthService;

/**
 * 瀵邦喕淇婄亸蹇曗柤鎼村繗顓荤拠浣瑰付閸掕泛娅? *
 * @author ruoyi
 */
@RestController
@RequestMapping("/app/auth/wechat/miniapp")
public class WechatMiniappAuthController
{
    @Autowired
    private IWechatMiniappAuthService wechatMiniappAuthService;

    /**
     * 瀵邦喕淇婃稉鈧柨顔炬瑜?     *
     * @param loginBody 閻ц缍嶉崣鍌涙殶
     * @return token 閸滃瞼鏁ら幋铚備繆閹?     */
    @Anonymous
    @PostMapping("/login")
    public AjaxResult login(@RequestBody(required = false) WechatMiniappLoginBody loginBody)
    {
        WechatMiniappLoginVo loginResult = wechatMiniappAuthService.login(loginBody);
        AjaxResult ajax = AjaxResult.success();
        ajax.put(Constants.TOKEN, loginResult.getToken());
        ajax.put("user", loginResult.getUser());
        ajax.put("roles", loginResult.getRoles());
        ajax.put("permissions", loginResult.getPermissions());
        ajax.put("authType", loginResult.getAuthType());
        ajax.put("openid", loginResult.getOpenid());
        ajax.put("firstLogin", loginResult.getFirstLogin());
        ajax.put("needBindPhone", loginResult.getNeedBindPhone());
        return ajax;
    }

    /**
     * 閺屻儴顕楄ぐ鎾冲閻劍鍩涙穱鈩冧紖
     *
     * @return 瑜版挸澧犻悽銊﹀煕娣団剝浼?     */
    @GetMapping("/info")
    public AjaxResult getInfo()
    {
        WechatMiniappLoginVo info = wechatMiniappAuthService.getCurrentUserInfo();
        AjaxResult ajax = AjaxResult.success();
        ajax.put("user", info.getUser());
        ajax.put("roles", info.getRoles());
        ajax.put("permissions", info.getPermissions());
        ajax.put("authType", info.getAuthType());
        ajax.put("firstLogin", info.getFirstLogin());
        ajax.put("needBindPhone", info.getNeedBindPhone());
        return ajax;
    }
}