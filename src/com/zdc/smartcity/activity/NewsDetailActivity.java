package com.zdc.smartcity.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;

public class NewsDetailActivity extends Activity implements OnClickListener {

	private Context mContext;
	@ViewInject(R.id.wv_news_detail)
	private WebView wvNewsDetail;
	@ViewInject(R.id.ib_back)
	private ImageButton ibBack;
	@ViewInject(R.id.ib_textsize)
	private ImageButton ibTextSize;
	@ViewInject(R.id.ib_share)
	private ImageButton ibShare;
	@ViewInject(R.id.tv_news_title)
	private TextView tvNewsTitle;
	@ViewInject(R.id.pb_loading_more)
	private ProgressBar pbLoading;
	private int mCurrentSize = 2;
	private final String[] items = { "超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体" };
	private final int[] textSize = { 200, 150, 100, 75, 50 };
	private WebSettings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newsdetail);
		mContext = this;
		ViewUtils.inject(this);
		initData();
		setBtnClick();
	}

	private void initData() {
		String url = getIntent().getStringExtra("url");
		wvNewsDetail.loadUrl(url);
		mSettings = wvNewsDetail.getSettings();
		mSettings.setBuiltInZoomControls(true);// 显示缩放按钮(wap网页不支持)
		mSettings.setUseWideViewPort(true);// 支持双击缩放(wap网页不支持)
		mSettings.setJavaScriptEnabled(true);// 支持js功能
		setWebViewCilent();
		setWebChromeClient();
	}

	/** 设置各按钮的点击事件 **/
	private void setBtnClick() {
		ibBack.setOnClickListener(this);
		ibTextSize.setOnClickListener(this);
		ibShare.setOnClickListener(this);
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
				tvNewsTitle.setText(title);
			}
		};
		wvNewsDetail.setWebChromeClient(webChromeClient);
	}

	private void setWebViewCilent() {
		WebViewClient webViewClient = new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				pbLoading.setVisibility(View.VISIBLE);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				pbLoading.setVisibility(View.INVISIBLE);
			}
		};
		wvNewsDetail.setWebViewClient(webViewClient);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_back:
			finish();
			break;
		case R.id.ib_textsize:
			showTextSizeDialog();
			break;
		case R.id.ib_share:
			showShare();
			break;
		}
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// text是分享文本，所有平台都需要这个字段
		oks.setText("把这条新闻分享给到你的朋友圈");
		// 分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath("/sdcard/share.jpg");// 确保SDcard下面存在此张图片
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("SmartCity.cn");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://SmartCity.cn.cn");
		// 启动分享GUI
		oks.show(this);
	}

	/** 显示选择字体大小的弹出对话框 **/
	private void showTextSizeDialog() {
		Builder builder = new Builder(mContext);
		builder.setTitle("请选择合适的字体大小");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mCurrentSize = which;
				changeTextSize();
				dialog.dismiss();
			}
		};
		builder.setSingleChoiceItems(items, mCurrentSize, listener);
		builder.show();
	}

	/** 修改当前浏览器的字体大小 **/
	private void changeTextSize() {
		mSettings.setTextZoom(textSize[mCurrentSize]);
	}
}
