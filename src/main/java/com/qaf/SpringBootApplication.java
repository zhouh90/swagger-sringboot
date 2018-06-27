package com.qaf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * @author 周 浩
 * @email zhou_eric90@163.com
 * @date 2018年6月27日 上午9:36:57
 * @描述
 */
@org.springframework.boot.autoconfigure.SpringBootApplication(exclude = { DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class SpringBootApplication {

	private static final Logger logger = LoggerFactory.getLogger(SpringBootApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootApplication.class, args);
		logger.info("SpringBoot start success !");
	}

}
