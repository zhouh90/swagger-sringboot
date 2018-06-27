package com.qaf.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年6月27日 上午9:40:07
 * @描述
 */

@RestController
@RequestMapping("qaf")
@Api("swaggerDemoController相关的api")
public class QAFHomeController {

	@ApiOperation(value = "根据出生年份计算当前年龄", notes = "计算年龄")
	@ApiImplicitParam(name = "year", value = "出生年份", paramType = "path", required = true, dataType = "String")
	@RequestMapping(value = "age/{year}", method = RequestMethod.GET)
	public String printAge(@PathVariable String year) {
		java.text.SimpleDateFormat formater = new java.text.SimpleDateFormat("yyyy");
		String curYear = formater.format(new Date());
		try {
			int age = Integer.parseInt(curYear) - Integer.parseInt(year);
			if (age <= 0) {
				return "对不起，您还没出生！";
			}
			return "您的年龄为：" + age;
		} catch (NumberFormatException e) {
			return "输入的年份不合法";
		}
	}

}
