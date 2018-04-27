package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Administrator on 2018/4/25.
 */
public class RedisPool {
    private static JedisPool pool;//jedis连接池
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle"));//在jedispool中最大的idle状态(空闲的)jedis实例个数
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle"));//在jedispool中最小的idle状态(空闲的)jedis实例个数
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.min.idle"));//
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.min.idle"));

    private static String redisIp=PropertiesUtil.getProperty("redis.ip");
    private static Integer redisPort=Integer.parseInt(PropertiesUtil.getProperty("redis.port"));
    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);
        pool=new JedisPool(config,redisIp,redisPort);
    }

    static {
        initPool();
    }

    public static Jedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(Jedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(Jedis jedis){
        pool.returnBrokenResource(jedis);
    }


}

