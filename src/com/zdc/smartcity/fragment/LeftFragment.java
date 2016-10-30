package com.zdc.smartcity.fragment;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.MainActivity;
import com.zdc.smartcity.page.impl.NewsCenterPage;

public class LeftFragment extends BaseFragment {

	private ListView lvMenuList;
	private List<String> mTitleData;
	private int mCurrentPos;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.left_fragment_lv, null);
		lvMenuList = (ListView) view.findViewById(R.id.lv_menu_list);
		return view;
	}

	@Override
	public void initData() {

	}

	public void setData(ArrayList<String> titles) {
		mCurrentPos = 0;
		mTitleData = titles;
		final MenuAdapter adapter = new MenuAdapter();
		lvMenuList.setAdapter(adapter);
		// 设置侧边栏条目的点击事件
		lvMenuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> p, View v, int position, long id) {
				mCurrentPos = position;
				adapter.notifyDataSetChanged();
				slidingMenuToggle();
				setCurrentDetailPager(position);
			}
		});
	}

	/**
	 * 设置详情页内容
	 * 
	 * @param position
	 */
	protected void setCurrentDetailPager(int position) {
		MainActivity mainActivity = (MainActivity) mActivity;
		// 获取新闻中心页
		ContentFragment contentFragment = mainActivity.getContentFragment();
		NewsCenterPage newsPage = contentFragment.getNewsCenterPager();
		// 设置新闻中心页的内容
		newsPage.setCurrentDetailPager(position);
	}

	/**
	 * 切换侧边栏的开关
	 */
	protected void slidingMenuToggle() {
		MainActivity mainActivity = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainActivity.getSlidingMenu();
		slidingMenu.toggle(true);
	}

	private class MenuAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return mTitleData.size();
		}

		@Override
		public String getItem(int position) {
			return mTitleData.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.lv_left_menu_item,
						null);
			}
			TextView tvMenu = (TextView) convertView.findViewById(R.id.tv_menu);
			tvMenu.setText(getItem(position));
			if (mCurrentPos == position) {
				tvMenu.setEnabled(true);
			} else {
				tvMenu.setEnabled(false);
			}
			return convertView;
		}
	}
}
