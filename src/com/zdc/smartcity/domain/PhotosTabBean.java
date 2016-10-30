package com.zdc.smartcity.domain;

import java.util.List;

public class PhotosTabBean {
	public PhotosData data;

	@Override
	public String toString() {
		return "PhotosTabBean [data=" + data + "]";
	}

	public class PhotosData {
		public String title;
		public List<PhotosNews> news;

		@Override
		public String toString() {
			return "PhotosData [news=" + news + "]";
		}

		public class PhotosNews {
			public int id;
			public String type;
			public String title;
			public String pubdate;
			public String listimage;

			@Override
			public String toString() {
				return "PhotosNews [title=" + title + "]";
			}
		}
	}
}
