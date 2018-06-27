package com.qaf.entity;
/**
 * @author 周 浩 
 * @email zhou_eric90@163.com 
 * @date 2018年6月27日 下午2:09:45 
 * @描述 
 */

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenEntity {

	public static final String LOGIN_TOKEN_KEY_PREFIX = "LOGIN_TK_";

	private String tokenKey;
	private String token;
	private Date createTime;
	private long expire;

	public TokenEntity() {
	}

	public TokenEntity(String tokenKey, String token) {
		this.tokenKey = tokenKey;
		this.token = token;
	}

}
