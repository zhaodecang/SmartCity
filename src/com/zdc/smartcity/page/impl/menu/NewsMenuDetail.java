package com.zdc.smartcity.page.impl.menu;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.MainActivity;
import com.zdc.smartcity.domain.NewsMenuBean.NewsItemData;
import com.zdc.smartcity.page.BaseMenuDetailPage;
import com.zdc.smartcity.utils.LogUtil;

public class NewsMenuDetail extends BaseMenuDetailPage implements OnPageChangeListener {

	private static final String tag = "NewsMenuDetail";
	@ViewInject(R.id.vp_news_menu_detail)
	private ViewPager mViewPager;
	@ViewInject(R.id.indicator)
	private TabPageIndicator mTabIndicator;
	private List<NewsItemData> mTabData;
	private List<NewsTabDetail> mPagers;

	private int preTabPosition = 0;

	public NewsMenuDetail(Activity activity, List<NewsItemData> _children) {
		super(activity);
		this.mTabData = _children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		mPagers = new ArrayList<NewsTabDetail>();
		for (int i = 0; i < mTabData.size(); i++) {
			NewsTabDetail pager = new NewsTabDetail(mActivity, mTabData.get(i));
			mPagers.add(pager);
		}
		mViewPager.setAdapter(new NewsMenuDetailAdapter());
		mTabIndicator.setViewPager(mViewPager);
		// mViewPager.setOnPageChangeListener(this);
		// 给指示器设置监听器
		mTabIndicator.setOnPageChangeListener(this);
	}

	@Override
	public void onPageSelected(int position) {
		if (preTabPosition != position) {
			mPagers.get(preTabPosition).removeHandlerMessage();
			LogUtil.i(tag, "切换到不同的页签,移除上一个消息队列的消息");
		}
		mPagers.get(position).initData();
		preTabPosition = position;
		if (position == 0) {
			setSlidMenuEndble(true);
		} else {
			setSlidMenuEndble(false);
		}
	}

	class NewsMenuDetailAdapter extends PagerAdapter {

		@Override
		public CharSequence getPageTitle(int position) {
			return mTabData.get(position).title;
		}

		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			NewsTabDetail tabDetailPager = mPagers.get(position);
			View view = tabDetailPager.menuDetailPagerView;
			if (position == 0) {
				tabDetailPager.initData();
			}
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	@OnClick(R.id.btn_next)
	public void nextTab(View view) {
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
	}

	/**
	 * 是否可拖拽
	 * 
	 * @param enable
	 */
	private void setSlidMenuEndble(boolean enable) {
		MainActivity activity = (MainActivity) mActivity;
		SlidingMenu slidingMenu = activity.getSlidingMenu();
		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}
}
