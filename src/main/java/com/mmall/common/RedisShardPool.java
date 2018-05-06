package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/2.
 */
public class RedisShardPool {

    private static ShardedJedisPool pool;//jedis连接池
    private static Integer maxTotal= Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20"));//
    private static Integer maxIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle"));//在jedispool中最大的idle状态(空闲的)jedis实例个数
    private static Integer minIdle=Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle"));//在jedispool中最小的idle状态(空闲的)jedis实例个数
    private static Boolean testOnBorrow=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.min.idle"));//
    private static Boolean testOnReturn=Boolean.parseBoolean(PropertiesUtil.getProperty("redis.min.idle"));

    private static String redis1Ip=PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port=Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    private static String redis2Ip=PropertiesUtil.getProperty("redis2.ip");
    private static Integer redis2Port=Integer.parseInt(PropertiesUtil.getProperty("redis2.port"));
    private static void initPool(){
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);
        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);
        config.setBlockWhenExhausted(true);
        JedisShardInfo info1=new JedisShardInfo(redis1Ip,redis1Port);
        JedisShardInfo info2=new JedisShardInfo(redis2Ip,redis2Port);

        List<JedisShardInfo> jedisShardInfoList=new ArrayList<JedisShardInfo>();
        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);

        pool= new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static {
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }

    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }

    public static void main(String[] args) {
        ShardedJedis jedis=pool.getResource();
        for(int i=0;i<10;i++){
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);
        System.out.println("ojbk");
    }
}
