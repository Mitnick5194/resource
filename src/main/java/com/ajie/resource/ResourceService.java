package com.ajie.resource;

import com.ajie.api.ip.IpQueryVo;

/**
 * 资源服务
 *
 * @author niezhenjie
 *
 */
public interface ResourceService {

	WeixinResource getWeixinResource();

	/**
	 * 指定接口商查询ip
	 * 
	 * @param ip
	 * @param provider
	 *            见 IpQueryApi.PROVIDER_XXX;0表示使用默认，及命令模式
	 * @return
	 */
	IpQueryVo queryIpAddress(String ip, int provider);

}
