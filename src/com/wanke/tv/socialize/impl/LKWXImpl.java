/**
 * 
 */
package com.wanke.tv.socialize.impl;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.wanke.tv.socialize.LKShareController.ShareContent;

/**
 * Œ¢–≈≈Û”—∑÷œÌ
 * 
 * @author jianglin@8864.com
 * 
 */
public class LKWXImpl extends BaseSharePlatform {

    /*
     * (non-Javadoc)
     * 
     * @see com.lk.socialize.impl.BaseSharePlatform#getStringID()
     */
    @Override
    public int getStringID(Context context) {
        return context.getResources().getIdentifier("lk_share_wechat_friend",
                "string", context.getPackageName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.lk.socialize.impl.BaseSharePlatform#getImageID()
     */
    @Override
    public int getImageID(Context context) {
        return context.getResources().getIdentifier("lk_share_wechat_friends",
                "drawable", context.getPackageName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#invokeShare(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void invokeShare(Context context, String appID,
            ShareContent shareContent) {
        if (shareContent == null) {
            return;
        }

        WxUtils utils = new WxUtils(context, appID);

        if (utils.isWXInstalled()) {
            utils.sendWebPageWx(context, shareContent.url, shareContent.title,
                    shareContent.content, shareContent.image,
                    SendMessageToWX.Req.WXSceneSession);
        } else {
        }
    }
}
