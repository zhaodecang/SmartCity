package com.zdc.smartcity.domain;

public class ConversationBean {

	private String text;
	private boolean isAsker; // 是否是提问者
	private int imageID = -1; // 图片资源的id, 默认为: -1, 代表当前没有图片

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean isAsker() {
		return isAsker;
	}

	public void setAsker(boolean isAsker) {
		this.isAsker = isAsker;
	}

	public int getImageID() {
		return imageID;
	}

	public void setImageID(int imageID) {
		this.imageID = imageID;
	}

	public ConversationBean() {
		super();
	}

	public ConversationBean(String text, boolean isAsker, int imageID) {
		super();
		this.text = text;
		this.isAsker = isAsker;
		this.imageID = imageID;
	}

	@Override
	public String toString() {
		return "ConversationBean [text=" + text + ", isAsker=" + isAsker
				+ ", imageID=" + imageID + "]";
	}
}
