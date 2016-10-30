package com.zdc.smartcity.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.zdc.smartcity.utils.LogUtil;

public class TopNewsViewPager extends ViewPager {

	private int startX;
	private int startY;

	public TopNewsViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public TopNewsViewPager(Context context) {
		super(context);
	}

	/**
	 * 分情况处理手指滑动： 向左滑动到最后一个界面时拦截;向右滑动到第一个界面时拦截
	 **/
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// 请求分控件不要拦截
		getParent().requestDisallowInterceptTouchEvent(true);
		// 具体判断 需要交给父控件处理时请求拦截
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startX = (int) ev.getX();
			startY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int endX = (int) ev.getX();
			int endY = (int) ev.getY();

			int distanceX = endX - startX;
			int distanceY = endY - startY;

			if (Math.abs(distanceX) > Math.abs(distanceY)) {
				// 左右滑 具体判断是哪一个条目
				horizontalSlideJudgeItem(distanceX);
			} else {
				// 上下滑动 交给父类处理 父类拦截
				LogUtil.i("TopNewsViewPager", "上下滑动-父类处理");
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 左右滑 具体判断是哪一个条目
	 * 
	 * @param distanceX
	 **/
	private void horizontalSlideJudgeItem(int distanceX) {
		int currentItem = getCurrentItem();
		if (distanceX > 0) {// 往右滑并且是第一条
			if (currentItem == 0) {
				// 交给父类处理(拦截)
				LogUtil.i("TopNewsViewPager", "右滑第一条-父类处理");
				getParent().requestDisallowInterceptTouchEvent(false);
			}
		} else {
			int count = getAdapter().getCount();
			if (currentItem == count - 1) {
				// 往左滑并且是最后一条 交给父类处理(拦截)
				LogUtil.i("TopNewsViewPager", "左滑最后一条-父类处理");
				getParent().requestDisallowInterceptTouchEvent(false);
			}
		}
	}
}
