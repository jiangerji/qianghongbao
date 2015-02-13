/**
 * 
 */
package com.wanke.tv.socialize.impl;

import android.content.Context;
import android.widget.Toast;

import com.wanke.tv.socialize.LKShareController.ShareContent;

/**
 * @author jianglin@8864.com
 * 
 */
public class LKTwitterImpl extends BaseSharePlatform {

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.lk.socialize.impl.BaseSharePlatform#getStringID(android.content.Context
     * )
     */
    @Override
    public int getStringID(Context context) {
        return context.getResources().getIdentifier("lk_share_twitter",
                "string", context.getPackageName());
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
        return context.getResources().getIdentifier("lk_share_twitter",
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
        Toast.makeText(context, "暂不支持分享到Twitter！", Toast.LENGTH_SHORT).show();
    }

}
