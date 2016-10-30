package com.zdc.smartcity.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.zdc.smartcity.R;
import com.zdc.smartcity.global.ConstantValue;
import com.zdc.smartcity.utils.SpUtil;

public class GuideActivity extends Activity {

	private Context mContext;
	private ViewPager vpGuide;
	private Button btnStart;
	private LinearLayout llContainer;
	private ImageView ivRedPoint;
	private int[] mResIds;
	private ArrayList<ImageView> mImageViews;
	private int mPointDis;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_guide);
		initUI();
		initData();
		vpGuide.setAdapter(new GuideAdapter());
		// 设置滑动监听
		vpGuide.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 如果是ViewPager的最后一条就显示button
				if (position == mImageViews.size() - 1) {
					btnStart.setVisibility(View.VISIBLE);
				} else {
					btnStart.setVisibility(View.INVISIBLE);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int pixels) {
				int marginLeft = (int) ((position + positionOffset) * mPointDis);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivRedPoint
						.getLayoutParams();
				params.leftMargin = marginLeft;
				ivRedPoint.setLayoutParams(params);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		// 获取视图树 设置监听
		ViewTreeObserver observer = ivRedPoint.getViewTreeObserver();
		observer.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				// 移除监听 避免重复回调
				ivRedPoint.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				// 布局加载完成之后获取左边距
				mPointDis = llContainer.getChildAt(1).getLeft()
						- llContainer.getChildAt(0).getLeft();
			}
		});
	}

	private void initData() {
		mResIds = new int[] { R.drawable.guide_1, R.drawable.guide_2,
				R.drawable.guide_3 };
		mImageViews = new ArrayList<ImageView>();
		for (int i = 0; i < mResIds.length; i++) {
			// 初始化viewpage的内容
			ImageView imageView = new ImageView(mContext);
			imageView.setBackgroundResource(mResIds[i]);
			mImageViews.add(imageView);
			// 初始化小圆点
			ImageView pointView = new ImageView(mContext);
			pointView.setBackgroundResource(R.drawable.shape_point_gray);
			// 初始化小圆点的布局参数
			LinearLayout.LayoutParams params = new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (i > 0) {
				params.leftMargin = 10;
			}
			pointView.setLayoutParams(params);
			llContainer.addView(pointView, params);
		}
	}

	/** ViewPager的数据适配器 **/
	private class GuideAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mImageViews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = mImageViews.get(position);
			container.addView(view);
			return view;
		}
	}

	private void initUI() {
		vpGuide = (ViewPager) findViewById(R.id.vp_guide);
		btnStart = (Button) findViewById(R.id.btn_start);
		llContainer = (LinearLayout) findViewById(R.id.ll_container);
		ivRedPoint = (ImageView) findViewById(R.id.iv_red_point);
		btnStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				SpUtil.putBoolean(mContext, ConstantValue.IS_FIRST_ENTERED, false);
				startActivity(new Intent(mContext, MainActivity.class));
				finish();
			}
		});
	}
}
