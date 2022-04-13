package com.safari.traderbot.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MD5Util {

	public static String buildSignature(
			Map<String, String> sArray,
			String secretKey) {
		String mysign = "";
		try {
			String prestr =  StringUtil.createLinkString(sArray);
			prestr = prestr + "&secret_key=" + secretKey;
			mysign = getMD5String(prestr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mysign;
	}

	/**
	 * 生成32位大写MD5值
	 */
	private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String getMD5String(String str) {
		try {
			if (str == null || str.trim().length() == 0) {
				return "";
			}
			byte[] bytes = str.getBytes();
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(bytes);
			bytes = messageDigest.digest();
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(HEX_DIGITS[(aByte & 0xf0) >> 4]).append(HEX_DIGITS[aByte & 0xf]);
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
}
