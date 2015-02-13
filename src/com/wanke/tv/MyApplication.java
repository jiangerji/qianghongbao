package com.wanke.tv;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.TextUtils;

import com.baidu.mobstat.StatService;

public class MyApplication extends Application {

    private static MyApplication mIam007Application;

    public static String getMeteDataByKey(Context mContext, String key) {
        try {
            ApplicationInfo appInfo = mContext
                    .getApplicationContext()
                    .getPackageManager()
                    .getApplicationInfo(mContext.getPackageName(),
                            PackageManager.GET_META_DATA);

            if (appInfo.metaData != null) {
                Object tmp = appInfo.metaData.get(key);
                if (tmp != null) {
                    String tmpValue = tmp.toString();
                    if (tmpValue.startsWith("<a>")) {
                        return tmpValue.substring(3);
                    } else {
                        return tmpValue;
                    }
                }
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mIam007Application = this;

        // 初始化百度统计
        String appChannel = getMeteDataByKey(this, "app_channel");
        if (TextUtils.isEmpty(appChannel)) {
            appChannel = "offical";
        }
        StatService.setAppChannel(this, appChannel, true);
        StatService.setSessionTimeOut(30);
    }

    public final static MyApplication getCurrentApplication() {
        return mIam007Application;
    }

}
