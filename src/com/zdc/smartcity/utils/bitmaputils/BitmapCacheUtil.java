package com.zdc.smartcity.utils.bitmaputils;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;

import com.zdc.smartcity.utils.LogUtil;

/**
 * @description 图片的三级缓存工具类
 * 
 * @author zhaodecang
 * @date 2016-10-2下午6:59:36
 */
public class BitmapCacheUtil {

	/** 获取网络图片资源-成功 **/
	public static final int SUCCESS = 0;
	/** 获取网络图片资源-失败 **/
	public static final int FAILED = 1;
	private static final String tag = "BitmapCacheUtil";
	private static final String CACHE_URL = FileUtil.getRealSdCardPath() + "/SmartCity";

	private Handler mHandler;
	private ExecutorService service;
	LruCache<String, Bitmap> memoryCache;

	public BitmapCacheUtil(Handler mHandler) {
		this.mHandler = mHandler;
		// 获取虚拟机的最大内存大小
		Runtime runtime = Runtime.getRuntime();
		// 设置缓存空间的最大大小为虚拟机内存大小的八分之一
		int memSize = (int) (runtime.maxMemory() / 8);
		memoryCache = new LruCache<String, Bitmap>(memSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
		// 创建一个线程池管理线程 指定最大创建10个线程循环使用
		service = Executors.newFixedThreadPool(10);
	}

	/**
	 * 获取通过图片的三级缓存保存的图片
	 * 
	 * @param imageUrl 图片资源地址
	 * @param position 如果使用listview展示数据就需要提供该图片在listview中的位置
	 * @return 获取到的bitmap对象
	 */
	public Bitmap getBitmapByUrl(String imageUrl, int position) {
		// 1.首先从内存中取
		Bitmap bitmap = getCacheFromMemory(imageUrl);
		if (bitmap != null) {
			// 取到内存数据就返回
			LogUtil.i(tag, "获取到内存中的缓存数据");
			return bitmap;
		}
		// 2.取不到就去本地文件中取
		bitmap = getCacheFromLocal(imageUrl);
		if (bitmap != null) {
			// 取到本地数据就返回并且向内存中保存一份
			LogUtil.i(tag, "内存中没有数据,获取到本地文件中的数据缓存");
			saveCache2Memory(imageUrl, bitmap);
			return bitmap;
		}
		// 3.取不到就通过网络获取 获取到之后直接发消息给子线程更新UI并保存缓存
		getBitmapFromServer(imageUrl, position);
		return null;
	}

	/** 获取内存中的缓存数据 **/
	public Bitmap getCacheFromMemory(String imageUrl) {
		return memoryCache.get(imageUrl);
	}

	/** 向内存中保存一份缓存数据 **/
	public void saveCache2Memory(String imageUrl, Bitmap bitmap) {
		LogUtil.i(tag, "向内存中保存缓存数据");
		memoryCache.put(imageUrl, bitmap);
	}

	/**
	 * 向本地保存图片缓存
	 * 
	 * @param imageUrl 图片的网路资源地址
	 * @param bitmap 要缓存到本地的图片资源
	 */
	public void saveCache2Local(String imageUrl, Bitmap bitmap) {
		OutputStream stream = null;
		try {
			File file = new File(CACHE_URL, md5Encoder(imageUrl));
			File parentFile = file.getParentFile();
			if (!parentFile.exists()) {
				parentFile.mkdirs();
			}
			// 压缩图片并以流的形式保存的SD卡
			stream = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, stream);
			LogUtil.i(tag, "向本地文件中保存缓存数据");
		} catch (Exception e) {
			LogUtil.i(tag, "向本地保存缓存数据出错");
		} finally {
			// 关流
			close(stream);
		}
	}

	/** 从本地获取缓存图片 **/
	public Bitmap getCacheFromLocal(String imageUrl) {
		try {
			File file = new File(CACHE_URL, md5Encoder(imageUrl));
			InputStream is = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (Exception e) {
			LogUtil.i(tag, "获取本地缓存数据出错");
			return null;
		}
	}

	/** 获取服务器图片资源 将获取到的图片资源发送到主线程显示 **/
	public void getBitmapFromServer(String url, int position) {
		// 开启子线程获取网络资源
		LogUtil.i(tag, "线程池执行新一个任务从网络获取图片数据");
		service.execute(new NetTask(url, position));
	}

	/** 运行在子线程获取网络数据的任务 **/
	private class NetTask implements Runnable {

		private String imageUrl;
		private int position;

		public NetTask(String imageUrl, int position) {
			this.imageUrl = imageUrl;
			this.position = position;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(imageUrl).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.connect();
				int code = conn.getResponseCode();
				if (code == 200) {
					// 获取网络资源成功
					InputStream is = conn.getInputStream();
					Bitmap bitmap = BitmapFactory.decodeStream(is);
					// 将获取到的图片发送的主线程显示
					Message msg = mHandler.obtainMessage();
					msg.what = SUCCESS;
					msg.obj = bitmap;
					msg.arg1 = position;
					mHandler.sendMessage(msg);
					// 将文件缓存到内存
					saveCache2Memory(imageUrl, bitmap);
					// 将文件缓存到本地
					saveCache2Local(imageUrl, bitmap);
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 发送获取网路资源失败的消息给主线程
				Message msg = mHandler.obtainMessage();
				msg.what = FAILED;
				msg.arg1 = position;
				mHandler.sendMessage(msg);
			} finally {
				if (conn != null) {
					// 断开与服务器的连接
					conn.disconnect();
				}
			}
		}
	}

	/** 对字符串进行MD5加密 **/
	private String md5Encoder(String str) {
		str = str + "zdc_md5_encoder";// 加盐
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(str.getBytes("utf-8"));
			StringBuilder sb = new StringBuilder();
			for (byte b : bs) {
				int i = b & 0xff;
				String hexStr = Integer.toHexString(i);
				if (hexStr.length() < 2) {
					hexStr = "0" + hexStr;
				}
				sb.append(hexStr);
			}
			return sb.toString();
		} catch (Exception e) {
			LogUtil.e(tag, "MD5加密失败");
			e.printStackTrace();
		}
		return null;
	}

	/** 用于关闭所有可关闭的资源 **/
	private void close(Closeable obj) {
		if (obj != null) {
			try {
				obj.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
