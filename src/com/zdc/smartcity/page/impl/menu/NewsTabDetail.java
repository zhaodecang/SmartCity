package com.zdc.smartcity.page.impl.menu;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.viewpagerindicator.CirclePageIndicator;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.NewsDetailActivity;
import com.zdc.smartcity.domain.NewsMenuBean.NewsItemData;
import com.zdc.smartcity.domain.NewsTabBean;
import com.zdc.smartcity.domain.NewsTabBean.NewsData;
import com.zdc.smartcity.domain.NewsTabBean.TopNewsData;
import com.zdc.smartcity.global.ConstantValue;
import com.zdc.smartcity.global.GlobalData;
import com.zdc.smartcity.page.BaseMenuDetailPage;
import com.zdc.smartcity.utils.BitmapHelp;
import com.zdc.smartcity.utils.CacheUtil;
import com.zdc.smartcity.utils.LogUtil;
import com.zdc.smartcity.utils.SpUtil;
import com.zdc.smartcity.utils.ToastUtil;
import com.zdc.smartcity.view.PullToRefreshListView;
import com.zdc.smartcity.view.PullToRefreshListView.OnRefreshingListener;
import com.zdc.smartcity.view.TopNewsViewPager;

public class NewsTabDetail extends BaseMenuDetailPage {

	protected static final String tag = "TabDetailPager";
	/** 改变ViewPager的position **/
	protected static final int CHANGE_VIEWPAGE_POSITION = 0;
	@ViewInject(R.id.vp_top_news)
	private TopNewsViewPager mViewPager;
	@ViewInject(R.id.tv_desc)
	private TextView tvTitle;
	@ViewInject(R.id.indicator)
	private CirclePageIndicator mIndicator;
	@ViewInject(R.id.lv_news_list)
	private PullToRefreshListView lvNews;
	private NewsItemData mTabData;
	/** 请求新闻数据的链接地址 **/
	private String mUrl;
	/** 顶部轮播图数据集合 **/
	private ArrayList<TopNewsData> mTopNews;
	/** 新闻列表数据集合 **/
	private ArrayList<NewsData> mNewsList;
	/** 默认的正在加载中图片 **/
	private int defaultLoadingImage = R.drawable.topnews_item_default;
	/** 加载更多时获取数据的链接地址 **/
	private String mMoreUrl;
	/** 顶部轮播图的数据填充器 **/
	private TopNewsAdapter topNewsAdapter;
	/** 新闻列表的数据填充器 **/
	private NewsListAdapter newsAdapter;

	private Handler mHandler = null;

	/** 开始自动轮播 **/
	private void startAutoScroll() {
		if (mHandler == null) {
			mHandler = new Handler() {
				public void handleMessage(android.os.Message msg) {
					if (msg.what == CHANGE_VIEWPAGE_POSITION) {
						setVpToNext();
					}
				}
			};
		}
		sendHandlerMessage();
	}

	/** 移动顶部轮播图到下一个页面 需要做容错处理 **/
	private void setVpToNext() {
		int currentItem = mViewPager.getCurrentItem();
		if (currentItem == mViewPager.getAdapter().getCount() - 1) {
			currentItem = -1;
		}
		mViewPager.setCurrentItem(currentItem + 1);
		// sendHandlerMessage();//由于在ViewPager的滑动监听事件中已经发送了一条消息,所以此处不用再次发送消息
	}

	/** 给handler发送一个延时消息 开始下一次自动轮播 **/
	private void sendHandlerMessage() {
		LogUtil.i(tag, mTabData.title + "发送一个自动移动的消息");
		mHandler.sendEmptyMessageDelayed(CHANGE_VIEWPAGE_POSITION, 3000);
	}

	/** 移除handler消息队列中的消息 **/
	public void removeHandlerMessage() {
		LogUtil.i(tag, mTabData.title + "移除消息队列的消息");
		if (mHandler != null) {
			mHandler.removeCallbacksAndMessages(null);
		}
	}

