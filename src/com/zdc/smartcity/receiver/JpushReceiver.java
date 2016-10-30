package com.zdc.smartcity.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zdc.smartcity.utils.ToastUtil;

public class JpushReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ToastUtil.show(context, "收到推送消息");
	}
}
