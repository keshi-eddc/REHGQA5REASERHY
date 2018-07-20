package com.edmi.site.dianping.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 对应config.properties
 * @author conner
 *
 */
@Component("dianping")
public class DianpingConfig {
	
	/**
	 * 已登录的手机帐号
	 */
	@Value(value = "${long_phone_queue}")
	private String[] long_phone_queue;
	
	@Value(value = "${login_user_phone_queue}")
	private String[] login_user_phone_queue;
	
	@Value(value = "${phone}")
	private String phone;
	
	@Value(value = "${task_user}")
	private String task_user;
	
	@Value(value = "${task_user_check}")
	private String task_user_check;
	
	@Value(value = "${task_shop_detail}")
	private String task_shop_detail;

	public String[] getLong_phone_queue() {
		return long_phone_queue;
	}

	public String[] getLogin_user_phone_queue() {
		return login_user_phone_queue;
	}

	public String getPhone() {
		return phone;
	}

	public String getTask_user() {
		return task_user;
	}

	public String getTask_user_check() {
		return task_user_check;
	}

	public String getTask_shop_detail() {
		return task_shop_detail;
	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println(context.getBean(DianpingConfig.class).getPhone());
		for (String phone : context.getBean(DianpingConfig.class).getLong_phone_queue()) {
			System.out.println(phone);
		}
	}

}
