package com.zdc.smartcity.utils;

import android.content.Context;
import android.os.Bundle;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

/**
 * description:语音服务工具类
 * 
 * @author zhaodecang
 * @date 2016-9-30上午9:15:07
 */
public class VoiceUtils {

	private Context mContext;

	public VoiceUtils(Context context) {
		this.mContext = context;

		// 将“12345678”替换成您申请的APPID，申请地址：http://open.voicecloud.cn
		SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=57de9d4d");
	}

	/**
	 * 开启界面的语音识别
	 */
	public void startUIVoiceListen(RecognizerDialogListener listener) {
		// 1.创建SpeechRecognizer对象，第二个参数：本地听写时传InitListener
		RecognizerDialog iatDialog = new RecognizerDialog(mContext,
				new MyInitListener());
		// 2.设置听写参数，同上节
		iatDialog.setParameter(SpeechConstant.DOMAIN, "iat");
		iatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
		iatDialog.setParameter(SpeechConstant.ACCENT, "mandarin ");
		// 3.设置回调接口, 这里需要别的类传递过来, 当识别成功后, 需要回传给那个类
		iatDialog.setListener(listener);
		// 4.开始听写
		iatDialog.show();
	}

	/**
	 * 把给定的字符串, 播放出来
	 * 
	 * @param text
	 */
	public void speakText(String text) {
		// 1.创建SpeechSynthesizer对象, 第二个参数：本地合成时传InitListener
		SpeechSynthesizer mTts = SpeechSynthesizer.createSynthesizer(mContext, null);
		// 2.合成参数设置，详见《科大讯飞MSC API手册(Android)》SpeechSynthesizer 类
		mTts.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");// 设置发音人
		mTts.setParameter(SpeechConstant.SPEED, "50");// 设置语速
		mTts.setParameter(SpeechConstant.VOLUME, "80");// 设置音量，范围0~100
		// 设置合成音频保存位置（可自定义保存位置），保存在“./sdcard/iflytek.pcm”
		// 保存在SD卡需要在AndroidManifest.xml添加写SD卡权限
		// 如果不需要保存合成音频，注释该行代码
		// mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH,
		// "./sdcard/iflytek.pcm");
		// 3.开始合成
		mTts.startSpeaking(text, new MySynthesizerListener());
	}

	class MySynthesizerListener implements SynthesizerListener {

		@Override
		public void onBufferProgress(int arg0, int arg1, int arg2, String arg3) {

		}

		@Override
		public void onCompleted(SpeechError arg0) {

		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

		@Override
		public void onSpeakBegin() {

		}

		@Override
		public void onSpeakPaused() {

		}

		@Override
		public void onSpeakProgress(int arg0, int arg1, int arg2) {

		}

		@Override
		public void onSpeakResumed() {

		}
	}

	class MyInitListener implements InitListener {
		@Override
		public void onInit(int arg0) {

		}
	}
}
