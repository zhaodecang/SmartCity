package com.zdc.smartcity.page.impl.menu;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.domain.PhotosTabBean;
import com.zdc.smartcity.domain.PhotosTabBean.PhotosData.PhotosNews;
import com.zdc.smartcity.global.GlobalData;
import com.zdc.smartcity.page.BaseMenuDetailPage;
import com.zdc.smartcity.utils.CacheUtil;
import com.zdc.smartcity.utils.LogUtil;
import com.zdc.smartcity.utils.ToastUtil;
import com.zdc.smartcity.utils.bitmaputils.BitmapCacheUtil;
import com.zdc.smartcity.view.PullToRefreshListView;

public class PhotosMenuDetail extends BaseMenuDetailPage implements OnClickListener {

	private static final String tag = "PhotosMenuDetail";
	private ImageButton ibPicStyle;
	@ViewInject(R.id.lv_photos_list)
	private PullToRefreshListView lvPhotosList;
	@ViewInject(R.id.gv_photos_list)
	private GridView gvPhotosList;

	private boolean isLvShow = true;
	private List<PhotosNews> mNews;

	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case BitmapCacheUtil.SUCCESS:
				Bitmap bitmap = (Bitmap) msg.obj;
				if (bitmap != null) {
					ImageView ivPhoto = (ImageView) lvPhotosList.findViewWithTag(msg.arg1);
					ivPhoto.setImageBitmap(bitmap);
				}
				break;
			}
		};
	};

	public PhotosMenuDetail(Activity activity, ImageButton _ibPicStyle) {
		super(activity);
		this.ibPicStyle = _ibPicStyle;
		ibPicStyle.setOnClickListener(this);
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_photos_tab_detail, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		String photoCache = CacheUtil.getCache(mActivity, GlobalData.PHOTOS_URL);
		if (TextUtils.isEmpty(photoCache)) {
			getDataFromServer();
		} else {
			processData(photoCache);
			LogUtil.i(tag, "获取到组图详情页数据缓存");
		}
	}

	private void getDataFromServer() {
		HttpUtils utils = new HttpUtils();
		RequestCallBack<String> callBack = new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				String result = responseInfo.result;
				processData(result);
				ToastUtil.show(mActivity, "组图数据加载成功");
				CacheUtil.saveCache(mActivity, GlobalData.PHOTOS_URL, result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				ToastUtil.show(mActivity, "组图数据加载失败");
				LogUtil.e(tag, msg);
			}
		};
		utils.send(HttpMethod.GET, GlobalData.PHOTOS_URL, callBack);
	}

	private void processData(String photoCache) {
		Gson gson = new Gson();
		PhotosTabBean photosData = gson.fromJson(photoCache, PhotosTabBean.class);
		mNews = photosData.data.news;
		if (mNews != null) {
			ListAdapter adapter = new PhotosAdapter();
			lvPhotosList.setAdapter(adapter);
			gvPhotosList.setAdapter(adapter);
		}
	}

	private class PhotosAdapter extends BaseAdapter {
		// private BitmapUtils utils;
		private BitmapCacheUtil cacheUtils;

		public PhotosAdapter() {
			// utils = BitmapHelp.getBitmapUtils(mActivity);
			// utils.configDefaultLoadingImage(R.drawable.pic_item_list_default);
			// 创建一个通过URL获取图片资源的工具类
			cacheUtils = new BitmapCacheUtil(mHandler);
		}

		@Override
		public int getCount() {
			return mNews.size();
		}

		@Override
		public PhotosNews getItem(int position) {
			return mNews.get(position);
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
				convertView = View.inflate(mActivity, R.layout.photos_list_item, null);
				holder.ivPhotos = (ImageView) convertView.findViewById(R.id.iv_photos);
				holder.tvDesc = (TextView) convertView.findViewById(R.id.tv_photos_desc);
				holder.tvTime = (TextView) convertView.findViewById(R.id.tv_photos_time);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			PhotosNews news = getItem(position);
			holder.tvDesc.setText(news.title);
			holder.tvTime.setText(news.pubdate);
			// 设置默认图片
			holder.ivPhotos.setTag(position);
			holder.ivPhotos.setImageResource(R.drawable.pic_item_list_default);
			// 此处加载图片使用三级缓存机制
			// utils.display(holder.ivPhotos, news.listimage);
			Bitmap bitmap = cacheUtils.getBitmapByUrl(news.listimage, position);
			if (bitmap != null) {
				holder.ivPhotos.setImageBitmap(bitmap);
			}
			return convertView;
		}
	}

	static class ViewHolder {
		private ImageView ivPhotos;
		private TextView tvDesc, tvTime;
	}

	@Override
	public void onClick(View v) {
		if (isLvShow) {
			isLvShow = false;
			lvPhotosList.setVisibility(View.GONE);
			gvPhotosList.setVisibility(View.VISIBLE);
			ibPicStyle.setImageResource(R.drawable.icon_pic_list_type);
		} else {
			isLvShow = true;
			lvPhotosList.setVisibility(View.VISIBLE);
			gvPhotosList.setVisibility(View.GONE);
			ibPicStyle.setImageResource(R.drawable.icon_pic_grid_type);
		}
	}
}
