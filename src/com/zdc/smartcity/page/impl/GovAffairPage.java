package com.zdc.smartcity.page.impl;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zdc.smartcity.R;
import com.zdc.smartcity.global.ConstantValue;
import com.zdc.smartcity.global.GlobalData;
import com.zdc.smartcity.page.BasePage;
import com.zdc.smartcity.utils.CacheUtil;
import com.zdc.smartcity.utils.SpUtil;
import com.zdc.smartcity.utils.ToastUtil;

public class GovAffairPage extends BasePage implements OnClickListener {

	@ViewInject(R.id.tv_weather_city)
	private TextView tvCity;
	@ViewInject(R.id.tv_weather_temp)
	private TextView tvTemp;
	@ViewInject(R.id.tv_weather_state)
	private TextView tvState;
	@ViewInject(R.id.tv_weather_wind)
	private TextView tvWind;
	@ViewInject(R.id.tv_weather_date1)
	private TextView tvDate1;
	@ViewInject(R.id.tv_weather_date2)
	private TextView tvDate2;
	@ViewInject(R.id.tv_weather_date3)
	private TextView tvDate3;
	private HttpUtils utils;
	private AlertDialog dialog;
	private EditText etCity;

	public GovAffairPage(Activity activity) {
		super(activity);
		utils = new HttpUtils();
	}

	@Override
	public void initData() {
		// 初始化标题栏
		tvTitle.setText("天气预报");
		ibMenu.setVisibility(View.VISIBLE);
		// 填充内容
		View view = View.inflate(mActivity, R.layout.pager_weather_forecast, null);
		ViewUtils.inject(this, view);
		flContent.addView(view);
		// 获取已经保存的城市 默认是北京
		String city = SpUtil.getString(mActivity, ConstantValue.CITY_NAME, "北京");
		tvCity.setOnClickListener(this);
		getWeatherData(city.trim());
	}

	/** 查询该城市的天气信息 **/
	private void getWeatherData(final String city) {
		final String cityCache = CacheUtil.getCache(mActivity, city + "WEATHER_INFO");
		if (!TextUtils.isEmpty(cityCache)) {
			processData(cityCache);
		}
		String url = GlobalData.FREE_API_URL + "天气" + city;
		utils.send(HttpMethod.GET, url, new RequestCallBack<String>() {
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				try {
					JSONObject object = new JSONObject(responseInfo.result);
					String allResult = object.getString("content");
					// 缓存天气信息数据
					CacheUtil.saveCache(mActivity, city + "WEATHER_INFO", allResult);
					// 处理天气信息数据
					processData(allResult);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				processData(cityCache);
			}
		});
	}

	/** 处理获取到的json数据 **/
	protected void processData(String allResult) {
		/*
		 * ★ 北京天气{br}霾，19℃，北风0级{br}★ 三天预测{br}[10月01日]
		 * 白天：霾，26℃，无持续风向；夜晚：霾，15℃，无持续风向{br}[10月02日]
		 * 白天：霾，27℃，无持续风向；夜晚：霾，16℃，无持续风向{br}[10月03日]
		 * 白天：霾，27℃，无持续风向；夜晚：霾，19℃，无持续风向
		 * 
		 * [10月01日] 白天：阵雨，30℃，无持续风向；夜晚：中雨，23℃，无持续风向
		 * [10月01日]\n\t白天：阵雨，30℃，无持续风向；\n\t夜晚：中雨，23℃，无持续风向
		 */
		try {
			String[] infos = allResult.split("\\{br\\}");
			String[] weatherState = infos[1].split("，");

			tvDate1.setText(infos[3].replace("] ", "]\n\t").replace("；", "；\n\t"));
			tvDate2.setText(infos[4].replace("] ", "]\n\t").replace("；", "；\n\t"));
			tvDate3.setText(infos[5].replace("] ", "]\n\t").replace("；", "；\n\t"));

			String currentCity = infos[0].substring(1, 4);
			tvCity.setText("当前城市:" + currentCity);
			SpUtil.putString(mActivity, ConstantValue.CITY_NAME, currentCity.trim());
			tvState.setText(weatherState[0]);
			tvTemp.setText(weatherState[1].replace("℃", "°"));
			tvWind.setText(weatherState[2]);
		} catch (Exception e) {
			ToastUtil.showLong(mActivity, "请输入具体城市名称");
			return;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_weather_city:
			showWeatherCityDialog();
			break;
		case R.id.bt_submit:
			String city = etCity.getText().toString().trim();
			if (TextUtils.isEmpty(city)) {
				ToastUtil.show(mActivity, "请输入正确的城市");
			} else {
				getWeatherData(city);
				dialog.dismiss();
			}
			break;
		case R.id.bt_cancel:
			dialog.dismiss();
			break;
		}
	}

	private void showWeatherCityDialog() {
		dialog = new AlertDialog.Builder(mActivity, R.style.WeatherCityDialog).create();//
		View view = View.inflate(mActivity, R.layout.dialog_weather_city, null);
		etCity = (EditText) view.findViewById(R.id.et_city);
		Button btnSubmit = (Button) view.findViewById(R.id.bt_submit);
		Button btnCancel = (Button) view.findViewById(R.id.bt_cancel);
		btnSubmit.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		dialog.setView(view, 0, 0, 0, 0);
		dialog.show();
	}
}
