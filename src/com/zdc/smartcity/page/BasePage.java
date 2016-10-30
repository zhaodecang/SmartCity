package com.zdc.smartcity.page;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.MainActivity;

public abstract class BasePage {

	protected Activity mActivity;
	protected TextView tvTitle;
	protected ImageButton ibMenu;
	protected FrameLayout flContent;
	protected RelativeLayout rlTitleBar;
	protected ImageButton ibPicStyle;
	public View mRootView;

	public BasePage(Activity activity) {
		this.mActivity = activity;
		mRootView = initUI();
	}

	private View initUI() {
		View view = View.inflate(mActivity, R.layout.base_pager, null);
		tvTitle = (TextView) view.findViewById(R.id.tv_title);
		ibMenu = (ImageButton) view.findViewById(R.id.ib_menu);
		flContent = (FrameLayout) view.findViewById(R.id.fl_content);
		rlTitleBar = (RelativeLayout) view.findViewById(R.id.rl_page_title_bar);
		ibPicStyle = (ImageButton) view.findViewById(R.id.ib_pic_type);
		ibMenu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				toggle();
			}
		});
		return view;
	}

	protected void toggle() {
		MainActivity activity = (MainActivity) mActivity;
		SlidingMenu slidingMenu = activity.getSlidingMenu();
		slidingMenu.toggle(true);
	}

	public abstract void initData();
}
