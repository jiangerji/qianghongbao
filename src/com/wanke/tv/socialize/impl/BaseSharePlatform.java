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
     * ����ƽ̨��ʼ��
     */
    public static final void init() {
        register(SharePlatformCode.WeChat, LKWXImpl.class);
        register(SharePlatformCode.WeChatCircle, LKWXCircleImpl.class);
        // register(SharePlatformCode.Facebook, LKFBImpl.class);
        register(SharePlatformCode.Twitter, LKTwitterImpl.class);
        register(SharePlatformCode.Email, LKEmailImpl.class);
    }

    /**
     * ��ȡ����ƽ̨��title string��ԴID
     * 
     * @return
     */
    public abstract int getStringID(Context context);

    /**
     * ��ȡ����ƽ̨��ͼƬ��ԴID
     * 
     * @return
     */
    public abstract int getImageID(Context context);

    /**
     * ����������Ϊ
     * 
     * @param title
     *            ����ı���
     * @param content
     *            ���������
     * @param image
     *            �����ͼƬ
     * @param url
     *            ���������url
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
