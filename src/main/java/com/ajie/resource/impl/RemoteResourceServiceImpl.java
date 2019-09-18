package com.ajie.resource.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ajie.api.ip.IpQueryVo;
import com.ajie.chilli.common.ResponseResult;
import com.ajie.chilli.http.HttpInvoke;
import com.ajie.chilli.http.Parameter;
import com.ajie.chilli.http.exception.InvokeException;
import com.ajie.resource.ResourceService;
import com.ajie.resource.WeixinResource;

/**
 * 资源服务远程接口
 *
 * @author niezhenjie
 *
 */
public class RemoteResourceServiceImpl implements ResourceService {

	private static final Logger logger = LoggerFactory
			.getLogger(RemoteResourceServiceImpl.class);

	/** 调用器 */
	private HttpInvoke invoke;


	public void setUrls(List<String> urls) {
		HttpInvoke invoke = HttpInvoke.getInstance(urls);
		this.invoke = invoke;
	}

	@Override
	public WeixinResource getWeixinResource() {
		try {
			ResponseResult result = invoke.invoke("getwxresource",
					(Parameter) null);
			return result.getData(WeixinResource.class);
		} catch (InvokeException e1) {
			logger.error(e1.getMessage(), e1);
		}
		return null;
	}

	@Override
	public IpQueryVo queryIpAddress(String ip, int provider) {
		ResponseResult result;
		try {
			result = invoke.invoke("queryIp", Parameter.valueOf("ip", ip),
					Parameter.valueOf("provider", provider + ""));
			return result.getData(IpQueryVo.class);
		} catch (InvokeException e) {
			logger.error(e.getMessage(), e);
		}
		return IpQueryVo._nil;
	}

}
