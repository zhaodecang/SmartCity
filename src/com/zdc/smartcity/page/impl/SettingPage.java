package com.zdc.smartcity.page.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.activity.SettingTestActivity;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.utils.ToastUtil;
import com.zdc.smartcity.view.SettingItemView;

public class SettingPage extends BasePage implements OnClickListener {

	private static final int THAK_PHOTO = 0;
	private static final int CROP_PHOTO = 1;
	@ViewInject(R.id.iv_setting_qq_login)
	private ImageView ivQQLogin;
	@ViewInject(R.id.iv_setting_sina_login)
	private ImageView ivSinaLogin;
	@ViewInject(R.id.iv_user_header)
	private ImageView ivUserHeader;
	@ViewInject(R.id.siv_feedback)
	private SettingItemView sivFeedback;

	private Uri imageUri;

	public SettingPage(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 隐藏标题栏
		rlTitleBar.setVisibility(View.GONE);
		// 填充内容
		View view = View.inflate(mActivity, R.layout.pager_setting, null);
		ViewUtils.inject(this, view);
		flContent.addView(view);
		ivQQLogin.setOnClickListener(this);
		ivSinaLogin.setOnClickListener(this);
		sivFeedback.setOnClickListener(this);
		ivUserHeader.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_setting_qq_login:
			Intent intent = new Intent(mActivity, SettingTestActivity.class);
			mActivity.startActivity(intent);
			// ToastUtil.show(mActivity, "使用QQ登陆");
			break;
		case R.id.iv_setting_sina_login:
			ToastUtil.show(mActivity, "使用Sina登陆");
			break;
		case R.id.siv_feedback:
			break;
		case R.id.iv_user_header:
			File file = new File(Environment.getExternalStorageDirectory(),
					System.currentTimeMillis() + ".jpg");
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			imageUri = Uri.fromFile(file);
			Intent intent2 = new Intent("android.media.action.IMAGE_CAPTURE");
			intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			mActivity.startActivityForResult(intent2, THAK_PHOTO);
			break;
		}
	}

	/** 当activity返回并传回数据时调用该方法 **/
	public void OnActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case THAK_PHOTO:
			if (resultCode == Activity.RESULT_OK) {
				Intent intent = new Intent("com.android.camera.action.CROP");
				intent.setDataAndType(imageUri, "image/*");
				intent.putExtra("scale", true);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				mActivity.startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
			}
			break;
		case CROP_PHOTO:
			if (resultCode == Activity.RESULT_OK) {
				try {
					ContentResolver observer = mActivity.getContentResolver();
					InputStream is = observer.openInputStream(imageUri);
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					ivUserHeader.setImageBitmap(bitmap); // 将裁剪后的照片显示出来
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}
}
