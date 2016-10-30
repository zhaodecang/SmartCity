package com.zdc.smartcity.global;

public interface GlobalData {

	/** 服务器地址 **/
	String SERVER_URL = "http://10.0.2.2:8080/zhbj";
	// String SERVER_URL = "http://zhihuibj.sinaapp.com/zhbj";
	/** 服务器分类数据地址 **/
	String CATEGORY_URL = SERVER_URL + "/categories.json";
	/** 组图详情页数据地址 **/
	String PHOTOS_URL = SERVER_URL + "/photos/photos_1.json";
	/** 免费数据的服务器地址 **/
	String FREE_API_URL = "http://api.qingyunke.com/api.php?key=free&appid=0&msg=";
}
