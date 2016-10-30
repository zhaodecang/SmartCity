package com.zdc.smartcity.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @description 用于保存配置信息的工具类
 * 
 * @author zhaodecang
 * @date 2016-9-18上午9:50:23
 */
public class SpUtil {
	private static SharedPreferences sp;

	/**
	 * @param context 上下文环境
	 * @param key 存储节点的名称
	 * @param defValue 存储节点的默认值
	 * @return 返回已存储 String 类型的值
	 */
	public static String getString(Context context, String key, String defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getString(key, defValue);
	}

	/**
	 * 把String类型的密码存储起来
	 * 
	 * @param context 上下文环境
	 * @param key 存储节点的名称
	 * @param value 存储节点的值
	 */
	public static void putString(Context context, String key, String value) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}

	/**
	 * 把boolean类型的开关状标记存入sp
	 * 
	 * @param context 上下文环境
	 * @param key 存储节点名称
	 * @param value 存储节点值 boolean类型
	 */
	public static void putBoolean(Context context, String key, boolean value) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}

	/**
	 * @param context 上下文环境
	 * @param key 要获取节点的名称
	 * @param defValue 该节点的默认值
	 * @return 返回boolean类型的标记
	 */
	public static boolean getBoolean(Context context, String key, boolean defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 把int类型的开关状标记存入sp
	 * 
	 * @param context 上下文环境
	 * @param key 存储节点名称
	 * @param value 存储节点值 int类型
	 */
	public static void putInt(Context context, String key, int value) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}

	/**
	 * @param context 上下文环境
	 * @param key 要获取节点的名称
	 * @param defValue 该节点的默认值
	 * @return 返回int类型的标记
	 */
	public static int getInt(Context context, String key, int defValue) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}

	/**
	 * 从sp中移除指定节点
	 * 
	 * @param context 上下文环境
	 * @param key 需要移除的节点名称
	 */
	public static void remove(Context context, String key) {
		if (sp == null) {
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().remove(key).commit();
	}
}
