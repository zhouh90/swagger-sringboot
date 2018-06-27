package com.qaf.exception;
/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年6月27日 下午2:44:00
 * @描述
 */
public class NotNullException extends Exception {

	private static final long serialVersionUID = 1L;

	public NotNullException() {
	}

	public NotNullException(String msg) {
		super(msg);
	}
}
