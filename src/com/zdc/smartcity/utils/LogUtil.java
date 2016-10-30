package com.zdc.smartcity.utils;

import android.util.Log;

/**
 * 用于打印各种等级的log信息，可通过设置flag随时关闭log信息的打印
 * 
 * @author zhaodecang
 */
public class LogUtil {
	// 设置为false 关闭所有log信息
	private static boolean flag = true;

	public static void i(String tag, String msg) {
		if (flag) {
			Log.i(tag, msg);
		}
	}

	public static void e(String tag, String msg) {
		if (flag) {
			Log.e(tag, msg);
		}
	}
}
