package com.zdc.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import cn.jpush.android.api.JPushInterface;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.zdc.smartcity.R;
import com.zdc.smartcity.fragment.ContentFragment;
import com.zdc.smartcity.fragment.LeftFragment;
import com.zdc.smartcity.page.impl.SettingPage;

public class MainActivity extends SlidingFragmentActivity {
	/** 表示侧边栏fragment **/
	private static final String TAG_LEFT_MENU = "tag_left_menu";
	/** 表示主页内容区的fragment **/
	private static final String TAG_MAIN_CONTENT = "tag_main_content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setBehindContentView(R.layout.fragment_left_menu);
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		slidingMenu.setBehindScrollScale(0.3f);
		// slidingMenu.setBehindOffset(200);
		slidingMenu.setBehindWidthRes(R.dimen.SlidingMenuBehindWidth);
		initFragment();
		// 初始化推送
		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
	}

	private void initFragment() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.replace(R.id.fl_left, new LeftFragment(), TAG_LEFT_MENU);
		transaction.replace(R.id.fl_main, new ContentFragment(), TAG_MAIN_CONTENT);
		transaction.commit();
	}

	/** 侧边栏fragment **/
	public LeftFragment getLeftFragment() {
		FragmentManager manager = getSupportFragmentManager();
		return (LeftFragment) manager.findFragmentByTag(TAG_LEFT_MENU);
	}

	/** 主页内容区的fragment **/
	public ContentFragment getContentFragment() {
		FragmentManager manager = getSupportFragmentManager();
		return (ContentFragment) manager.findFragmentByTag(TAG_MAIN_CONTENT);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		SettingPage settingPager = getContentFragment().getSettingPager();
		settingPager.OnActivityResult(requestCode, resultCode, data);
	}
}
