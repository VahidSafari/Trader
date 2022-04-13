package com.safari.traderbot.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class StringUtil {

	public static boolean isEmpty(String str) {
		if(str == null) 
			return true; 
		String tempStr = str.trim(); 
		if(tempStr.length() == 0)
			return true; 
		if(tempStr.equals("null"))
			return true;
		return false; 
	}

	public static String createLinkString(Map<String, String> params) {
		
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		StringBuilder stringBuilder = new StringBuilder();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (i == keys.size() - 1) {
				stringBuilder.append(key).append("=").append(value);
			} else {
				stringBuilder.append(key).append("=").append(value).append("&");
			}
		}
		return stringBuilder.toString();
	}
}
