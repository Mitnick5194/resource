package com.ajie.resource.impl;

import java.util.Date;

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
import com.ajie.chilli.support.TimingTask;
import com.ajie.chilli.support.Worker;
import com.ajie.chilli.thread.ThreadPool;
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
	private static Logger logger = LoggerFactory
			.getLogger(ResourceServiceImpl.class);

	private IpQueryApi ipQueryApi;

	private WeixinApi weixinApi;

	private RedisClient redisClient;

	/** 公众号appid */
	private String appId;

	/** 公众号密钥 */
	private String secret;

	/** 高德地图的key */
	private String gaodeKey;

	/** 高德地图的key */
	private String ipstackAccessKey;

	/** 远程命令服务 */
	private RemoteCmd remoteCmd;
	/** 线程池 */
	private ThreadPool threadPool;

	public ResourceServiceImpl() {
		ipQueryApi = new IpQueryApiImpl();
		weixinApi = new WeixinApiImpl();
	}

	public void setThreadPool(ThreadPool pool) {
		threadPool = pool;
		TimingTask.createTimingTask(threadPool, "update-resource", this,
				new Date(), WeixinResource.REDIS_EXPIRE_TIME * 1000);
	}

	public void setRemoteCmd(RemoteCmd remoteCmd) {
		this.remoteCmd = remoteCmd;
		ipQueryApi.injectRemoteCmd(this.remoteCmd);
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
		ipQueryApi.injectGaodeKey(gaodeKey);
	}

	public void setIpstackAccessKey(String key) {
		this.ipstackAccessKey = key;
		ipQueryApi.injectIpStackAccessKey(ipstackAccessKey);
	}

	@Override
	public WeixinResource getWeixinResource() {
		String token = redisClient.get(WeixinResource.REDIS_KEY_TOKEN);
		String jsTicket = redisClient.get(WeixinResource.REDIS_KEY_JSTICKET);
		if (null == token) {
			token = weixinApi.getAccessToken(this.appId, this.secret);
			if (null == token) {
				logger.error("无法刷新access token");
				return null;
			}
			// token更新了 jsTicket一定要更新
			jsTicket = getJsTicket(token);
			try {
				redisClient.set(WeixinResource.REDIS_KEY_TOKEN, token);
				redisClient.expire(WeixinResource.REDIS_KEY_TOKEN,
						WeixinResource.REDIS_EXPIRE_TIME);
			} catch (RedisException e) {
				logger.error("", e);
			}
		} else if (null == jsTicket) {
			jsTicket = getJsTicket(token);
		}
		WeixinResourceVo vo = new WeixinResourceVo();
		vo.setAccessToken(token);
		JsConfig config = new JsConfig(this.appId, jsTicket);
		vo.setJsConfig(config);
		vo.setAppId(this.appId);
		return vo;
	}

	private String getJsTicket(String token) {
		String jsTicket = weixinApi.getJsTicket(token);
		if (null == jsTicket) {
			logger.error("无法刷新jsTicket");
			return null;
		}
		try {
			redisClient.set(WeixinResource.REDIS_KEY_JSTICKET, jsTicket);
			redisClient.expire(WeixinResource.REDIS_KEY_JSTICKET,
					WeixinResource.REDIS_EXPIRE_TIME);
		} catch (RedisException e) {
			logger.error("", e);
		}
		return jsTicket;
	}

	@Override
	public IpQueryVo queryIpAddress(String ip, int provider) {
		if (0 == provider) {
			provider = IpQueryApi.PROVIDER_CMD.getId();
		}
		IpQueryVo vo;
		try {
			vo = ipQueryApi.query(ip, provider);
			return vo;
		} catch (ApiInvokeException e) {
		}
		return IpQueryVo._nil;
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
