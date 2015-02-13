/**
 * 
 */
package com.wanke.tv.socialize.view;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wanke.tv.socialize.LKShareController;
import com.wanke.tv.socialize.LKShareController.ShareContent;
import com.wanke.tv.socialize.LKShareController.SharePlatformCode;
import com.wanke.tv.socialize.LKSharePlatformContainer;
import com.wanke.tv.socialize.impl.BaseSharePlatform;

/**
 * @author jianglin@8864.com
 * 
 */
public class SharePlatformChooser extends Dialog {
    private Context mContext;

    private BaseSharePlatform mSharePlatform = null;

    /**
     * @param context
     */
    public SharePlatformChooser(Context context) {
        super(context, context.getResources().getIdentifier(
                "Theme.ShareActivity", "style", context.getPackageName()));
        mContext = context;

        initView();

        setCancelable(true);
        setCanceledOnTouchOutside(false);

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setLayout(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);

        WindowManager.LayoutParams localLayoutParams = getWindow()
                .getAttributes();
        localLayoutParams.softInputMode = WindowManager.LayoutParams.ANIMATION_CHANGED;
        getWindow().setAttributes(localLayoutParams);
    }

    private void initView() {
        LinearLayout mContainer = new LinearLayout(mContext);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        mContainer.setLayoutParams(layoutParams);
        mContainer.setBackgroundColor(Color.WHITE);
        mContainer.setGravity(Gravity.CENTER);

        GridView gridView = new GridView(mContext);
        gridView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        gridView.setGravity(Gravity.CENTER);
        gridView.setSelector(mContext.getResources().getIdentifier(
                "lk_share_item_bg", "drawable", mContext.getPackageName()));

        ArrayList<SharePlatformCode> sharePlatformCodes = LKShareController
                .getInstance().getSharePlatform();
        int size = sharePlatformCodes.size();
        if (size <= 4) {
            gridView.setNumColumns(size);
        } else {
            gridView.setNumColumns(4);
        }

        gridView.setAdapter(new ImageAdapter(sharePlatformCodes));
        mContainer.addView(gridView);

        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (view != null) {
                    mSharePlatform = (BaseSharePlatform) view.getTag();
                    if (mSharePlatform != null) {
                        ShareContent shareContent = LKShareController
                                .getInstance().getShareContent();

                        if (shareContent != null) {
                            mSharePlatform.invokeShare(mContext,
                                    shareContent.appID, shareContent);
                        }
                    }
                }

                dismiss();
            }
        });

        setContentView(mContainer);
    }

    private class ImageAdapter extends BaseAdapter {
        private ArrayList<SharePlatformCode> mSharePlatforms;
        private int mLayoutID;

        public ImageAdapter(ArrayList<SharePlatformCode> sharePlatforms) {
            mSharePlatforms = sharePlatforms;
            mLayoutID = mContext.getResources().getIdentifier(
                    "lk_share_btn_layout", "layout", mContext.getPackageName());
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getCount()
         */
        @Override
        public int getCount() {
            return mSharePlatforms.size();
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItem(int)
         */
        @Override
        public Object getItem(int position) {
            return null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getItemId(int)
         */
        @Override
        public long getItemId(int position) {
            return position;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.widget.Adapter#getView(int, android.view.View,
         * android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout linearLayout = null;
            if (convertView == null) {
                linearLayout = (LinearLayout) View.inflate(mContext, mLayoutID,
                        null);
            } else {
                linearLayout = (LinearLayout) convertView;
            }

            TextView title = (TextView) linearLayout
                    .findViewWithTag("share_platform_title");
            ImageView imageView = (ImageView) linearLayout
                    .findViewWithTag("share_platform_image");
            BaseSharePlatform sharePlatform = LKSharePlatformContainer
                    .getSharePlatformImpl(mSharePlatforms.get(position));
            linearLayout.setTag(sharePlatform);

            title.setText(sharePlatform.getStringID(mContext));
            imageView.setImageResource(sharePlatform.getImageID(mContext));

            return linearLayout;
        }

    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mSharePlatform != null) {
            mSharePlatform.onActivityResult(requestCode, resultCode, data);
        }
    }
}
