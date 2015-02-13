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
 * ΢�Ź�����
 * 
 * @author dyc
 * 
 */
public class WxUtils {
    // APP_ID �滻Ϊ���Ӧ�ôӹٷ���վ���뵽�ĺϷ�appId
    private IWXAPI api;

    public WxUtils(Context context, String appID) {
        api = WXAPIFactory.createWXAPI(context, appID, true);
        api.registerApp(appID);
    }

    /**
     * ����Ƿ��Ѿ���װ΢��
     * 
     * @return
     *         true:�Ѱ�װ��false:δ��װ
     */
    public boolean isWXInstalled() {
        return api.isWXAppInstalled();
    }

    /**
     * �Ƿ�֧�ַ���������Ȧ
     * 
     * @return
     *         true:֧�֣�false:��֧��
     */
    public boolean isSupportCircle() {
        return api.getWXAppSupportAPI() >= 0x21020001;
    }

    /**
     * ����ֻ���ı���΢��
     * 
     * @param text
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline ���͵�����Ȧ
     *            SendMessageToWX.Req.WXSceneSession ���͵��Ự
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
     * ΢�ŷ���ͼƬ
     * 
     * @param imageData
     *            ͼƬ��������Դ
     * @param imagePath
     *            ͼƬ��·��
     * @param imageUrl
     *            ͼƬ��URL
     * @param width
     *            ��ʾͼƬ�Ŀ��
     * @param heigth
     *            ��ʾͼƬ�ĸ߶�
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline ���͵�����Ȧ
     *            SendMessageToWX.Req.WXSceneSession ���͵��Ự
     * 
     *            ע��:imageData imagePath imageUrlָ��һ�����У�����ͬʱΪ��
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
     * ��������
     * 
     * @param url
     *            ���ӵĵ�ַ
     * @param title
     *            ���ӵı���
     * @param text
     *            ���ӵ�����
     * @param bitmap
     *            ���ӵ�ͼƬ��Դ
     * @param width
     *            ͼƬ�Ŀ��
     * @param heigth
     *            ͼƬ�ĸ߶�
     * @param scene
     *            SendMessageToWX.Req.WXSceneTimeline ���͵�����Ȧ
     *            SendMessageToWX.Req.WXSceneSession ���͵��Ự
     */
    public void sendWebPageWx(Context context, String url, String title,
            String description, Bitmap bitmap, int scene) {
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title; // titleС��512
        msg.description = description; // descriptionС��1024
        if (bitmap != null) {
            // ������гߴ��С���ƣ�������32768
            msg.thumbData = getBitmapBytes(bitmap, false, 120, 120);
        }
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = System.currentTimeMillis() + "";
        req.message = msg;
        req.scene = scene;
        Log.d("lkshare", "���ͷ���title��" + title);
        Log.d("lkshare", "���ͷ���description��" + description);
        Log.d("lkshare", "���ͷ���url��" + url);
        Log.d("lkshare", "���ͷ���ͼƬ��"
                + (msg.thumbData != null ? msg.thumbData.length : 0));
        Log.d("lkshare", "���ͷ�������" + api.sendReq(req));
    }

    /**
     * һ�ֱȽ�tricky�ķ��������Դ�΢�ŵĽ��棬����ͼƬ�����ֵķ���
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
     * ͼƬ������, ��bitmap�д���width, height��С��bitmap���ֽ�
     * 
     * @param bitmap
     * @param recycle
     * @param width
     * @param heigth
     * @return
     */
    private static byte[] getBitmapBytes(Bitmap bitmap, boolean recycle,
            int width, int heigth) {
        // ������ͼƬ��С���ƣ�������565��ʽ
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
