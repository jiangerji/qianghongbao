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
 * 蓝港分享SDK
 * 
 * @author jianglin@8864.com
 * 
 */
public class LKShareController {

    public static enum SharePlatformCode {
        /**
         * 微信好友分享
         */
        WeChat,

        /**
         * 微信朋友圈分享
         */
        WeChatCircle,

        /**
         * facebook分享
         */
        Facebook,

        /**
         * twitter分享
         */
        Twitter,

        /**
         * 使用gmail邮件客户端进行分享
         */
        Email
    };

    private static LKShareController _instance = null;

    private SharePlatformChooser mSharePlatformChooser = null;

    private LKShareController() {
        mSharePlatformCodes = new ArrayList<LKShareController.SharePlatformCode>();
    }

    /**
     * 获取蓝港分享实例
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
     * 添加支持的平台，会根据添加的顺序显示出来
     * 
     * @param sharePlatform
     *            分享平台的代码
     */
    public void addSharePlatform(SharePlatformCode sharePlatform) {
        if (!mSharePlatformCodes.contains(sharePlatform)) {
            mSharePlatformCodes.add(sharePlatform);
        }
    }

    /**
     * 删除分享的平台
     * 
     * @param sharePlatform
     *            分享平台的代码
     */
    public void removeSharePlatform(SharePlatformCode sharePlatform) {
        mSharePlatformCodes.remove(sharePlatform);
    }

    public static class ShareContent {

        public String title;
        public String content;
        public Bitmap image = null;
        public String imageUrl = null; // 目前该字段，就facebook使用
        public String url;
        public String appID;

        /**
         * 
         * @param title
         * @param content
         * @param imageUrl
         *            该字段只有facebook有用
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
     * 设置需要分享的内容，显示当前分享平台，用户选择后进行分享
     * 对于选择邮件分享，参数传入的图片对象无效，邮件不回发送图片对象
     * 
     * @param activity
     *            使用分享的activity实例
     * @param title
     *            分享的标题
     * @param content
     *            分享的内容
     * @param imageUrl
     *            图片的URL地址，目前facebook会使用
     * @param image
     *            分享的缩略图
     * @param url
     *            分享跳转的url
     * @param appID
     *            微信的app id
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

        Log.d("lkshare", "分享title:" + title);
        Log.d("lkshare", "分享content:" + content);
        Log.d("lkshare", "分享url:" + url);
        Log.d("lkshare", "分享appID:" + appID);
        Log.d("lkshare", "分享image url:" + imageUrl);
        mShareContent = new ShareContent(title, content, imageUrl, image, url,
                appID);
    }

    /**
     * 获取当前需要进行分享的内容
     * 
     * @return
     */
    public ShareContent getShareContent() {
        return mShareContent;
    }

    /**
     * 获取当前配置的需要显示的分享平台
     * 
     * @return
     */
    public ArrayList<SharePlatformCode> getSharePlatform() {
        return mSharePlatformCodes;
    }

    /**
     * 获取当前应用图片
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
     * 接收其他activity回调的消息
     * 目前用于接收facebook使用webdialog登陆回来的消息
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
    // // 关闭邮件、短信分享
    // controller.getConfig().setShareMail(false);
    // controller.getConfig().setShareSms(false);
    // controller.getConfig().setPlatforms();
    // controller.getConfig().supportAppPlatform(context,
    // APP_PLATFORM.FACEBOOK, "Facebook", false);
    //
    // controller.setShareContent(content);
    // controller.setShareMedia(new UMImage(context, getAppIcon(context)));
    // // 增加微信分享
    // UMWXHandler.WX_APPID = appID;
    // controller.getConfig().supportWXPlatform(context);
    // controller.getConfig().supportWXPlatform(
    // context,
    // UMServiceFactory.getUMWXHandler(context).setToCircle(
    // true));
    // // 设置微信分享的链接和标题
    // UMWXHandler.CONTENT_URL = url;
    // UMWXHandler.WX_CONTENT_TITLE = title;
    // UMWXHandler.WXCIRCLE_CONTENT_TITLE = title;
    //
    // // 国内的吧这两行注释掉
    // controller.getConfig().setMailSubject(title);
    // controller.getConfig().supportAppPlatform(context,
    // APP_PLATFORM.FACEBOOK, "Facebook", true);
    // controller.getConfig().setShareMail(true);
    //
    // controller.openShare(context, false);
    // }
}
