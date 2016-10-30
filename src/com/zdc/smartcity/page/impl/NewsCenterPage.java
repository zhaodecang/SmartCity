package com.zdc.smartcity.page.impl;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.zdc.smartcity.activity.MainActivity;
import com.zdc.smartcity.domain.NewsMenuBean;
import com.zdc.smartcity.fragment.LeftFragment;
import com.zdc.smartcity.global.GlobalData;
import com.zdc.smartcity.page.BaseMenuDetailPage;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.page.impl.menu.InteractMenuDetail;
import com.zdc.smartcity.page.impl.menu.NewsMenuDetail;
import com.zdc.smartcity.page.impl.menu.PhotosMenuDetail;
import com.zdc.smartcity.page.impl.menu.TopicMenuDetail;
import com.zdc.smartcity.utils.CacheUtil;
import com.zdc.smartcity.utils.LogUtil;
import com.zdc.smartcity.utils.ToastUtil;

public class NewsCenterPage extends BasePage {

	public static final String tag = "NewsCenterPage";
	private ArrayList<BaseMenuDetailPage> mMenuDetailPagers;
	private NewsMenuBean mNewsData;
	/** 用于保存左侧菜单栏标题的集合 **/
	private ArrayList<String> titles;

	public NewsCenterPage(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 首先获取缓存数据
		String cacheData = CacheUtil.getCache(mActivity, GlobalData.CATEGORY_URL);
		if (!TextUtils.isEmpty(cacheData)) {
			// 使用缓存数据
			LogUtil.i(tag, "获取到新闻中心分类信息缓存数据");
			processData(cacheData);
		}
		// 如果缓存数据为空 就去获取网络数据
		// 请求服务器数据
		getDataFromServer();
	}

	/**
	 * 请求服务器数据
	 */
	private void getDataFromServer() {
		HttpUtils httpUtils = new HttpUtils();
		// httpUtils.sendSync(method, url, params);
		httpUtils.send(HttpMethod.GET, GlobalData.CATEGORY_URL, callBack);
	}

	RequestCallBack<String> callBack = new RequestCallBack<String>() {
		@Override
		public void onFailure(HttpException error, String msg) {
			LogUtil.i(tag, "新闻中心网络数据获取失败");
			ToastUtil.show(mActivity, "网络异常,获取数据失败");
		}

		@Override
		public void onSuccess(ResponseInfo<String> responseInfo) {
			LogUtil.i(tag, "新闻中心网络数据获取成功");
			String result = responseInfo.result;
			processData(result);
			// 保存缓存数据
			CacheUtil.saveCache(mActivity, GlobalData.CATEGORY_URL, result);
		}
	};

	/**
	 * 处理从服务器获取到的数据
	 * 
	 * @param result 服务器返回的数据
	 */
	public void processData(String result) {
		// 解析json数据
		Gson gson = new Gson();
		mNewsData = gson.fromJson(result, NewsMenuBean.class);
		MainActivity mainActivity = (MainActivity) mActivity;
		// 给侧边栏设置初始化数据
		LeftFragment leftFragment = mainActivity.getLeftFragment();
		titles = new ArrayList<String>();
		for (int i = 0; i < mNewsData.data.size(); i++) {
			titles.add(mNewsData.data.get(i).title);
		}
		leftFragment.setData(titles);
		// 初始化4个菜单详情页
		initMenuItem();
		// 将 新闻 菜单详情页设置为默认页面
		setCurrentDetailPager(0);
	}

	/**
	 * 设置新闻中心的详情页内容
	 * 
	 * @param position 侧边栏的索引
	 */
	public void setCurrentDetailPager(int position) {
		// 设置详情页全局内容
		BaseMenuDetailPage pager = mMenuDetailPagers.get(position);
		View view = pager.menuDetailPagerView;
		flContent.removeAllViews();
		flContent.addView(view);
		pager.initData();
		// 设置详情页标题栏
		tvTitle.setText(titles.get(position));
		// 在组图详情页显示图片样式切换按钮
		if (pager instanceof PhotosMenuDetail) {
			ibPicStyle.setVisibility(View.VISIBLE);
		} else {
			ibPicStyle.setVisibility(View.GONE);
		}
	}

	/** 初始化4个菜单详情页 **/
	private void initMenuItem() {
		mMenuDetailPagers = new ArrayList<BaseMenuDetailPage>();
		mMenuDetailPagers.clear();
		mMenuDetailPagers
				.add(new NewsMenuDetail(mActivity, mNewsData.data.get(0).children));
		mMenuDetailPagers.add(new TopicMenuDetail(mActivity));
		mMenuDetailPagers.add(new PhotosMenuDetail(mActivity, ibPicStyle));
		mMenuDetailPagers.add(new InteractMenuDetail(mActivity));
	}
}
