package com.zdc.smartcity.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

/**
 * description:判断用户使用的是真机还是模拟器
 * 
 * @author zhaodecang
 * @date 2016-10-1下午7:15:29
 */
public class EmulatorUtils {
	public static boolean isEmulator(Context context) {
		String result = "";
		try {
			String[] args = { "/system/bin/cat", "/proc/cpuinfo" };
			ProcessBuilder cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			StringBuffer sb = new StringBuffer();
			String readLine = "";
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
			BufferedReader br = new BufferedReader(isr);
			while ((readLine = br.readLine()) != null) {
				sb.append(readLine);
			}
			br.close();
			result = sb.toString().toLowerCase();
			System.out.println(result);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return (!result.contains("arm")) || (result.contains("intel"))
				|| (result.contains("amd"));
	}
}
