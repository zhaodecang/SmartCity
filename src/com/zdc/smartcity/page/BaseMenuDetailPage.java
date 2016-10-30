package com.zdc.smartcity.page;

import android.app.Activity;
import android.view.View;

public abstract class BaseMenuDetailPage {
	public Activity mActivity;
	public View menuDetailPagerView;

	public BaseMenuDetailPage(Activity activity) {
		mActivity = activity;
		menuDetailPagerView = initView();
	}

	public abstract View initView();

	public void initData() {

	}
}
