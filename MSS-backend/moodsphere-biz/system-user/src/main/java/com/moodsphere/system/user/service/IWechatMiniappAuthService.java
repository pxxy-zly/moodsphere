package com.moodsphere.system.user.service;

import com.moodsphere.system.user.domain.dto.WechatMiniappLoginBody;
import com.moodsphere.system.user.domain.vo.WechatMiniappLoginVo;

/**
 * 瀵邦喕淇婄亸蹇曗柤鎼村繗顓荤拠浣规箛閸? *
 * @author ruoyi
 */
public interface IWechatMiniappAuthService
{
    /**
     * 娴ｈ法鏁ゅ顔讳繆鐏忓繒鈻兼惔?code 閻ц缍?     *
     * @param loginBody 閻ц缍嶉崣鍌涙殶
     * @return 閻ц缍嶇紒鎾寸亯
     */
    WechatMiniappLoginVo login(WechatMiniappLoginBody loginBody);

    /**
     * 閺屻儴顕楄ぐ鎾冲閻ц缍嶉悽銊﹀煕娣団剝浼?     *
     * @return 閻劍鍩涙穱鈩冧紖
     */
    WechatMiniappLoginVo getCurrentUserInfo();
}