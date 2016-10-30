package com.zdc.smartcity.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.utils.LogUtil;

public class PullToRefreshListView extends ListView implements OnScrollListener {

	private static final String tag = "PullToRefreshListView";
	private View headerView, footerView;
	private int mHeaderHeight, mFooterHeight;
	private int startY = -1;
	@ViewInject(R.id.iv_arrow)
	private ImageView ivArrow;
	@ViewInject(R.id.pb_refreshing)
	private ProgressBar pbRefreshing;
	@ViewInject(R.id.pb_loading_more)
	private ProgressBar pbLoadMoreBar;
	@ViewInject(R.id.tv_state)
	private TextView tvState;
	@ViewInject(R.id.tv_time)
	private TextView tvTime;
	@ViewInject(R.id.tv_footer_time)
	private TextView tvFooterTime;
	@ViewInject(R.id.ll_refresh_header_root)
	private LinearLayout llHeaderRoot;
	/** 下拉刷新状态 **/
	private final int STATE_PULL_TO_REFRESH = 0;
	/** 释放刷新状态 **/
	private final int STATE_RELEASE_REFRESH = 1;
	/** 刷新中状态 **/
	private final int STATE_REFRESHING = 2;
	/** 当前状态 **/
	private int currentState = STATE_PULL_TO_REFRESH;
	private SimpleDateFormat mSdf;
	/** 回调接口 **/
	private OnRefreshingListener listener;
	/** 是否正在加载更多数据 **/
	private boolean isOnLoading;

