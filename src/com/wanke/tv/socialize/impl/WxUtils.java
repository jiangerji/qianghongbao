package com.wanke.tv.socialize.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXImageObject;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信工具类
 * 
 * @author dyc
 * 
 */
public class WxUtils {
    // APP_ID 替换为你的应用从官方网站申请到的合法appId
    private IWXAPI api;

    public WxUtils(Context context, String appID) {
        api = WXAPIFactory.createWXAPI(context, appID, true);
        api.registerApp(appID);
    }

    /**
     * 检查是否已经安装微信
     * 
     * @return
     *         true:已安装，false:未安装
     */
    public boolean isWXInstalled() {
        return api.isWXAppInstalled();
    }

    /**
     * 是否支持发布到朋友圈
     * 
     * @return
     *         true:支持，false:不支持
     */
    public boolean isSupportCircle() {
        return api.getWXAppSupportAPI() >= 0x21020001;
    }

    /**
     * 发布只带文本的微信
     * 
     * @param text
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline 发送到朋友圈
     *            SendMessageToWX.Req.WXSceneSession 发送到会话
     * 
     */
    public void sendWx(String text, int scene) {
        WXTextObject wXTextObject = new WXTextObject(text);
        WXMediaMessage wXMediaMessage = new WXMediaMessage();
        wXMediaMessage.mediaObject = wXTextObject;
        wXMediaMessage.description = text;
        SendMessageToWX.Req request = new SendMessageToWX.Req();
        request.transaction = System.currentTimeMillis() + "";
        request.message = wXMediaMessage;
        api.sendReq(request);

    }

    /**
     * 微信发布图片
     * 
     * @param imageData
     *            图片的数组资源
     * @param imagePath
     *            图片的路径
     * @param imageUrl
     *            图片的URL
     * @param width
     *            显示图片的宽度
     * @param heigth
     *            显示图片的高度
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline 发送到朋友圈
     *            SendMessageToWX.Req.WXSceneSession 发送到会话
     * 
     *            注意:imageData imagePath imageUrl指定一个就行，不能同时为空
     */
    public void sendImageWx(byte[] imageData, String imagePath,
            String imageUrl, int width, int heigth, int scene) {
        WXImageObject imgObj = new WXImageObject();
        if (imageData != null) {
            imgObj.imageData = imageData;
        }
        if (imagePath != null) {
            imgObj.imagePath = imagePath;
        }
        if (imageUrl != null) {
            imgObj.imageUrl = imageUrl;
        }
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = imgObj;
        try {
            Bitmap bmp = BitmapFactory.decodeStream(new URL(imageUrl)
                    .openStream());
            byte[] bytes = getBitmapBytes(bmp, false, width, heigth);
            msg.thumbData = bytes;
            SendMessageToWX.Req req = new SendMessageToWX.Req();
            req.transaction = System.currentTimeMillis() + "";
            req.message = msg;
            req.scene = scene;
            api.sendReq(req);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 发布链接
     * 
     * @param url
     *            链接的地址
     * @param title
     *            链接的标题
     * @param text
     *            链接的内容
     * @param bitmap
     *            链接的图片资源
     * @param width
     *            图片的宽度
     * @param heigth
     *            图片的高度
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline 发送到朋友圈
     *            SendMessageToWX.Req.WXSceneSession 发送到会话
     */
    public void sendWebPageWx(Context context, String url, String title,
            String description, Bitmap bitmap, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title; // title小于512
        msg.description = description; // description小于1024
        if (bitmap != null) {
            // 这个是有尺寸大小限制，不大于32768
            msg.thumbData = getBitmapBytes(bitmap, false, 120, 120);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.currentTimeMillis() + "";
        req.message = msg;
        req.scene = scene;
        Log.d("lkshare", "发送分享title：" + title);
        Log.d("lkshare", "发送分享description：" + description);
        Log.d("lkshare", "发送分享url：" + url);
        Log.d("lkshare", "发送分享图片："
                + (msg.thumbData != null ? msg.thumbData.length : 0));
        Log.d("lkshare", "发送分享请求：" + api.sendReq(req));
    }

    /**
     * 一种比较tricky的方法，可以打开微信的界面，进行图片和文字的分享
     * 
     * @param activity
     * @param paramString
     */
    private void startWeixinPublisher(Activity activity, String description) {
        Intent localIntent = new Intent();
        localIntent.setComponent(new ComponentName("com.tencent.mm",
                "com.tencent.mm.ui.tools.ShareToTimeLineUI"));
        localIntent.setAction("android.intent.action.SEND");
        localIntent.setType("image/*");
        localIntent.setFlags(1);

        localIntent.putExtra("Kdescription", description);
        localIntent.putExtra("android.intent.extra.STREAM", Uri
                .fromFile(new File(Environment.getExternalStorageDirectory(),
                        "test.png")));
        try {
            activity.startActivity(localIntent);
            return;
        } catch (ActivityNotFoundException localActivityNotFoundException) {

        }
    }

    /**
     * 图片处理类, 从bitmap中创建width, height大小的bitmap的字节
     * 
     * @param bitmap
     * @param recycle
     * @param width
     * @param heigth
     * @return
     */
    private static byte[] getBitmapBytes(Bitmap bitmap, boolean recycle,
            int width, int heigth) {
        // 由于有图片大小限制，所以用565格式
        Bitmap thumbBmp = Bitmap.createBitmap(width, heigth,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(thumbBmp);

        int origWidth = bitmap.getWidth();
        int origHeight = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.postScale(width / (float) origWidth, heigth / (float) origHeight);

        canvas.drawColor(Color.TRANSPARENT);
        canvas.drawBitmap(bitmap, matrix, null);

        return bmpToByteArray(thumbBmp, true);
    }

    private static byte[] bmpToByteArray(final Bitmap bmp,
            final boolean needRecycle) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bmp.compress(CompressFormat.PNG, 100, output);
        if (needRecycle) {
            bmp.recycle();
        }

        byte[] result = output.toByteArray();
        try {
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
