/**
 * 
 */
package com.wanke.tv.socialize.impl;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.wanke.tv.socialize.LKShareController.ShareContent;

/**
 * @author jianglin@8864.com
 * 
 */
@SuppressLint("DefaultLocale")
public class LKEmailImpl extends BaseSharePlatform {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#getStringID(android.content.Context
     * )
     */
    @Override
    public int getStringID(Context context) {
        return context.getResources().getIdentifier("lk_share_email", "string",
                context.getPackageName());
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
        return context.getResources().getIdentifier("lk_share_email",
                "drawable", context.getPackageName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#invokeShare(android.content.Context
     * , java.lang.String, java.lang.String, android.graphics.Bitmap,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void invokeShare(Context context, String appID,
            ShareContent shareContent) {
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.putExtra(Intent.EXTRA_TITLE, "Share");
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_SUBJECT, shareContent.title);
        intent.putExtra(Intent.EXTRA_TEXT,
                Html.fromHtml(shareContent.content + shareContent.url));

        // TODO:添加图片

        // 获取gmail客户端
        try {
            PackageManager pmManager = context.getPackageManager();
            ResolveInfo gmailResolveInfo = null;
            List<ResolveInfo> resolveInfos = pmManager.queryIntentActivities(
                    intent, 0);
            for (ResolveInfo resolveInfo : resolveInfos) {
                if (resolveInfo.activityInfo.packageName.endsWith(".gm")
                        && resolveInfo.activityInfo.name.toLowerCase()
                                .contains("gmail")) {
                    gmailResolveInfo = resolveInfo;
                }
            }

            if (gmailResolveInfo != null) {
                intent.setClassName(gmailResolveInfo.activityInfo.packageName,
                        gmailResolveInfo.activityInfo.name);
            }
        } catch (Exception e) {
            Log.d("lkshare", "没有找到gmail客户端！");
        }

        try {
            context.startActivity(intent);
        } catch (Exception e) {
            int errorId = context.getResources().getIdentifier(
                    "lk_share_email_error", "string", context.getPackageName());
            Toast.makeText(context, errorId, Toast.LENGTH_SHORT).show();
            Log.d("lkshare", "使用邮件分享出错：" + e.getMessage());
        }
    }
}
