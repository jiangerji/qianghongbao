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

import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

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
	private ImageButton mButton;
	private TextView mTextView;
	private View layout;
	boolean mState = true;
	PopupWindow mPopupWindow;
	public static final String WX_APP_ID = "wxe793cd583c6cb873";
	private IWXAPI wxApi;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID);
		wxApi.registerApp(WX_APP_ID);
		setContentView(R.layout.tasklist_main);
		mstart = (ImageView) findViewById(R.id.start);
		mShare = (ImageView) findViewById(R.id.mshare);
		mAbout = (ImageView) findViewById(R.id.about);
		mButton = (ImageButton) findViewById(R.id.button);
		mTextView = (TextView) findViewById(R.id.ming);
		// Add a shortcut to the accessibility settings.
		initListener();
	}

	private void initListener() {
		mstart.setOnClickListener(this);
		mShare.setOnClickListener(this);
		mAbout.setOnClickListener(this);
		mButton.setOnClickListener(this);
		mTextView.setOnClickListener(this);
	}

	private boolean mBorder;

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (view.getId()) {
		case R.id.start:
			if (mBorder) {

				mstart.setBackgroundResource(R.drawable.start);
				mBorder = false;
			} else {

				mstart.setBackgroundResource(R.drawable.stop);
				mBorder = true;
			}

			break;

		case R.id.mshare:
			if (mState) {
				mState = false;
				LayoutInflater inflater = LayoutInflater
						.from(view.getContext());
				layout = inflater.inflate(R.layout.share, null);
				mPopupWindow = new PopupWindow(layout,
						LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				mPopupWindow.setContentView(layout);
				mPopupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
				mPopupWindow.setOutsideTouchable(true);
				ImageView weixin = (ImageView) layout
						.findViewById(R.id.share_weixin);
				ImageView pengyouquan = (ImageView) layout
						.findViewById(R.id.share_pengyouquan);

				pengyouquan.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub

						if (wxApi.isWXAppInstalled()) {
							wechatShare(1);
						} else {
							Toast.makeText(getApplicationContext(),
									R.string.share_noweixin, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
				// weixin
				weixin.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (wxApi.isWXAppInstalled()) {
							wechatShare(0);
						} else {
							Toast.makeText(getApplicationContext(),
									R.string.share_noweixin, Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
			} else {
				mState = true;
				mPopupWindow.dismiss();
			}
			break;

		case R.id.about:
			Intent mIntent = new Intent();
			intent = new Intent(getApplicationContext(), explain.class);
			startActivity(intent);
			break;

		case R.id.button:
			startActivity(sSettingsIntent);
		case R.id.ming:

		default:
			break;
		}
	}

	private void wechatShare(int flag) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = "www.baidu.com";
		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = "这里填写标题";
		msg.description = "这里填写内容";

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

	public boolean isAccessibilityEnabled() {
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
