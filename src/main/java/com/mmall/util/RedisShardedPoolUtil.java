package com.mmall.util;

import com.mmall.common.RedisShardPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.ShardedJedis;

/**
 * Created by Administrator on 2018/5/2.
 */
public class RedisShardedPoolUtil {


    /**
     * Created by Administrator on 2018/4/26.
     */
    private static Logger logger= LoggerFactory.getLogger(RedisShardedPoolUtil.class);

        public static Long expire(String key,int exTime){
            ShardedJedis jedis=null;
            Long result=null;
            try{
                jedis= RedisShardPool.getJedis();
                result=jedis.expire(key, exTime);
            }catch (Exception e){
                logger.error("set key:{} value:{} error",key,e);
                RedisShardPool.returnBrokenResource(jedis);
                return result;
            }
            RedisShardPool.returnResource(jedis);
            return result;


        }

        public static String setEx(String key,String value,int exTime){
            ShardedJedis jedis=null;
            String result=null;
            try{
                jedis=RedisShardPool.getJedis();
                result=jedis.setex(key,exTime,value);
            }catch (Exception e){
                logger.error("set key:{} value:{} error",key,value,e);
                RedisShardPool.returnBrokenResource(jedis);
                return result;
            }
            RedisShardPool.returnResource(jedis);
            return result;
        }

        public static String set(String key,String value){
            ShardedJedis jedis=null;
            String result=null;
            try{
                jedis=RedisShardPool.getJedis();
                result=jedis.set(key, value);
            }catch (Exception e){
                logger.error("set key:{} value:{} error",key,value,e);
                RedisShardPool.returnBrokenResource(jedis);
                return result;
            }
            RedisShardPool.returnResource(jedis);
            return result;
        }

        public static String get(String key){
            ShardedJedis jedis=null;
            String result=null;
            try{
                jedis=RedisShardPool.getJedis();
                result=jedis.get(key);
            }catch (Exception e){
                logger.error("set key:{}  error",key,e);
                RedisShardPool.returnBrokenResource(jedis);
                return result;
            }
            RedisShardPool.returnResource(jedis);
            return result;
        }

        public static Long del(String key){
            ShardedJedis jedis=null;
            Long result=null;
            try{
                jedis=RedisShardPool.getJedis();
                result=jedis.del(key);
            }catch (Exception e){
                logger.error("del key:{} value:{} error",key,e);
                RedisShardPool.returnBrokenResource(jedis);
                return result;
            }
            RedisShardPool.returnResource(jedis);
            return result;
        }

//    public static void main(String[] args) {
//        Jedis jedis=RedisShardPool.getJedis();
//        RedisShardPoolUtil.set("keyTest","value");
//    }



    }


