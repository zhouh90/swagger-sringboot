package com.qaf.apitest;

import java.util.HashMap;
import java.util.Map;

import com.qaf.utils.HttpClientUtil;

/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年7月4日 下午3:07:42
 * @描述
 */
public class TestAPI {

	public static void main(String[] args) {
		testP();
	}

	private static void testP() {
		String url = "http://127.0.0.1:8080/qaf/p";
		Map<String, String> headers = new HashMap<>();
		String result = HttpClientUtil.doGet(url, new HashMap<>(), headers);
		System.out.println(result);
	}
}
