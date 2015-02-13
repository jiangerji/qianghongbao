/**
 * 
 */
package com.wanke.tv.socialize.impl;

import android.content.Context;
import android.content.Intent;

import com.wanke.tv.socialize.LKShareController.ShareContent;
import com.wanke.tv.socialize.LKShareController.SharePlatformCode;
import com.wanke.tv.socialize.LKSharePlatformContainer;

/**
 * @author jianglin@8864.com
 * 
 */
public abstract class BaseSharePlatform {

    private static final void register(SharePlatformCode code, Class<?> cls) {
        // Log.d(TAG, "register: realClass is " + cls.getName());
        LKSharePlatformContainer.addSharePlatform(code, cls);
    }

    /**
     * 分享平台初始化
     */
    public static final void init() {
        register(SharePlatformCode.WeChat, LKWXImpl.class);
        register(SharePlatformCode.WeChatCircle, LKWXCircleImpl.class);
        // register(SharePlatformCode.Facebook, LKFBImpl.class);
        register(SharePlatformCode.Twitter, LKTwitterImpl.class);
        register(SharePlatformCode.Email, LKEmailImpl.class);
    }

    /**
     * 获取分享平台的title string资源ID
     * 
     * @return
     */
    public abstract int getStringID(Context context);

    /**
     * 获取分享平台的图片资源ID
     * 
     * @return
     */
    public abstract int getImageID(Context context);

    /**
     * 触发分享行为
     * 
     * @param title
     *            分享的标题
     * @param content
     *            分享的内容
     * @param image
     *            分享的图片
     * @param url
     *            分享的链接url
     */
    public abstract void invokeShare(Context context, String appID,
            ShareContent shareContent);

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }
}
