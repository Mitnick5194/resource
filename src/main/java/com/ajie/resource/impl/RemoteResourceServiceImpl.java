package com.ajie.resource.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ajie.api.ip.IpQueryVo;
import com.ajie.chilli.common.ResponseResult;
import com.ajie.chilli.utils.HttpClientUtil;
import com.ajie.chilli.utils.common.JsonUtils;
import com.ajie.resource.ResourceService;
import com.ajie.resource.WeixinResource;
import com.alibaba.fastjson.JSONObject;

/**
 *
 *
 * @author niezhenjie
 *
 */
public class RemoteResourceServiceImpl implements ResourceService {

	private static final Logger logger = LoggerFactory.getLogger(RemoteResourceServiceImpl.class);

	/** resource系统链接 */
	private String resourceURL;

	public void setResourceURL(String url) {
		this.resourceURL = url;
	}

	public String getResourceURL() {
		return resourceURL;
	}

	@Override
	public WeixinResource getWeixinResource() {
		String url = genUrl("getwxresource");
		try {
			String ret = HttpClientUtil.doGet(url);
			ResponseResult response = JsonUtils.toBean(ret, ResponseResult.class);
			return JsonUtils.toBean((JSONObject) response.getData(), WeixinResource.class);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}

	@Override
	public IpQueryVo queryIpAddress(String ip, int provider) {
		String url = genUrl("queryIp");
		Map<String, String> param = new HashMap<String, String>();
		param.put("ip", ip);
		param.put("provider", provider + "");
		try {
			String ret = HttpClientUtil.doGet(url, param);
			if (null == ret) {
				return null;
			}
			ResponseResult response = JsonUtils.toBean(ret, ResponseResult.class);
			return JsonUtils.toBean((JSONObject) response.getData(), IpQueryVo.class);
		} catch (IOException e) {
			logger.error("", e);
		}
		return null;
	}

	/**
	 * 拼接远程链接
	 * 
	 * @param method
	 *            控制器方法名
	 * @return
	 */
	private String genUrl(String method) {
		if (!resourceURL.startsWith("http")) {
			throw new IllegalArgumentException("resource系统链接错误" + resourceURL);
		}
		if (!resourceURL.endsWith("/")) {
			resourceURL += "/";
		}
		return resourceURL + method + ".do";
	}

}
