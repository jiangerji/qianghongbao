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

import java.util.ArrayList;
import java.util.List;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.baidu.mobstat.StatService;

/**
 * This class demonstrates how an accessibility service can query
 * window content to improve the feedback given to the user.
 */
public class QiangHongBaoService extends AccessibilityService {

    static {
        System.loadLibrary("hb");
    }

    public native static int home();

    public native static int touch(int x, int y);

    /** Tag for logging. */
    private static final String LOG_TAG = "acc";

    /**
     * {@inheritDoc}
     */
    @Override
    public void onServiceConnected() {
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.notificationTimeout = 100;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);

        isAccessibilityEnabled();
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

    private boolean isOpen() {
        SharedPreferences settings = this.getSharedPreferences("qianghongbao",
                Context.MODE_PRIVATE);
        return settings.getBoolean("isOpen", false);
    }

    static boolean isInWX = false;

    static boolean inChaiHongBao = false;

    /**
     * Processes an AccessibilityEvent, by traversing the View's tree and
     * putting together a message to speak to the user.
     */
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (!isOpen()) {
            return;
        }

        boolean isAuto = false;

        if (isShared() && isAuto()) {
            isAuto = true;
        }

        int type = event.getEventType();
        List<CharSequence> texts = event.getText();
        //        Log.d("acc", "type:" + type);
        if (type == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            for (CharSequence cs : texts) {
                String aString = cs.toString();
                if (aString.contains("微信红包")) {
                    Notification parcelable = (Notification) event.getParcelableData();
                    try {
                        parcelable.contentIntent.send();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }

                    StatService.onEvent(this, "noti", "open");

                    playSound();
                    break;
                }
            }
        }

        if (!isAuto) {
            return;
        }

        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            //            Log.d(LOG_TAG,
            //                    "TYPE_WINDOW_STATE_CHANGED:" + event.getPackageName()
            //                            + ", " + event.getClassName());
            boolean isEnterWX = false;
            for (CharSequence cs : texts) {
                //                Log.d("acc", "text:" + cs);
                String aString = cs.toString();
                if (aString.contains("微信")) {
                    isEnterWX = true;
                }

            }

            if (event.getClassName()
                    .equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI")) {
                // 进入抢红包界面
                AccessibilityNodeInfo source = event.getSource();
                chaihongbao(source);
                inChaiHongBao = true;
                StatService.onEvent(this, "chai", "open");
                return;
            }

            if (event.getClassName()
                    .equals("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI")) {
                inChaiHongBao = true;
                return;
            }

