package com.mmall.util;

import com.mmall.common.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by Administrator on 2018/4/26.
 */
public class RedisPoolUtil {
    private static Logger logger= LoggerFactory.getLogger(RedisPoolUtil.class);

    public static Long expire(String key,int exTime){
        Jedis jedis=null;
        Long result=null;
        try{
            jedis= RedisPool.getJedis();
            result=jedis.expire(key, exTime);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;


    }

    public static String setEx(String key,String value,int exTime){
        Jedis jedis=null;
        String result=null;
        try{
            jedis=RedisPool.getJedis();
            result=jedis.setex(key,exTime,value);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static String set(String key,String value){
        Jedis jedis=null;
        String result=null;
        try{
            jedis=RedisPool.getJedis();
            result=jedis.set(key, value);
        }catch (Exception e){
            logger.error("set key:{} value:{} error",key,value,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    public static Long get(String key){
        Jedis jedis=null;
        Long result=null;
        try{
            jedis=RedisPool.getJedis();
            result=jedis.del(key);
        }catch (Exception e){
            logger.error("del key:{} value:{} error",key,e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

//    public static void main(String[] args) {
//        Jedis jedis=RedisPool.getJedis();
//        RedisPoolUtil.set("keyTest","value");
//    }



}
