package com.zdc.smartcity.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zdc.smartcity.R;

/**
 * description:设置界面条目自定义组合控件
 * 
 * @author zhaodecang
 * @date 2016-9-26下午9:22:45
 */
public class SettingItemView extends RelativeLayout {
	private String mTitle;
	private int mRootBg;

	public SettingItemView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// 加载自定义布局
		View.inflate(context, R.layout.view_setting_item, this);
		// 获取属性值并给给子view赋值
		TextView tv_setting_title = (TextView) findViewById(R.id.tv_setting_title);
		// 获取属性值
		initAttrs(context, attrs);
		// 赋值
		tv_setting_title.setText(mTitle);
		switch (mRootBg) {
		case 0:
			setBackgroundResource(R.drawable.setting_item_first_bg);
			break;
		case 1:
			setBackgroundResource(R.drawable.setting_item_middle_bg);
			break;
		case 2:
			setBackgroundResource(R.drawable.setting_item_last_bg);
			break;
		}
	}

	/**
	 * 初始化属性 获取属性值
	 * 
	 * @param attrs 属性集合
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.SettingItemView);
		mTitle = typedArray.getString(R.styleable.SettingItemView_title);
		mRootBg = typedArray.getInt(R.styleable.SettingItemView_root_bg_style, -1);
	}

	public SettingItemView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SettingItemView(Context context) {
		this(context, null);
	}
}
