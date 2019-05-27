package com.foruo.sc.permission.example.config;

import com.foruo.sc.permission.example.util.PropertiesLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局配置
 * 对应 permission.properties
 * @author GaoYuan
 */
@Configuration
@ConfigurationProperties(prefix = "permission.client")
@PropertySource("classpath:permission.properties")
@Component
public class PermissionConfig {
	private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);

	/**
	 * 保存全局属性值
	 */
	private static Map<String, String> map = new HashMap<>(16);

	private String permissionMethod;
	private Map<String,String> configs;

	public String getPermissionMethod() {
		return permissionMethod;
	}

	public void setPermissionMethod(String permissionMethod) {
		this.permissionMethod = permissionMethod;
	}

	public Map<String, String> getConfigs() {
		return configs;
	}

	public void setConfigs(Map<String, String> configs) {
		this.configs = configs;
	}

	/**
	 * 属性文件加载对象
	 */
	private static PropertiesLoader loader = new PropertiesLoader(
			"permission.properties");

	/**
	 * 获取配置
	 */
	public static String getConfig(String key) {
		if(loader == null){
			logger.info("缺失配置文件 - permission.properties");
			return null;
		}
		String value = map.get(key);
		if (value == null) {
			value = loader.getProperty(key);
			map.put(key, value != null ? value : StringUtils.EMPTY);
		}
		return value;
	}

}