	public PullToRefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		mSdf = new SimpleDateFormat("a HH:mm:ss");
	}

	/** 初始化头布局和脚布局 **/
	private void initView() {
		initHeaderView();
		initFooterView();
	}

	/** 给listview添加一个头布局 **/
	private void initHeaderView() {
		headerView = View.inflate(getContext(), R.layout.lv_refreshing_header, null);
		ViewUtils.inject(this, headerView);
		headerView.measure(0, 0);
		addHeaderView(headerView);
		mHeaderHeight = headerView.getMeasuredHeight();
		hideHeaderView();
	}

	/** 给listview添加一个脚布局 **/
	private void initFooterView() {
		footerView = View.inflate(getContext(), R.layout.lv_refreshing_footer, null);
		ViewUtils.inject(this, footerView);
		footerView.measure(0, 0);
		addFooterView(footerView);
		mFooterHeight = footerView.getMeasuredHeight();
		hideFooterView();// 刚开始完全隐藏
		/** 给listview添加滑动监听 **/
		setOnScrollListener(this);
	}

	/** 给listview的头布局附加一个布局(用于添加轮播图布局 方便统一管理) **/
	public void addCustomHeaderView(View view) {
		llHeaderRoot.addView(view);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && !isOnLoading) {
			// 如果此时显示的是listview条目的最后一条
			if (getLastVisiblePosition() >= getCount() - 1) {
				isOnLoading = true;
				// 显示脚布局 加载更多数据
				showFooterView();
				if (listener != null) {
					listener.onLoadingMore();
				} else {
					setonLoadMoreCompleted(false);
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			startY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (startY == -1) {// 如果点击的时是顶部ViewPager,action_down事件会被ViewPager消费掉,导致startY=-1
				// 此时需要重新计算一下手指按下时的值
				startY = (int) ev.getY();
			}
			if (currentState == STATE_REFRESHING) {
				// 如果是正在刷新,隐藏头布局并 跳出循环
				reSetHeader();
				break;
			}
			int distanceY = (int) (ev.getY() - startY);
			if (getFirstVisiblePosition() <= 0 && distanceY > 0) {
				int paddingTop = -mHeaderHeight + distanceY;
				showHeaderView(paddingTop);
				if (paddingTop < 0 && currentState != STATE_PULL_TO_REFRESH) {
					currentState = STATE_PULL_TO_REFRESH;
					changeStateAndData();
				} else if (paddingTop >= 0 && currentState != STATE_RELEASE_REFRESH) {
					currentState = STATE_RELEASE_REFRESH;
					changeStateAndData();
				}
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			if (currentState == STATE_PULL_TO_REFRESH) {
				ivArrow.clearAnimation();
				hideHeaderView();
			} else if (currentState == STATE_RELEASE_REFRESH) {
				currentState = STATE_REFRESHING;
				changeStateAndData();
				if (listener != null) {
					listener.onRefreshing();
				} else {
					setOnRefreshCompleted(false);
				}
			}
			break;
		}
		return super.onTouchEvent(ev);
	}

	/** 修改头布局的当前状态及要显示的数据 **/
	private void changeStateAndData() {
		switch (currentState) {
		case STATE_RELEASE_REFRESH:
			tvState.setText("释放刷新");
			startUpArrowAnim();
			break;
		case STATE_PULL_TO_REFRESH:
			tvState.setText("下拉刷新");
			startDownArrowAnim();
			break;
		case STATE_REFRESHING:
			showHeaderView();
			tvState.setText("正在刷新");
			tvTime.setText("现在时间:" + getNowDate());
			ivArrow.clearAnimation();// 清除箭头动画
			ivArrow.setVisibility(View.INVISIBLE);// 隐藏箭头
			pbRefreshing.setVisibility(View.VISIBLE);// 显示进度条
			break;
		}
	}

	private void startUpArrowAnim() {
		RotateAnimation upAnimation = initAnimation(0, 180);
		ivArrow.startAnimation(upAnimation);
	}

	private void startDownArrowAnim() {
		RotateAnimation downAnimation = initAnimation(180, 360);
		ivArrow.startAnimation(downAnimation);
	}

	private RotateAnimation initAnimation(float fromDegree, float toDegree) {
		RotateAnimation animation = new RotateAnimation(fromDegree, toDegree,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setFillAfter(true);
		animation.setDuration(600);
		return animation;
	}

	/** 刷新数据完成 **/
	public void setOnRefreshCompleted(boolean isSuccess) {
		reSetHeader();// 重置头布局
		if (isSuccess) {// 如果刷新数据成功 就更新时间
			tvTime.setText("上次刷新时间:" + getNowDate());
			LogUtil.i(tag, "刷新成功");
		} else {
			LogUtil.i(tag, "刷新失败");
		}
	}

	/** 加载新数据完成 **/
	public void setonLoadMoreCompleted(boolean isSuccess) {
		isOnLoading = false;
		hideFooterView();// 隐藏脚布局
		if (isSuccess) {
			// 如果加载数据成功 就更新时间
			tvFooterTime.setText("上次加载时间:" + getNowDate());
			LogUtil.i(tag, "加载成功");
		} else {
			LogUtil.i(tag, "加载失败");
		}
	}

	/** 获取已经格式化的当前时间 **/
	private String getNowDate() {
		return mSdf.format(new Date());
	}

	/** 获取是否正在加载更多数据 **/
	public boolean getIsLoading() {
		return isOnLoading;
	}

	/** 重置头布局 **/
	private void reSetHeader() {
		// 重置手指按下时的Y轴坐标
		startY = -1;
		// 隐藏头布局
		hideHeaderView();
		// 状态归位
		currentState = STATE_PULL_TO_REFRESH;
		// 箭头显示
		ivArrow.setVisibility(View.VISIBLE);
		// 进度条隐藏
		pbRefreshing.setVisibility(View.INVISIBLE);
	}

	/** 完全隐藏脚布局 **/
	private void hideFooterView() {
		showFooterView(-mFooterHeight);
	}

	/** 完全显示脚布局 **/
	private void showFooterView() {
		showFooterView(0);
		setSelection(getCount() - 1);
		tvFooterTime.setText("当前时间:" + getNowDate());
	}

	/** 根据指定顶部内边距展示脚布局 **/
	private void showFooterView(int paddingTop) {
		footerView.setPadding(0, 0, 0, paddingTop);
	}

	/** 完全隐藏头布局 **/
	private void hideHeaderView() {
		showHeaderView(-mHeaderHeight);
	}

	/** 完全显示头布局 **/
	private void showHeaderView() {
		showHeaderView(0);
	}

	/** 根据指定顶部内边距展示头布局 **/
	private void showHeaderView(int paddingTop) {
		headerView.setPadding(0, paddingTop, 0, 0);
	}

	public void setOnRefreshingListener(OnRefreshingListener listener) {
		this.listener = listener;
	}

	/** listview的刷新监听回调接口 **/
	public interface OnRefreshingListener {
		public void onRefreshing();

		public void onLoadingMore();
	}

	@Override
	public void onScroll(AbsListView view, int fvi, int vic, int tic) {

	}
}
