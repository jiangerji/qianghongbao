/**
 * 
 */
package com.wanke.tv.socialize.impl;

import android.content.Context;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.wanke.tv.socialize.LKShareController.ShareContent;

/**
 * @author jianglin@8864.com
 * 
 */
public class LKWXCircleImpl extends BaseSharePlatform {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#getStringID(android.content.Context
     * )
     */
    @Override
    public int getStringID(Context context) {
        return context.getResources().getIdentifier("lk_share_wechat_circle",
                "string", context.getPackageName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#getImageID(android.content.Context
     * )
     */
    @Override
    public int getImageID(Context context) {
        return context.getResources().getIdentifier("lk_share_wechat_circle",
                "drawable", context.getPackageName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#invokeShare(android.content.Context
     * , java.lang.String, java.lang.String, java.lang .String,
     * java.lang.String)
     */
    @Override
    public void invokeShare(Context context, String appID,
            ShareContent shareContent) {
        // 判断是否支持发送到朋友圈,bitmap为空，会分享失败
        WxUtils utils = new WxUtils(context, appID);

        if (utils.isSupportCircle()) {
            utils.sendWebPageWx(context, shareContent.url, shareContent.title,
                    shareContent.content, shareContent.image,
                    SendMessageToWX.Req.WXSceneTimeline);
        } else {
            if (utils.isWXInstalled()) {
            } else {
            }
        }
    }

}
