package com.zdc.smartcity.page.impl.menu;

import com.zdc.smartcity.page.BaseMenuDetailPage;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

public class InteractMenuDetail extends BaseMenuDetailPage {

	public InteractMenuDetail(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView view = new TextView(mActivity);
		view.setText("详情页-互动");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		view.setGravity(Gravity.CENTER);
		return view;
	}
}
