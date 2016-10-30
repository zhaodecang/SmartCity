package com.zdc.smartcity.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class NoScrollViewPage extends ViewPager {

	public NoScrollViewPage(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public NoScrollViewPage(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		// return true 表示响应事件
		// return false 表示不响应事件
		return false;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		return false;// 不拦截事件的传递
	}

}
