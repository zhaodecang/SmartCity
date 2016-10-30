package com.zdc.smartcity.page.impl;

import android.app.Activity;
import android.view.View;

import com.zdc.smartcity.R;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.utils.ToastUtil;
import com.zdc.smartcity.view.CircleMenuLayout;
import com.zdc.smartcity.view.CircleMenuLayout.OnMenuItemClickListener;

public class HomePage extends BasePage implements OnMenuItemClickListener {

	private CircleMenuLayout cmlAllService;

	private String[] mItemTexts = { "出行预定", "翻译查询", "交通路线", "求职招聘", "天气查询" };
	private int[] mItemImgs = { R.drawable.index_chuxing, R.drawable.index_fanyi,
			R.drawable.index_itlx, R.drawable.jiaoyu, R.drawable.shenghuo };

	public HomePage(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 初始化标题栏
		tvTitle.setText("智慧城市");
		// 隐藏菜单按钮
		ibMenu.setVisibility(View.INVISIBLE);
		// 填充内容
		View view = View.inflate(mActivity, R.layout.pager_home_layout, null);
		cmlAllService = (CircleMenuLayout) view.findViewById(R.id.cml_all_service);
		cmlAllService.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
		cmlAllService.setOnMenuItemClickListener(this);
		flContent.addView(view);
	}

	public void resetLayout() {
		if (cmlAllService != null) {
			cmlAllService.removeAllViews();
		}
	}

	@Override
	public void itemClick(View view, int pos) {
		ToastUtil.show(mActivity, mItemTexts[pos]);
	}

	@Override
	public void itemCenterClick(View view) {
		ToastUtil.show(mActivity, "智慧城市服务列表");
	}
}
