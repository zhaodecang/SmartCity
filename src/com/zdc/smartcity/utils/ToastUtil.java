package com.zdc.smartcity.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
	public static void showLong(Context context, String str) {
		Toast.makeText(context, str, 1).show();
	}

	public static void show(Context context, String str) {
		Toast.makeText(context, str, 0).show();
	}
}
