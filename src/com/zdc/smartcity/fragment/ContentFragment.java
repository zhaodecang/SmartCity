package com.zdc.smartcity.fragment;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.MainActivity;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.page.impl.GovAffairPage;
import com.zdc.smartcity.page.impl.HomePage;
import com.zdc.smartcity.page.impl.NewsCenterPage;
import com.zdc.smartcity.page.impl.SettingPage;
import com.zdc.smartcity.page.impl.SmartServicePage;

public class ContentFragment extends BaseFragment {

	/** 不能水平滑动的ViewPager **/
	private ViewPager nsvpContent;
	private RadioGroup rgGroup;
	private ArrayList<BasePage> mPages;

	@Override
	protected View initView() {
		View view = View.inflate(mActivity, R.layout.fragment_content, null);
		nsvpContent = (ViewPager) view.findViewById(R.id.nsvp_content);
		rgGroup = (RadioGroup) view.findViewById(R.id.rg_group);
		return view;
	}

	@Override
	protected void initData() {
		mPages = new ArrayList<BasePage>();
		mPages.add(new HomePage(mActivity));
		mPages.add(new NewsCenterPage(mActivity));
		mPages.add(new SmartServicePage(mActivity));
		mPages.add(new GovAffairPage(mActivity));
		mPages.add(new SettingPage(mActivity));

		nsvpContent.setAdapter(new ContentAdapter());
		// 给底部单选按钮设置选择改变监听事件
		rgGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_home:
					nsvpContent.setCurrentItem(0, false);
					break;
				case R.id.rb_news:
					nsvpContent.setCurrentItem(1, false);
					break;
				case R.id.rb_smart:
					nsvpContent.setCurrentItem(2, false);
					break;
				case R.id.rb_gov:
					nsvpContent.setCurrentItem(3, false);
					break;
				case R.id.rb_setting:
					nsvpContent.setCurrentItem(4, false);
					break;
				}
			}
		});
		nsvpContent.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				if (position != 0) {
					((HomePage) mPages.get(0)).resetLayout();
				}
				// 初始化被选中条目的数据
				mPages.get(position).initData();
				// 在指定内容页不显示菜单按钮
				if (position == 0 || position == mPages.size() - 1) {
					setSlidMenuEndble(false);
				} else {
					setSlidMenuEndble(true);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		// 手动加载加载一下主页的数据
		mPages.get(0).initData();
	}

	/**
	 * 是否可拖拽侧边栏
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

	/**
	 * 获取新闻中心页
	 * 
	 * @return NewsPage
	 */
	public NewsCenterPage getNewsCenterPager() {
		return (NewsCenterPage) mPages.get(1);
	}

	/**
	 * 获取设置页
	 * 
	 * @return NewsPage
	 */
	public SettingPage getSettingPager() {
		return (SettingPage) mPages.get(4);
	}

	private class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPages.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			BasePage page = mPages.get(position);
			View view = page.mRootView;
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