	public NewsTabDetail(Activity activity, NewsItemData newsItemData) {
		super(activity);
		mTabData = newsItemData;
		mUrl = GlobalData.SERVER_URL + mTabData.url;
	};

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_news_tab_detail, null);
		ViewUtils.inject(this, view);

		View header = View.inflate(mActivity, R.layout.lv_news_detail_header, null);
		ViewUtils.inject(this, header);
		// 此处使用给listview额外添加一个头的方式展示轮播图效果的ViewPager
		lvNews.addHeaderView(header);
		// 还可以使用给第一个头布局附加一个子view的方式展示
		// lvNews.addCustomHeaderView(header);
		// 设置listview刷新时的监听
		setListViewOnRefreshingListener();
		// 设置listview的条目点击事件
		setListViewItemClick();
		return view;
	}

	/** 设置listview刷新时的监听 **/
	private void setListViewOnRefreshingListener() {
		OnRefreshingListener refreshingListener = new OnRefreshingListener() {
			@Override
			public void onRefreshing() {
				getDataFromServer();
				LogUtil.i(tag, "下拉刷新,去服务器加载数据");
			}

			@Override
			public void onLoadingMore() {
				getMoreDataFromServer();
				LogUtil.i(tag, "去服务器加载更多数据");
			}
		};
		lvNews.setOnRefreshingListener(refreshingListener);
	}

	/** 设置listview的条目点击事件 **/
	private void setListViewItemClick() {
		// 当点击某一条具体新闻条目时打开一个新的界面展示该条新闻对应的具体新闻内容
		final Intent intent = new Intent(mActivity, NewsDetailActivity.class);
		OnItemClickListener itemClickListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View v, int pos, long id) {
				// 获取当前listview的头布局数量
				int headerCount = lvNews.getHeaderViewsCount();
				if (pos <= headerCount - 1) {
					// 当点击的是头布局的条目时不做任何处理
					return;
				}
				// 如果正在加载更多 并且 点击的是条目随后一条(脚布局)就不做任何处理
				if (lvNews.getIsLoading() && pos == parent.getCount() - 1) {
					return;
				}
				// 打开对应新闻内容 点击的条目位置要减去header的数量
				pos = pos - headerCount;
				NewsData newsData = mNewsList.get(pos);
				// 标记为已读
				String newsId = SpUtil.getString(mActivity, ConstantValue.HAVE_READED, "");
				if (!newsId.contains(newsData.id + "")) {
					newsId += newsData.id + ",";
					SpUtil.putString(mActivity, ConstantValue.HAVE_READED, newsId);
				}
				// 改变被点击条目对应文本的字体颜色 正常为黑色 已读为灰色
				TextView tv = (TextView) v.findViewById(R.id.tv_news_content);
				tv.setTextColor(Color.GRAY);
				// 打开一个新的activity 附带新闻详情页url地址
				String url = newsData.url;
				intent.putExtra("url", url);
				mActivity.startActivity(intent);
			}
		};
		lvNews.setOnItemClickListener(itemClickListener);
	}

	@Override
	public void initData() {
		String cache = CacheUtil.getCache(mActivity, mUrl);
		if (TextUtils.isEmpty(cache)) {
			// 如果缓存数据为空就去获取服务器数据
			getDataFromServer();
		} else {
			LogUtil.i(tag, "获取到新闻菜单缓存数据");
			processData(cache, false);
		}
		// 此处不让软件每次打开都去刷新一下数据,只有用户手动下拉刷新才去获取新数据
		// getDataFromServer();
	}

	/** 从服务器获取更多数据 **/
	protected void getMoreDataFromServer() {
		if (TextUtils.isEmpty(mMoreUrl)) {
			// 如果没有加载更多的数据连接地址 则不去加载更多数据
			lvNews.setonLoadMoreCompleted(false);
			ToastUtil.show(mActivity, "没有更多数据了");
			return;
		}
		// 发送请求获取更多数据
		HttpUtils utils = new HttpUtils();
		RequestCallBack<String> callBack = new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				lvNews.setonLoadMoreCompleted(true);
				// 进一步处理服务器数据
				processData(result, true);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				lvNews.setonLoadMoreCompleted(false);
				ToastUtil.show(mActivity, "加载更多数据失败");
			}
		};
		// 发送数据请求
		utils.send(HttpMethod.GET, mMoreUrl, callBack);
	}

	/**
	 * 刷新或者没有缓存数据时获取服务器数据资源
	 */
	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		RequestCallBack<String> callBack = new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				// 收起顶部控件
				lvNews.setOnRefreshCompleted(true);
				// 进一步处理服务器数据
				processData(result, false);
				// 保存缓存数据
				CacheUtil.saveCache(mActivity, mUrl, result);
				ToastUtil.show(mActivity, "已帮你加载最新新闻数据");
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				// 收起顶部控件
				lvNews.setOnRefreshCompleted(false);
				ToastUtil.show(mActivity, "网络异常,更新新闻数据失败");
			}
		};
		// 发送数据请求
		utils.send(HttpMethod.GET, mUrl, callBack);
	}

	/**
	 * 处理获取到的数据
	 * 
	 * @param data 要解析的服务器数据
	 * @param isLoadingMore 是否是通过加载更多获取到的数据
	 */
	private void processData(String data, boolean isLoadingMore) {
		Gson gson = new Gson();
		NewsTabBean newsTabBean = gson.fromJson(data, NewsTabBean.class);
		String moreUrl = newsTabBean.data.more;// 加载更多时获取数据的相对链接地址
		if (TextUtils.isEmpty(moreUrl)) {
			mMoreUrl = null;
		} else {
			mMoreUrl = GlobalData.SERVER_URL + moreUrl;
		}
		if (!isLoadingMore) {
			processDataFromServer(data, newsTabBean);
		} else {
			processMoreDataFromServer(newsTabBean);
		}
	}

	/** 处理加载更多获取到的数据 **/
	private void processMoreDataFromServer(NewsTabBean newsTabBean) {
		mNewsList.addAll(0, newsTabBean.data.news);
		newsAdapter.notifyDataSetChanged();
		// 判断在获取更多数据的时候有没有获取到顶部轮播图的数据
		if (newsTabBean.data.topnews != null) {
			mTopNews.addAll(0, newsTabBean.data.topnews);
			topNewsAdapter.notifyDataSetChanged();
		}
	}

	/** 处理从服务器获取到的普通数据 **/
	private void processDataFromServer(String data, NewsTabBean newsTabBean) {
		mTopNews = newsTabBean.data.topnews;
		if (mTopNews != null) {
			if (topNewsAdapter == null) {
				topNewsAdapter = new TopNewsAdapter();
			}
			LogUtil.i(tag, "填充轮播图数据");
			// 填充轮播图数据
			mViewPager.setAdapter(topNewsAdapter);
			mIndicator.setViewPager(mViewPager);
			// 设置指示器以跳变的形式展示
			mIndicator.setSnap(true);
			mIndicator.setOnPageChangeListener(pageChangeListener);
			// 设置默认第一条被选中
			tvTitle.setText(mTopNews.get(0).title);
			// 每次重新初始化数据将指示器复位
			mIndicator.onPageSelected(0);
			// 开始自动轮播
			startAutoScroll();
			// 可以通过设置手指触摸事件的方式避免轮播图不响应手指滑动的问题 不过这样还是会导致其他的问题 所以不使用
			// setViewPagerOnTouchListener();
		}
		// 填充新闻列表条目数据
		mNewsList = newsTabBean.data.news;
		if (mNewsList != null) {
			if (newsAdapter == null) {
				newsAdapter = new NewsListAdapter();
			}
			LogUtil.i(data, "填充新闻列表数据");
			lvNews.setAdapter(newsAdapter);
		}
	}

	/** 给indicator设置监听 **/
	OnPageChangeListener pageChangeListener = new OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			// 更新头条新闻标题
			tvTitle.setText(mTopNews.get(position).title);
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// SCROLL_STATE_IDLE = 0
			// SCROLL_STATE_DRAGGING = 1
			// SCROLL_STATE_SETTLING = 2
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				// 当手指的滑动结束(处于空闲状态)的时候发送一个消息改变指示器的位置
				sendHandlerMessage();
			} else {
				removeHandlerMessage();
			}
		}

		@Override
		public void onPageScrolled(int position, float pos, int pop) {

		}
	};

	/** 顶部ViewPager的适配器 **/
	private class TopNewsAdapter extends PagerAdapter {
		private BitmapUtils mBitmapUtils;

		/** 顶部ViewPager的适配器 **/
		public TopNewsAdapter() {
			mBitmapUtils = BitmapHelp.getBitmapUtils(mActivity);
			mBitmapUtils.configDefaultLoadingImage(defaultLoadingImage);
		}

		@Override
		public int getCount() {
			return mTopNews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = new ImageView(mActivity);
			view.setScaleType(ScaleType.FIT_XY);
			TopNewsData topNewsData = mTopNews.get(position);
			try {
				mBitmapUtils.display(view, topNewsData.topimage);
			} catch (Exception e) {
				LogUtil.e(tag, "bitmaputils加载图片异常");
			}
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	/** 新闻列表的适配器 **/
	private class NewsListAdapter extends BaseAdapter {

		private BitmapUtils utils;
		private String newsId;

		/** 新闻列表的适配器 **/
		public NewsListAdapter() {
			utils = BitmapHelp.getBitmapUtils(mActivity);
			utils.configDefaultLoadingImage(defaultLoadingImage);
			// 回显已读新闻状态
			newsId = SpUtil.getString(mActivity, ConstantValue.HAVE_READED, "");
		}

		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public NewsData getItem(int position) {
			return mNewsList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = View.inflate(mActivity, R.layout.lv_news_item, null);
				holder.ivNewsIcon = (ImageView) convertView
						.findViewById(R.id.iv_news_icon);
				holder.tvNewsContent = (TextView) convertView
						.findViewById(R.id.tv_news_content);
				holder.tvNewsDate = (TextView) convertView.findViewById(R.id.tv_news_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			NewsData itemData = getItem(position);
			// 设置一条新闻数据的内容概要
			holder.tvNewsContent.setText(itemData.title);
			// 设置一条新闻数据的日期
			holder.tvNewsDate.setText(itemData.pubdate);
			// 设置一条新闻数据的缩略图
			try {
				utils.display(holder.ivNewsIcon, itemData.listimage);
			} catch (Exception e) {
				LogUtil.e(tag, "bitmaputils加载图片异常");
			}
			// 回显已读新闻状态
			if (newsId.contains(itemData.id + "")) {
				holder.tvNewsContent.setTextColor(Color.GRAY);
			}
			return convertView;
		}

		private class ViewHolder {
			public ImageView ivNewsIcon;
			public TextView tvNewsContent, tvNewsDate;
		}
	}
}
