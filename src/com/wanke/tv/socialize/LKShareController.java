/**
 * 
 */
package com.wanke.tv.socialize;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.wanke.tv.socialize.impl.BaseSharePlatform;
import com.wanke.tv.socialize.view.SharePlatformChooser;

/**
 * ���۷���SDK
 * 
 * @author jianglin@8864.com
 * 
 */
public class LKShareController {

    public static enum SharePlatformCode {
        /**
         * ΢�ź��ѷ���
         */
        WeChat,

        /**
         * ΢������Ȧ����
         */
        WeChatCircle,

        /**
         * facebook����
         */
        Facebook,

        /**
         * twitter����
         */
        Twitter,

        /**
         * ʹ��gmail�ʼ��ͻ��˽��з���
         */
        Email
    };

    private static LKShareController _instance = null;

    private SharePlatformChooser mSharePlatformChooser = null;

    private LKShareController() {
        mSharePlatformCodes = new ArrayList<LKShareController.SharePlatformCode>();
    }

    /**
     * ��ȡ���۷���ʵ��
     * 
     * @return
     */
    public static LKShareController getInstance() {
        if (_instance == null) {
            BaseSharePlatform.init();
            _instance = new LKShareController();
        }
        return _instance;
    }

    private ArrayList<SharePlatformCode> mSharePlatformCodes;

    /**
     * ���֧�ֵ�ƽ̨���������ӵ�˳����ʾ����
     * 
     * @param sharePlatform
     *            ����ƽ̨�Ĵ���
     */
    public void addSharePlatform(SharePlatformCode sharePlatform) {
        if (!mSharePlatformCodes.contains(sharePlatform)) {
            mSharePlatformCodes.add(sharePlatform);
        }
    }

    /**
     * ɾ�������ƽ̨
     * 
     * @param sharePlatform
     *            ����ƽ̨�Ĵ���
     */
    public void removeSharePlatform(SharePlatformCode sharePlatform) {
        mSharePlatformCodes.remove(sharePlatform);
    }

    public static class ShareContent {

        public String title;
        public String content;
        public Bitmap image = null;
        public String imageUrl = null; // Ŀǰ���ֶΣ���facebookʹ��
        public String url;
        public String appID;

        /**
         * 
         * @param title
         * @param content
         * @param imageUrl
         *            ���ֶ�ֻ��facebook����
         * @param image
         * @param url
         * @param appID
         */
        public ShareContent(String title, String content, String imageUrl,
                Bitmap image, String url, String appID) {
            this.title = title;
            this.content = content;
            this.image = image;
            this.imageUrl = imageUrl;
            this.url = url;
            this.appID = appID;
        }
    }

    private ShareContent mShareContent;

    /**
     * ������Ҫ��������ݣ���ʾ��ǰ����ƽ̨���û�ѡ�����з���
     * ����ѡ���ʼ��������������ͼƬ������Ч���ʼ����ط���ͼƬ����
     * 
     * @param activity
     *            ʹ�÷����activityʵ��
     * @param title
     *            ����ı���
     * @param content
     *            ���������
     * @param imageUrl
     *            ͼƬ��URL��ַ��Ŀǰfacebook��ʹ��
     * @param image
     *            ���������ͼ
     * @param url
     *            ������ת��url
     * @param appID
     *            ΢�ŵ�app id
     */
    public void invokeShare(Activity activity, String title, String content,
            String imageUrl, Bitmap image, String url, String appID) {
        if (mSharePlatformChooser != null && mSharePlatformChooser.isShowing()) {
            return;
        }

        mSharePlatformChooser = new SharePlatformChooser(activity);
        mSharePlatformChooser.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {
                // mSharePlatformChooser = null;
            }
        });
        mSharePlatformChooser.show();

        if (image == null) {
            image = getAppIcon(activity);
        }

        Log.d("lkshare", "����title:" + title);
        Log.d("lkshare", "����content:" + content);
        Log.d("lkshare", "����url:" + url);
        Log.d("lkshare", "����appID:" + appID);
        Log.d("lkshare", "����image url:" + imageUrl);
        mShareContent = new ShareContent(title, content, imageUrl, image, url,
                appID);
    }

    /**
     * ��ȡ��ǰ��Ҫ���з��������
     * 
     * @return
     */
    public ShareContent getShareContent() {
        return mShareContent;
    }

    /**
     * ��ȡ��ǰ���õ���Ҫ��ʾ�ķ���ƽ̨
     * 
     * @return
     */
    public ArrayList<SharePlatformCode> getSharePlatform() {
        return mSharePlatformCodes;
    }

    /**
     * ��ȡ��ǰӦ��ͼƬ
     * 
     * @param packname
     * @param context
     * @return
     */
    private Bitmap getAppIcon(Context context) {
        PackageManager pm;
        Bitmap map = null;
        pm = context.getPackageManager();
        try {
            ApplicationInfo info = context.getApplicationInfo();

            Drawable draw = info.loadIcon(pm);
            BitmapDrawable bd = (BitmapDrawable) draw;
            map = bd.getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * ��������activity�ص�����Ϣ
     * Ŀǰ���ڽ���facebookʹ��webdialog��½��������Ϣ
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSharePlatformChooser.onActivityResult(requestCode, resultCode, data);
        mSharePlatformChooser = null;
    }
    /********************************* debug ***********************************/
    // private UMSocialService controller;
    //
    // public void umengShare(Context context, String title,
    // String content, Bitmap image, String url, String appID) {
    // controller = UMServiceFactory.getUMSocialService(
    // context.getPackageName(), RequestType.SOCIAL);
    // // controller.getConfig().setSinaSsoHandler(new
    // // SinaSsoHandler());
    // // �ر��ʼ������ŷ���
    // controller.getConfig().setShareMail(false);
    // controller.getConfig().setShareSms(false);
    // controller.getConfig().setPlatforms();
    // controller.getConfig().supportAppPlatform(context,
    // APP_PLATFORM.FACEBOOK, "Facebook", false);
    //
    // controller.setShareContent(content);
    // controller.setShareMedia(new UMImage(context, getAppIcon(context)));
    // // ����΢�ŷ���
    // UMWXHandler.WX_APPID = appID;
    // controller.getConfig().supportWXPlatform(context);
    // controller.getConfig().supportWXPlatform(
    // context,
    // UMServiceFactory.getUMWXHandler(context).setToCircle(
    // true));
    // // ����΢�ŷ�������Ӻͱ���
    // UMWXHandler.CONTENT_URL = url;
    // UMWXHandler.WX_CONTENT_TITLE = title;
    // UMWXHandler.WXCIRCLE_CONTENT_TITLE = title;
    //
    // // ���ڵİ�������ע�͵�
    // controller.getConfig().setMailSubject(title);
    // controller.getConfig().supportAppPlatform(context,
    // APP_PLATFORM.FACEBOOK, "Facebook", true);
    // controller.getConfig().setShareMail(true);
    //
    // controller.openShare(context, false);
    // }
}
