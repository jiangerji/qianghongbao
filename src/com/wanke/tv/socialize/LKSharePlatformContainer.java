/**
 * 
 */
package com.wanke.tv.socialize;

import java.util.Hashtable;

import com.wanke.tv.socialize.LKShareController.SharePlatformCode;
import com.wanke.tv.socialize.impl.BaseSharePlatform;

/**
 * @author jianglin@8864.com
 * 
 */
public class LKSharePlatformContainer {
    private static Hashtable<SharePlatformCode, Class<?>> mContainer = new Hashtable<LKShareController.SharePlatformCode, Class<?>>();

    /**
     * �������͵ķ���ƽ̨��ӵ���Դ����
     * 
     * @param sharePlatformCode
     * @param cls
     */
    public static void addSharePlatform(SharePlatformCode sharePlatformCode,
            Class<?> cls) {
        if (sharePlatformCode != null && cls != null) {
            mContainer.put(sharePlatformCode, cls);
        }
    }

    /**
     * ��ȡ��ǰ����ƽ̨�ľ���ʵ�ֶ���
     * 
     * @param sharePlatformCode
     *            ����ƽ̨����
     * @return
     *         ����ƽ̨����
     */
    public static BaseSharePlatform getSharePlatformImpl(
            SharePlatformCode sharePlatformCode) {
        BaseSharePlatform sharePlatform = null;

        Class<?> cls = mContainer.get(sharePlatformCode);
        if (cls != null) {
            try {
                Object object = cls.newInstance();
                if (object instanceof BaseSharePlatform) {
                    sharePlatform = (BaseSharePlatform) object;
                }
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return sharePlatform;
    }
}
