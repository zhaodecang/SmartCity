package com.zdc.smartcity.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.jpush.android.api.JPushInterface;

import com.zdc.smartcity.R;
import com.zdc.smartcity.global.ConstantValue;
import com.zdc.smartcity.utils.LogUtil;
import com.zdc.smartcity.utils.SpUtil;

public class SplashActivity extends Activity {
	protected static final String tag = "SplashActivity";
	private Context mContext;
	private ImageView ivHouse;
	private RelativeLayout rlRoot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		rlRoot = (RelativeLayout) findViewById(R.id.rl_root);
		mContext = this;
		initUI();
		initAnim();
	}

	private void initAnim() {
		// 旋转动画
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		// 缩放动画
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		// 渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		// 设置动画的执行时长和最终状态
		rotateAnimation.setDuration(2000);
		rotateAnimation.setFillAfter(true);
		scaleAnimation.setDuration(2000);
		scaleAnimation.setFillAfter(true);
		alphaAnimation.setDuration(2000);
		alphaAnimation.setFillAfter(true);
		// 用一个动画集合来一起执行
		AnimationSet set = new AnimationSet(true);
		set.addAnimation(rotateAnimation);
		set.addAnimation(scaleAnimation);
		set.addAnimation(alphaAnimation);
		// 设置动画监听
		set.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				boolean isFirst = SpUtil.getBoolean(mContext,
						ConstantValue.IS_FIRST_ENTERED, true);
				if (isFirst) {
					LogUtil.i(tag, "进入导航界面");
					startActivity(new Intent(mContext, GuideActivity.class));
				} else {
					LogUtil.i(tag, "进入主页面");
					startActivity(new Intent(mContext, MainActivity.class));
				}
				finish();
			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});
		ivHouse.startAnimation(set);
		rlRoot.startAnimation(alphaAnimation);
	}

	private void initUI() {
		ivHouse = (ImageView) findViewById(R.id.iv_house);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
	}
}
