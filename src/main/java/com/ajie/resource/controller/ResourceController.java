package com.ajie.resource.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ajie.api.ip.IpQueryVo;
import com.ajie.api.weixin.vo.JsConfig;
import com.ajie.chilli.common.ResponseResult;
import com.ajie.chilli.utils.Toolkits;
import com.ajie.resource.ResourceService;
import com.ajie.resource.WeixinResource;

/**
 * 资源控制器
 *
 * @author niezhenjie
 *
 */
@Controller
public class ResourceController {

	@Resource
	private ResourceService resourceService;

	@Resource(name = "remoteResourceService")
	private ResourceService remoteService;

	@ResponseBody
	@RequestMapping("/getwxresource")
	public ResponseResult getwxresource(HttpServletRequest request, HttpServletResponse response) {
		WeixinResource wxResource = resourceService.getWeixinResource();
		if (null == wxResource)
			return ResponseResult.empty();
		JsConfig config = wxResource.getJsConfiig();
		config.sign("http://www.ajie18.top/blog");
		return ResponseResult.success(wxResource);
	}

	@ResponseBody
	@RequestMapping("/queryIp")
	public ResponseResult queryIp(HttpServletRequest request, HttpServletResponse response) {
		String ip = request.getParameter("ip");
		int provider = Toolkits.toInt(request.getParameter("provider"), 0);
		IpQueryVo vo = resourceService.queryIpAddress(ip, provider);
		if (null == vo)
			return ResponseResult.empty();
		return ResponseResult.success(vo);
	}

	@ResponseBody
	@RequestMapping("/remoteQueryIp")
	public ResponseResult remoteQueryIp(HttpServletRequest request, HttpServletResponse response) {
		String ip = request.getParameter("ip");
		IpQueryVo vo = remoteService.queryIpAddress(ip, 0);
		if (null == vo)
			return ResponseResult.empty();
		return ResponseResult.success(vo);
	}

}
