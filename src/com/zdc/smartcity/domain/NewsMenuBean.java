package com.zdc.smartcity.domain;

import java.util.List;

/**
 * description:新闻中心服务器数据集合
 * 
 * @author zhaodecang
 * @date 2016-9-18下午4:40:44
 */
public class NewsMenuBean {

	public int retcode;
	public int[] extend;
	public List<NewsMenuData> data;

	@Override
	public String toString() {
		return "NewsMenu [data=" + data + "]";
	}

	/**
	 * description:菜单栏条目对象
	 * 
	 * @author zhaodecang
	 * @date 2016-9-18下午4:41:16
	 */
	public class NewsMenuData {
		public int id, type;
		public String title, url;
		public List<NewsItemData> children;

		@Override
		public String toString() {
			return "NewsMenuData [children=" + children + "]";
		}
	}

	/**
	 * description:页签对象
	 * 
	 * @author zhaodecang
	 * @date 2016-9-18下午4:42:12
	 */
	public class NewsItemData {
		public int id, type;
		public String title, url;

		@Override
		public String toString() {
			return "NewsItemData [title=" + title + "]";
		}
	}
}
