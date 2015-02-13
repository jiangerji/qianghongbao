package com.wanke.tv;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

public class explain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_explain);
		WebView mWebView = (WebView) findViewById(R.id.web);
		mWebView.getSettings().setJavaScriptEnabled(true);
		// 加载需要显示的网页
		mWebView.loadUrl("http://www.knwan.cn/help.html");
	}

}
