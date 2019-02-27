package com.ajie.resource.vo;

import com.ajie.api.weixin.vo.JsConfig;
import com.ajie.resource.WeixinResource;

/**
 *
 *
 * @author niezhenjie
 *
 */
public class WeixinResourceVo implements WeixinResource {

	private String appId;

	private JsConfig jsConfig;

	private String accessToken;

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setJsConfig(JsConfig config) {
		this.jsConfig = config;
	}

	public void setAccessToken(String token) {
		this.accessToken = token;
	}

	@Override
	public JsConfig getJsConfiig() {
		return jsConfig;
	}

	@Override
	public String getAppId() {
		return appId;
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

}
