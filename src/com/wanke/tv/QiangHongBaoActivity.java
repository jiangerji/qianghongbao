/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.wanke.tv;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wanke.tv.socialize.LKShareController;
import com.wanke.tv.socialize.LKShareController.SharePlatformCode;

/**
 * Starts up the task list that will interact with the AccessibilityService
 * sample.
 */
public class QiangHongBaoActivity extends Activity implements
        View.OnClickListener {

    /** An intent for launching the system settings. */
    private static final Intent sSettingsIntent = new Intent(
            Settings.ACTION_ACCESSIBILITY_SETTINGS);
    private ImageView mstart, mShare, mAbout;
    View mAuto, mVoice;
    private ImageButton mButton;
    // private TextView mTextView;
    private View layout;
    boolean mState = true;
    PopupWindow mPopupWindow;
    public static final String WX_APP_ID = "wxe793cd583c6cb873";

    private IWXAPI wxApi;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID);
        wxApi.registerApp(WX_APP_ID);
        setContentView(R.layout.tasklist_main);
        mstart = (ImageView) findViewById(R.id.start);
        mShare = (ImageView) findViewById(R.id.mshare);
        mAbout = (ImageView) findViewById(R.id.about);
        mButton = (ImageButton) findViewById(R.id.button);
        mVoice = findViewById(R.id.voice);
        // mTextView = (TextView) findViewById(R.id.ming);]
        mAuto = findViewById(R.id.enable_auto);

        initListener();
    }

    private void initListener() {
        mstart.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mAbout.setOnClickListener(this);
        mButton.setOnClickListener(this);
        mAuto.setOnClickListener(this);
        mVoice.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!isAccessibilityEnabled()) {
            open(false);
            mstart.setBackgroundResource(R.drawable.start);
        } else {
            if (isOpen()) {
                mstart.setBackgroundResource(R.drawable.stop2);
            } else {
                mstart.setBackgroundResource(R.drawable.start);
            }
        }

        mAuto.setSelected(isAuto());
        mVoice.setSelected(isVoice());

        if (isShared() && !isAccessibilityEnabled()) {
            showEnableServiceHintDialog();
        }

        if (isShared() && isAccessibilityEnabled()) {
            open(true);
            mstart.setBackgroundResource(R.drawable.stop2);

            SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                    Context.MODE_PRIVATE);
            Editor editor = settings.edit();
            editor.putBoolean("isAuto", true);
            editor.commit();
        }
    }

    private boolean isOpen() {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        return settings.getBoolean("isOpen", false);
    }

    private void open(boolean enable) {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean("isOpen", enable);
        editor.commit();

        //        int resId = R.string.already_open;
        //        if (!enable) {
        //            resId = R.string.already_close;
        //        }
        //
        //        Toast toast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        //        toast.setGravity(Gravity.CENTER, 0, 0);
        //        toast.show();
    }

    private boolean isShared() {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        return settings.getBoolean("isShared", false);
    }

    private boolean isAuto() {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        return settings.getBoolean("isAuto", false);
    }

    private void toggleAuto() {
        //        boolean isAuto = isAuto();
        //        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
        //                Context.MODE_PRIVATE);
        //        Editor editor = settings.edit();
        //        editor.putBoolean("isAuto", !isAuto);
        //        editor.commit();
        //
        //        if (!isAuto) {
        //            mstart.setBackgroundResource(R.drawable.stop2);
        //            open(true);
        //        }
    }

    Dialog mEnableServiceHintDialog = null;

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            if (mEnableServiceHintDialog != null) {
                mEnableServiceHintDialog.dismiss();
                mEnableServiceHintDialog = null;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected
            void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 0x123) {
            open(true);
            mstart.setBackgroundResource(R.drawable.stop2);
        }
    }

    private void showEnableServiceHintDialog() {

        mEnableServiceHintDialog = new Dialog(this, R.style.selectorDialog);
        mEnableServiceHintDialog.setContentView(R.layout.enable_service_hint_dialog);
        mEnableServiceHintDialog.findViewById(R.id.icon).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivityForResult(sSettingsIntent, 0x123);
                        mEnableServiceHintDialog.dismiss();
                    }
                });
        mEnableServiceHintDialog.findViewById(R.id.open_setting)
                .setOnClickListener(
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                startActivity(sSettingsIntent);
                                mEnableServiceHintDialog.dismiss();
                            }
                        });

        LayoutParams lay = mEnableServiceHintDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Rect rect = new Rect();
        View view = getWindow().getDecorView();
        view.getWindowVisibleDisplayFrame(rect);
        lay.height = dm.heightPixels - rect.top;
        lay.width = dm.widthPixels;

        mEnableServiceHintDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
        case R.id.start:
            if (!isShared()) {
                Toast toast = Toast.makeText(this,
                        R.string.start_hint,
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                showShare1(view);
            } else {
                if (isAccessibilityEnabled()) {
                    if (isOpen()) {
                        mstart.setBackgroundResource(R.drawable.start);
                        // 关闭
                        open(false);
                    } else {
                        mstart.setBackgroundResource(R.drawable.stop2);
                        // 打开
                        open(true);
                    }
                } else {
                    showEnableServiceHintDialog();
                }
            }

            break;

        case R.id.mshare:
            showShare1(view);
            break;

        case R.id.about:
            Intent mIntent = new Intent();
            intent = new Intent(getApplicationContext(), explain.class);
            startActivity(intent);
            StatService.onEvent(this, "help", "open");
            break;

        case R.id.enable_auto:
            if (isAccessibilityEnabled()) {
                if (isShared()) {
                    mAuto.setSelected(!isAuto());
                    toggleAuto();
                } else {
                    Toast toast = Toast.makeText(this, R.string.share_hint,
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    showShare1(view);
                }
            } else {
                showEnableServiceHintDialog();
            }
            break;

        case R.id.voice:
            toggleVoice();
            if (isVoice()) {
                mVoice.setSelected(true);
                //                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                //                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                mVoice.setSelected(false);
                //                AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
                //                audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            }
            break;

        default:
            break;
        }
    }

    private void showShare1(View view) {
        String url = "http://mp.weixin.qq.com/s?__biz=MzA3MTg5NDEyNQ==&mid=203628785&idx=1&sn=c8185fd99bd70bbf8e2a2cf7c595d622#rd";
        String content = "微信红包抢不过来，用自动抢红包工具帮你秒抢，无插件，无广告，安全，稳定。";
        String title = "自动抢红包工具";

        LKShareController controller = LKShareController.getInstance();
        controller.addSharePlatform(SharePlatformCode.WeChatCircle);
        controller.addSharePlatform(SharePlatformCode.WeChat);
        controller
                .invokeShare(this, title, content, null, null, url, WX_APP_ID);
    }

    private void showShare(View view) {
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        layout = inflater.inflate(R.layout.share, null);
        mPopupWindow = new PopupWindow(layout, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(layout);
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        mPopupWindow.setOutsideTouchable(true);
        ImageView weixin = (ImageView) layout.findViewById(R.id.share_weixin);
        ImageView pengyouquan = (ImageView) layout
                .findViewById(R.id.share_pengyouquan);

        pengyouquan.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (wxApi.isWXAppInstalled()) {
                    wechatShare(1);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.share_noweixin, Toast.LENGTH_SHORT).show();
                }

                // mPopupWindow.dismiss();
            }
        });
        // weixin
        weixin.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (wxApi.isWXAppInstalled()) {
                    wechatShare(0);
                } else {
                    Toast.makeText(getApplicationContext(),
                            R.string.share_noweixin, Toast.LENGTH_SHORT).show();
                }

                // mPopupWindow.dismiss();
            }
        });
    }

    private boolean isVoice() {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        return settings.getBoolean("isVoice", true);
    }

    private void toggleVoice() {
        boolean isVoice = isVoice();
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        Editor editor = settings.edit();
        editor.putBoolean("isVoice", !isVoice);
        editor.commit();
    }

    private void wechatShare(int flag) {
        String url = "http://mp.weixin.qq.com/s?__biz=MzA3MTg5NDEyNQ==&mid=203621455&idx=1&sn=ad9a080ad1ed1a7ff9a00dc1d3b79bb1&scene=1&key=8ea74966bf01cfb698e164751ddd837869a15740bf3ab312424d45339706abcac51cce2e6d4ff53685faa8c1ed6c7d34&ascene=1&uin=NTI0ODI3NDIw&devicetype=webwx&version=70000001&pass_ticket=O%2BZ3Ix%2BK5fGbq%2FsIz6RizKNnNSZjjsRD6pXviGzdsU5vn%2Fx%2BcJjLULeUEilRt1aS";
        String content = "微信红包抢不过来，用自动抢红包神器帮你秒抢，无插件，无广告，安全，稳定。";
        String title = "自动抢红包神器";

        Log.d("acc", "wechat share:" + flag);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = content;

        Bitmap thumb = BitmapFactory.decodeResource(getResources(),
                R.drawable.ic_launcher);
        msg.setThumbImage(thumb);

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = flag == 0 ? SendMessageToWX.Req.WXSceneSession
                : SendMessageToWX.Req.WXSceneTimeline;
        wxApi.sendReq(req);
    }

    private boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String LIGHTFLOW_ACCESSIBILITY_SERVICE = "com.wanke.tv/com.wanke.tv.QiangHongBaoService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    this.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);

        } catch (SettingNotFoundException e) {

        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(
                ':');

        if (accessibilityEnabled == 1) {

            String settingValue = Settings.Secure.getString(
                    getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    if (accessabilityService
                            .equalsIgnoreCase(LIGHTFLOW_ACCESSIBILITY_SERVICE)) {

                        return true;
                    }
                }
            }

        }

        return accessibilityFound;
    }

}
