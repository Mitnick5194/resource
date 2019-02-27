package com.ajie.resource.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ajie.api.ApiInvokeException;
import com.ajie.api.ip.IpQueryApi;
import com.ajie.api.ip.IpQueryVo;
import com.ajie.api.ip.impl.IpQueryApiImpl;
import com.ajie.api.weixin.WeixinApi;
import com.ajie.api.weixin.impl.WeixinApiImpl;
import com.ajie.api.weixin.vo.JsConfig;
import com.ajie.chilli.cache.redis.RedisClient;
import com.ajie.chilli.cache.redis.RedisException;
import com.ajie.chilli.remote.RemoteCmd;
import com.ajie.chilli.support.Worker;
import com.ajie.resource.ResourceService;
import com.ajie.resource.WeixinResource;
import com.ajie.resource.vo.WeixinResourceVo;

/**
 * 资源服务
 *
 * @author niezhenjie
 *
 */
public class ResourceServiceImpl implements ResourceService, Worker {
	private static Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

	private IpQueryApi ipQueryApi;

	private WeixinApi weixinApi;

	private RedisClient redisClient;

	/** 公众号appid */
	private String appId;

	/** 公众号密钥 */
	private String secret;

	/** 高德地图的key */
	private String gaodeKey;

	private RemoteCmd remoteCmd;

	public ResourceServiceImpl() {
		ipQueryApi = new IpQueryApiImpl();
		weixinApi = new WeixinApiImpl();
	}

	public void setRemoteCmd(RemoteCmd remoteCmd) {
		this.remoteCmd = remoteCmd;
	}

	public void setAppid(String appId) {
		this.appId = appId;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public void setIpQueryApi(IpQueryApi api) {
		this.ipQueryApi = api;
	}

	public IpQueryApi gtIpQueryApi() {
		return ipQueryApi;
	}

	public void setRedisClient(RedisClient redisClient) {
		this.redisClient = redisClient;
	}

	public RedisClient getRedisClient() {
		return redisClient;
	}

	public void setGaodeKey(String key) {
		this.gaodeKey = key;
	}

	@Override
	public WeixinResource getWeixinResource() {
		String token = redisClient.get(WeixinResource.REDIS_KEY_TOKEN);
		if (null == token) {
			token = weixinApi.getAccessToken(this.appId, this.secret);
			try {
				redisClient.set(WeixinResource.REDIS_KEY_TOKEN, token);
				redisClient
						.expire(WeixinResource.REDIS_KEY_TOKEN, WeixinResource.REDIS_EXPIRE_TIME);
			} catch (RedisException e) {
				logger.error("", e);
			}

		}
		String jsTicket = redisClient.get(WeixinResource.REDIS_KEY_JSTICKET);
		if (null == jsTicket) {
			jsTicket = weixinApi.getJsTicket(token);
			try {
				redisClient.set(WeixinResource.REDIS_KEY_JSTICKET, jsTicket);
				redisClient.expire(WeixinResource.REDIS_KEY_JSTICKET,
						WeixinResource.REDIS_EXPIRE_TIME);
			} catch (RedisException e) {
				logger.error("", e);
			}

		}
		WeixinResourceVo vo = new WeixinResourceVo();
		vo.setAccessToken(token);
		JsConfig config = new JsConfig(this.appId, jsTicket);
		vo.setJsConfig(config);
		vo.setAppId(this.appId);
		return vo;
	}

	@Override
	public IpQueryVo queryIpAddress(String ip, int provider) {
		if(0 == provider){
			provider = IpQueryApi.PROVIDER_CMD.getId();
		}
		if (provider == IpQueryApi.PROVIDER_GAODE.getId()) {
			ipQueryApi.injectGaodeKey(gaodeKey);
		} else if (provider == IpQueryApi.PROVIDER_CMD.getId()) {
			ipQueryApi.injectRemoteCmd(remoteCmd);
		}
		IpQueryVo vo;
		try {
			vo = ipQueryApi.query(ip, provider);
			return vo;
		} catch (ApiInvokeException e) {
		}
		return null;
	}

	@Override
	public void work() throws Exception {
		// 先删除缓存
		redisClient.del(WeixinResource.REDIS_KEY_TOKEN);
		redisClient.del(WeixinResource.REDIS_KEY_JSTICKET);
		getWeixinResource();
		logger.info("刷新access token 和 js ticket");
	}

}
