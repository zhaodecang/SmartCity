package com.zdc.smartcity.utils;

import android.content.Context;

public class CacheUtil {

	/**
	 * 保存数据缓存
	 * 
	 * @param context 上下文
	 * @param key 服务器地址
	 * @param value 数据
	 */
	public static void saveCache(Context context, String key, String value) {
		SpUtil.putString(context, key, value);
		LogUtil.i("CacheUtil", "缓存数据已保存");
	}

	/**
	 * 获取已经保存的换粗数据
	 * 
	 * @param context 上下文
	 * @param key 服务器地址
	 * @return 返回已经保存的缓存数据
	 */
	public static String getCache(Context context, String key) {
		LogUtil.i("CacheUtil", "获取到缓存数据");
		return SpUtil.getString(context, key, null);
	}
}
