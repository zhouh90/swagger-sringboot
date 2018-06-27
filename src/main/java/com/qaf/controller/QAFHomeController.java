package com.qaf.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qaf.cache.MyCache;
import com.qaf.entity.TokenEntity;
import com.qaf.exception.NotNullException;
import com.qaf.utils.DateUtils;
import com.qaf.utils.MD5Util;
import com.qaf.utils.R;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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

	@Autowired
	private MyCache cache;

	@ApiOperation(value = "登录获取凭证", notes = "登录")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "userName", value = "用户名", paramType = "query", required = true, dataType = "String"),
			@ApiImplicitParam(name = "passWord", value = "用户密码", paramType = "query", required = true, dataType = "String")})
	@RequestMapping(value = "login", method = RequestMethod.POST)
	public R login(@RequestParam String userName,
			@RequestParam String passWord) {
		if ("admin".equals(userName) && "qwer`123".equals(passWord)) {
			String token = UUID.randomUUID().toString().replace("-", "")
					.toUpperCase();
			String uid = MD5Util.md5(userName);
			TokenEntity tokenEntity = new TokenEntity(
					TokenEntity.LOGIN_TOKEN_KEY_PREFIX + uid, token);
			tokenEntity.setCreateTime(new Date());
			tokenEntity.setExpire(5 * 60 * 1000);
			try {
				cache.put(tokenEntity.getTokenKey(), tokenEntity);
				return R.ok().put("msg", "登录成功").put("token", token).put("uid",
						uid);
			} catch (Exception e) {
				e.printStackTrace();
				return R.error().put("msg", "用户凭证保存失败");
			}
		}
		return R.error().put("msg", "用户验证失败");

	}

	@ApiOperation(value = "根据出生年份计算当前年龄", notes = "计算年龄")
	@ApiImplicitParams({
			@ApiImplicitParam(name = "year", value = "出生年份", paramType = "path", required = true, dataType = "String"),
			@ApiImplicitParam(name = "token", value = "用户凭证", paramType = "header", required = true, dataType = "String"),
			@ApiImplicitParam(name = "uid", value = "用户ID", paramType = "header", required = true, dataType = "String")})
	@RequestMapping(value = "age/{year}", method = RequestMethod.POST)
	public R printAge(@PathVariable String year,
			@RequestHeader("token") String token,
			@RequestHeader("uid") String uid) {
		R r = checkUser(token, uid);
		if ((int) r.get("code") == 500) {
			return R.error().put("msg", r.get("msg"));
		}
		SimpleDateFormat formater = new SimpleDateFormat("yyyy");
		String curYear = formater.format(new Date());
		try {
			int age = Integer.parseInt(curYear) - Integer.parseInt(year);
			if (age <= 0) {
				return R.error().put("msg", "对不起，您还没出生");
			}
			return R.ok().put("msg", "您的年龄为：" + age);
		} catch (NumberFormatException e) {
			return R.error().put("msg", "输入的年份不合法");
		}
	}

	private R checkUser(String token, String uid) {
		if (StringUtils.isBlank(token) || StringUtils.isBlank(uid)) {
			return R.error().put("msg", "用户未登陆");
		}
		String tokenKey = TokenEntity.LOGIN_TOKEN_KEY_PREFIX + uid;
		try {
			TokenEntity tokenEntity = (TokenEntity) cache.get(tokenKey);
			if (tokenEntity == null) {
				return R.error().put("msg", "用户未登陆");
			}
			String sToekn = tokenEntity.getToken();
			if (!token.equals(sToekn)) {
				return R.error().put("msg", "用户验证失败");
			}
			long sExpire = tokenEntity.getExpire();
			long rExpire = DateUtils.calculateTime(tokenEntity.getCreateTime(),
					new Date());
			if (rExpire > sExpire) {
				return R.error().put("msg", "用户凭证已失效，重新登录");
			}
			return R.ok().put("msg", "用户验证成功");
		} catch (NotNullException e) {
			return R.error().put("msg", "用户验证失败");
		} catch (Exception e) {
			return R.error().put("msg", "用户未登陆");
		}
	}

}
