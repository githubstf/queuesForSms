package com.buybal.queues.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
	public static String md5(String str) {
		// 确定计算方法
		MessageDigest md5;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
		byte[] data;
		try {
			data = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		byte[] md5Date = md5.digest(data);
		return byteArray2HexString(md5Date);
	}

	private static final String hexChars = "0123456789ABCDEF";

	/**
	 * byte数组转换成Hex字符串
	 * 
	 * @param data
	 * @return
	 */
	private static String byteArray2HexString(byte[] data) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length; i++) {
			byte lo = (byte) (0x0f & data[i]);
			byte hi = (byte) ((0xf0 & data[i]) >>> 4);
			sb.append(hexChars.charAt(hi)).append(hexChars.charAt(lo));
		}
		return sb.toString();
	}
}
