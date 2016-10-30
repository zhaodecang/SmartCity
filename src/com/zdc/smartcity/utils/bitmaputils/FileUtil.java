package com.zdc.smartcity.utils.bitmaputils;

import java.io.File;
import java.io.FileNotFoundException;

import android.os.Environment;
import android.os.StatFs;

/**
 * description:文件信息相关的工具类
 * 
 * @author zhaodecang
 * @date 2016-10-2下午5:22:50
 */
public class FileUtil {

	/**
	 * 获取实际SD卡路径 有的手机SD卡路径是storage/sdcard1
	 * 
	 * @return
	 */
	public static String getRealSdCardPath() {
		String sdcard0 = "/storage/sdcard0";
		String sdcard1 = "/storage/sdcard1";
		File file = new File(sdcard1);
		// 判断是否存在/storage/sdcard1
		if (file.exists()) {
			System.out.println("================" + sdcard1);
			return sdcard1;
		} else {
			System.out.println("================" + sdcard0);
			return sdcard0;
		}
	}

	/**
	 * 检查SD卡是否存在
	 * 
	 * @return 返回true 表示存在
	 */
	public static boolean checkSdCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 获取指点磁盘路径的可用/总空间大小
	 * 
	 * @param path 要获取空间大小信息的文件路径
	 * @return 数组fileSize[0] 表示可用大小,fileSize[1] 表示总的大小
	 * @exception FileNotFoundException
	 */
	public static long[] getFileSize(String path) throws FileNotFoundException {
		File file = new File(path);
		if (file.exists()) {
			// 获取可用磁盘类
			StatFs mStatFs = new StatFs(path);
			// 获取区块的大小
			long blockSizeLong = mStatFs.getBlockSizeLong();
			// 获取总的区块的个数
			long blockCountLong = mStatFs.getBlockCountLong();
			// 获取可用区块的个数
			long availableBlocksLong = mStatFs.getAvailableBlocksLong();
			// 计算磁盘空间大小
			long totalSize = blockSizeLong * blockCountLong;
			long availabSize = blockSizeLong * availableBlocksLong;
			return new long[] { availabSize, totalSize };
		} else {
			return null;
		}
	}

	/** 获取手机SD卡总空间 **/
	public static long getSDcardTotalSize() {
		// 首先判断SD卡是否存在/挂载
		if (checkSdCard()) {
			// 存在 获取空间大小信息
			try {
				long[] fileSize = getFileSize(getRealSdCardPath());
				return fileSize[1];
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return 0;
			}
		} else {// 不存在就返回0
			return 0;
		}
	}

	/** 获取SD卡可用空间 **/
	public static long getSDcardAvailableSize() {
		// 首先判断SD卡是否存在/挂载
		if (checkSdCard()) {
			// 存在 获取空间大小信息
			try {
				long[] fileSize = getFileSize(getRealSdCardPath());
				return fileSize[0];
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return 0;
			}
		} else {// 不存在就返回0
			return 0;
		}
	}

	/** 获取手机内部存储总空间 **/
	public static long getPhoneTotalSize() {
		String path = Environment.getDataDirectory().getAbsolutePath();
		long[] fileSize;
		try {
			fileSize = getFileSize(path);
			return fileSize[1];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 获取手机内存存储可用空间
	 * 
	 * @return
	 */
	public static long getPhoneAvailableSize() {
		String path = Environment.getDataDirectory().getAbsolutePath();
		long[] fileSize;
		try {
			fileSize = getFileSize(path);
			return fileSize[0];
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
}
