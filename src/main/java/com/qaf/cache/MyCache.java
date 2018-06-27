package com.qaf.cache;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年6月27日 下午1:59:46
 * @描述
 */
@Component("myCache")
public class MyCache {

	private Map<String, Object> cacheMap = new HashMap<String, Object>();

	public void put(String key, Object value) throws Exception {
		if (key == null) {
			throw new Exception("KEY不能为null");
		}
		this.cacheMap.put(key, value);
	}

	public Object get(String key) {
		return this.cacheMap.get(key);
	}
}
