package com.ajie.resource;

import com.ajie.api.weixin.vo.JsConfig;

/**
 * 获取微信公众号相关信息
 *
 * @author niezhenjie
 *
 */
public interface WeixinResource {
	/** sccess_token redis key */
	public static final String REDIS_KEY_TOKEN = "ACCESS_TOKEN";
	/** jsapi_ticket redis key */
	public static final String REDIS_KEY_JSTICKET = "JSAPI_TICKET";

	/** token和jsticket过期时间为两个小时，7000秒刷新一次 */
	public static final int REDIS_EXPIRE_TIME = 7000;

	/**
	 * 获取公众号js api配置
	 * 
	 * @return
	 */
	JsConfig getJsConfiig();

	/**
	 * 获取公众号appId
	 * 
	 * @return
	 */
	String getAppId();

	/**
	 * 获取access token
	 * 
	 * @return
	 */
	String getAccessToken();

}