            if (isEnterWX) {
                if (isInWX) {
                    return;
                }

                isInWX = true;
                // 进入微信界面了
                //                Log.d(LOG_TAG, "进入微信界面了！");
                AccessibilityNodeInfo source = event.getSource();
                ArrayList<AccessibilityNodeInfo> nodeInfos = new ArrayList<AccessibilityNodeInfo>();
                enterHongBao(source, nodeInfos);
                if (nodeInfos.size() > 0) {
                    //                    Log.d(LOG_TAG, "进入拆红包界面");
                    nodeInfos.get(nodeInfos.size() - 1).getParent()
                            .performAction(0x10);
                }
                return;
            } else {
                if (!event.getClassName()
                        .toString()
                        .startsWith("com.tencent.mm")) {
                    isInWX = false;
                }
            }
        }

        if (type == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if (inChaiHongBao) {
                return;
            }

            //            Log.d(LOG_TAG,
            //                    "TYPE_WINDOW_CONTENT_CHANGED:" + event.getPackageName()
            //                            + ", " + event.getClassName() + ", "
            //                            + event.getText());
            if (event.getPackageName().equals("com.tencent.mm")) {
                if (inWxContentChanged == true) {
                    if (inChaiHongBao) {
                        inWxContentChanged = false;
                    }
                    inChaiHongBao = false;
                    return;
                }
            } else {
                inWxContentChanged = false;
                inChaiHongBao = false;
                return;
            }
            inChaiHongBao = false;

            AccessibilityNodeInfo source = event.getSource();
            if (source == null) {
                return;
            }

            ArrayList<AccessibilityNodeInfo> nodeInfos = new ArrayList<AccessibilityNodeInfo>();
            enterHongBao(source, nodeInfos);
            if (nodeInfos.size() > 0) {
                //                Log.d(LOG_TAG, "  进入拆红包界面");
                nodeInfos.get(nodeInfos.size() - 1).getParent()
                        .performAction(0x10);
                inWxContentChanged = true;
            }
        }
    }

    MediaPlayer mMediaPlayer = null;

    private void playSound() {
        if (mMediaPlayer != null) {
            return;
        }

        mMediaPlayer = new MediaPlayer();
        //读取raw文件夹下的mp3文件
        mMediaPlayer = MediaPlayer.create(this, R.raw.qiang);
        mMediaPlayer.start();
        mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
        });
    }

    static boolean inWxContentChanged = false;

    static int depth = 0;

    /**
     * 执行拆红包
     * 
     * @param nodeInfo
     */
    private void chaihongbao(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo == null) {
            return;
        }

        String log = "";
        for (int i = 0; i < depth; i++) {
            log += "  ";
        }

        //        Log.d(LOG_TAG, log + nodeInfo.getClassName() + ":" + nodeInfo.getText());
        if (nodeInfo.getChildCount() > 0) {
            depth++;
            AccessibilityNodeInfo childNodeInfo = null;
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                childNodeInfo = nodeInfo.getChild(i);
                CharSequence cs = childNodeInfo.getText();
                if (cs != null && cs.toString().equals("拆红包")) {
                    childNodeInfo.performAction(0x10); // AccessibilityNodeInfo.ACTION_CLICK
                }
            }
            depth--;
        }
    }

    /**
     * 进入拆红包界面
     * 
     * @param nodeInfo
     * @param nodeInfos
     */
    private void enterHongBao(
            AccessibilityNodeInfo nodeInfo,
            ArrayList<AccessibilityNodeInfo> nodeInfos) {
        if (nodeInfo == null) {
            return;
        }

        String log = "";
        for (int i = 0; i < depth; i++) {
            log += "  ";
        }

        //        Log.d(LOG_TAG, log + nodeInfo.getClassName() + ":" + nodeInfo.getText());
        CharSequence text = nodeInfo.getText();
        if (text != null) {
            if (text.toString().contains("微信红包")) {
                //                Log.d(LOG_TAG, log + "  加入到微信红包");
                if (nodeInfos != null) {
                    nodeInfos.add(nodeInfo);
                }
            }
        }
        if (nodeInfo.getChildCount() > 0) {
            depth++;
            for (int i = 0; i < nodeInfo.getChildCount(); i++) {
                enterHongBao(nodeInfo.getChild(i), nodeInfos);
            }
            depth--;
        }

    }

    @Override
    public boolean onUnbind(Intent intent) {
        //        Log.d(LOG_TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onInterrupt() {
        //        Log.d(LOG_TAG, "onInterrupt");
    }

    public boolean isAccessibilityEnabled() {
        int accessibilityEnabled = 0;
        final String LIGHTFLOW_ACCESSIBILITY_SERVICE = "com.wanke.tv/com.wanke.tv.QiangHongBaoService";
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(this.getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.d(LOG_TAG, "ACCESSIBILITY: " + accessibilityEnabled);
        } catch (SettingNotFoundException e) {
            Log.d(LOG_TAG,
                    "Error finding setting, default accessibility to not found: "
                            + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.d(LOG_TAG, "***ACCESSIBILIY IS ENABLED***: ");

            String settingValue = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            Log.d(LOG_TAG, "Setting: " + settingValue);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    Log.d(LOG_TAG, "Setting: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(LIGHTFLOW_ACCESSIBILITY_SERVICE)) {
                        Log.d(LOG_TAG,
                                "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }

            Log.d(LOG_TAG, "***END***");
        }
        else {
            Log.d(LOG_TAG, "***ACCESSIBILIY IS DISABLED***");
        }
        return accessibilityFound;
    }

}
