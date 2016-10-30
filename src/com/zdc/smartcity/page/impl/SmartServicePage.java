package com.zdc.smartcity.page.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.domain.ConversationBean;
import com.zdc.smartcity.domain.VoiceBean;
import com.zdc.smartcity.domain.VoiceBean.WS;
import com.zdc.smartcity.global.GlobalData;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.utils.ResourcesUtils;
import com.zdc.smartcity.utils.VoiceUtils;

public class SmartServicePage extends BasePage implements OnClickListener {
	@ViewInject(R.id.lv_conversation)
	private ListView mListView;
	@ViewInject(R.id.et_question)
	private EditText etQuestion;
	@ViewInject(R.id.btn_start_listen)
	private Button btnStartListen;
	private VoiceUtils mVoiceUtils;
	private List<ConversationBean> conversationList;
	private ConversationAdapter mAdapter;
	private HttpUtils utils;

	public SmartServicePage(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 初始化标题栏
		tvTitle.setText("智慧服务");
		ibMenu.setVisibility(View.VISIBLE);
		// 填充内容
		View view = View.inflate(mActivity, R.layout.activity_cheat, null);
		ViewUtils.inject(this, view);
		flContent.removeAllViews();
		flContent.addView(view);
		btnStartListen.setOnClickListener(this);
		mVoiceUtils = new VoiceUtils(mActivity);
		// 初始化ListView是适配, 但是默认初始化ListView中是没有数据的.
		conversationList = new ArrayList<ConversationBean>();
		conversationList.add(new ConversationBean("请输入:你好", true, -1));
		conversationList.add(new ConversationBean("你好啊,感谢使用掌盟", false, -1));
		mAdapter = new ConversationAdapter();
		mListView.setAdapter(mAdapter);
		// 初始化一个请求网络数据的httputils对象
		utils = new HttpUtils();
	}

	@Override
	public void onClick(View v) {
		String text = etQuestion.getText().toString().trim();
		if (!TextUtils.isEmpty(text)) {
			etQuestion.setText("");
			answer(text);
		} else {
			// 开始语音识别
			mVoiceUtils.startUIVoiceListen(new MyRecognizerDialogListener());
		}
	}

	class MyRecognizerDialogListener implements RecognizerDialogListener {
		private StringBuilder mBuilder = new StringBuilder();

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {
			// 把json数据解析成字符串.
			String text = parserJson(results.getResultString());
			// System.out.println("识别结果: " + text);
			// Toast.makeText(MainActivity.this, "识别结果: " + text, 0).show();
			mBuilder.append(text); // 把数据拼接起来
			if (!isLast) {
				// 当前不是最后一次识别
				return;
			}
			// 识别完成, StringBuilder中存储着此次识别所有的数据.
			text = mBuilder.toString();
			mBuilder = new StringBuilder();
			answer(text);
		}

		@Override
		public void onError(SpeechError arg0) {
			Toast.makeText(mActivity, "识别失败", 0).show();
		}
	}

	/**
	 * 把json数据中的话提取出来
	 * 
	 * @param json
	 * @return
	 */
	public String parserJson(String json) {
		Gson gson = new Gson();
		VoiceBean bean = gson.fromJson(json, VoiceBean.class);
		List<WS> ws = bean.ws;
		StringBuilder mBuilder = new StringBuilder();
		for (int i = 0; i < ws.size(); i++) {
			mBuilder.append(ws.get(i).cw.get(0).w);
		}
		return mBuilder.toString();
	}

	private void answer(String text) {
		// 显示到ListView中.
		// 把提问的数据封装成javaBean, 放到集合中.
		conversationList.add(new ConversationBean(text, true, -1));
		mAdapter.notifyDataSetChanged();

		// 根据提问的数据, 来选择回答的数据, 加载到界面上显示, 让语音播报出来
		String answerText = text;
		int imageID = -1;
		if (text.contains("你好")) {
			answerText = "你好!!!";
		} else if (text.contains("美女")) {
			Random random = new Random();
			int index = random.nextInt(ResourcesUtils.mnAnswerText.length);
			answerText = ResourcesUtils.mnAnswerText[index];
			imageID = ResourcesUtils.mnAnswerImageID[index];
		} else if (text.contains("天王盖地虎")) {
			answerText = "小鸡炖蘑菇!";
			imageID = R.drawable.p1;
		} else if (text.contains("小龙女")) {
			answerText = "过儿, 你在哪里..";
			imageID = R.drawable.cyx;
		} else if (text.contains("苍老师")) {
			answerText = "苍老师是我最敬爱的老师,你也喜欢她吗？";
		} else {
			getAnswerFromServer(text);
			return;
		}
		answerTheQuestion(answerText, imageID);
	}

	private void answerTheQuestion(String answerText, int imageID) {
		// 把提问者的数据, 添加到集合中,显示.
		conversationList.add(new ConversationBean(answerText, false, imageID));
		mAdapter.notifyDataSetChanged();
		mListView.setSelection(conversationList.size() - 1);
		// 把文字说出来
		if (answerText.length() > 60) {
			mVoiceUtils.speakText(answerText.substring(0, 60));
		} else {
			mVoiceUtils.speakText(answerText);
		}
	}

	private void getAnswerFromServer(final String text) {
		String url = GlobalData.FREE_API_URL + text;
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject object = new JSONObject(responseInfo.result);
					String answerText = object.getString("content");
					answerText = answerText.replaceAll("\\{(.*?)\\}", "");
					answerTheQuestion(answerText, -1);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				answerTheQuestion(text, -1);
			}
		});
	}

	class ConversationAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return conversationList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(mActivity, R.layout.lv_cheat_item, null);
			}
			View answerView = convertView.findViewById(R.id.ll_answer);
			TextView tvAnswerText = (TextView) convertView
					.findViewById(R.id.tv_answer_text);
			ImageView ivAnswerImage = (ImageView) convertView
					.findViewById(R.id.iv_answer_image);
			TextView tvAskerText = (TextView) convertView.findViewById(R.id.tv_asker_text);
			ConversationBean bean = getItem(position);
			if (bean.isAsker()) {
				// 当前是提问者, 显示提问者的数据
				tvAskerText.setVisibility(View.VISIBLE);
				answerView.setVisibility(View.GONE);
				tvAskerText.setText(bean.getText());
			} else {
				// 当前是回答者, 显示回答者的数据
				tvAskerText.setVisibility(View.GONE);
				answerView.setVisibility(View.VISIBLE);
				tvAnswerText.setText(bean.getText());
				if (bean.getImageID() != -1) {
					ivAnswerImage.setVisibility(View.VISIBLE);
					ivAnswerImage.setImageResource(bean.getImageID());
				} else {
					ivAnswerImage.setVisibility(View.GONE);
				}
			}
			return convertView;
		}

		@Override
		public ConversationBean getItem(int position) {
			return conversationList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}
	}
}
