package com.zdc.smartcity.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.utils.ToastUtil;

public class SettingTestActivity extends Activity implements OnClickListener {

	private Context mContext;
	@ViewInject(R.id.wv_news_detail)
	private WebView wvNewsDetail;
	@ViewInject(R.id.rl_an_title)
	private RelativeLayout rl_an_title;
	private WebSettings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		mContext = this;
		ViewUtils.inject(this);
		rl_an_title.setVisibility(View.GONE);
		initData();
	}

	private void initData() {
		wvNewsDetail.loadUrl("file:///android_asset/setting/index.html");

		mSettings = wvNewsDetail.getSettings();
		mSettings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
		mSettings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
		mSettings.setJavaScriptEnabled(true);// 支持js功能
		setWebViewCilent();
		setWebChromeClient();
	}

	private void setWebChromeClient() {
		WebChromeClient webChromeClient = new WebChromeClient() {
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
			}

			@Override
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		};
		wvNewsDetail.setWebChromeClient(webChromeClient);
	}

	private void setWebViewCilent() {
		WebViewClient webViewClient = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				System.out.println("开始加载  显示进度条");
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				System.out.println("加载完成  隐藏进度条");
			}
		};
		wvNewsDetail.setWebViewClient(webViewClient);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_share:
			ToastUtil.show(mContext, "分享该新闻");
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && wvNewsDetail.canGoBack()) {
			wvNewsDetail.goBack();// 返回前一个页面
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
