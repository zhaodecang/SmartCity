package com.zdc.smartcity.domain;

import java.util.ArrayList;

/**
 * description:所有数据
 * 
 * @author zhaodecang
 * @date 2016-9-23上午8:22:48
 */
public class NewsTabBean {

	public NewsTab data;

	/**
	 * description:所有获取到新闻的数据
	 * 
	 * @author zhaodecang
	 * @date 2016-9-23上午8:22:31
	 */
	public class NewsTab {
		public String countcommenturl;
		public String more;
		public String title;
		public ArrayList<NewsData> news;
		public ArrayList<TopNewsData> topnews;
		public ArrayList<TopicData> topic;

	}

	/**
	 * description:所有新闻数据
	 * 
	 * @author zhaodecang
	 * @date 2016-9-23上午8:22:16
	 */
	public class NewsData extends BaseNews {
		public String listimage;
	}

	/**
	 * description:头条数据
	 * 
	 * @author zhaodecang
	 * @date 2016-9-23上午8:22:08
	 */
	public class TopNewsData extends BaseNews {
		public String topimage;
	}

	/**
	 * description:封装公共属性
	 * 
	 * @author zhaodecang
	 * @date 2016-9-23上午8:21:28
	 */
	class BaseNews {
		public String commentlist;
		public String commenturl;
		public boolean commment;
		public String pubdate;
		public String title;
		public String type;
		public String url;
		public int id;
	}

	/**
	 * description:专题数据
	 * 
	 * @author zhaodecang
	 * @date 2016-9-23上午8:21:50
	 */
	public class TopicData {
		public String description;
		public String listimage;
		public String title;
		public String url;
		public int sort;
		public int id;
	}
}
