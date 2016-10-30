package com.zdc.smartcity.utils;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;

public class BitmapHelp {
	private static BitmapUtils bitmapUtils;

	private BitmapHelp() {

	}

	/**
	 * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
	 * 
	 * @param appContext application context
	 * @return
	 */
	public static BitmapUtils getBitmapUtils(Context appContext) {
		if (bitmapUtils == null) {
			bitmapUtils = new BitmapUtils(appContext);
		}
		return bitmapUtils;
	}
}
