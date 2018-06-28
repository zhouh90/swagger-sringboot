package com.qaf.apitest;

import java.util.HashMap;
import java.util.Map;

import com.qaf.utils.HttpClientUtil;

/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年6月27日 下午3:01:26
 * @描述
 */
public class TestLogin {

	public static void main(String[] args) {
		String url = "http://127.0.0.1:8080/qaf/login";
		Map<String, String> param = new HashMap<>();
		param.put("userName", "admin");
		param.put("passWord", "qwer`123");
		String result = HttpClientUtil.doPost(url, param, new HashMap<>());
		System.out.println(result);
	}

}
