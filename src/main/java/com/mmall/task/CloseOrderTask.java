package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedissonManager;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.omg.PortableServer.THREAD_POLICY_ID;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2018/5/10.
 */
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    private IOrderService iOrderService;

    private RedissonManager redissonManager;
  //  @Scheduled(cron="0 */1 * * * ?")//每个一分钟的整数倍
    public void closeOrderTaskV1(){
        log.info("关闭订单任务开始");

        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        //iOrderService.closeOrder(hour);
        log.info("关闭订单任务结束");
    }

    //@Scheduled(cron="0 */1 * * * ?")//每个一分钟的整数倍
    public void closeOrderTaskV2(){
        log.info("关闭订单任务开始");
        long lockTimeout= Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50000"));
        Long setnxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult!=null&&setnxResult.intValue()==1){
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            //如果返回值是1，代表设置成功，获取锁
        }else{
            log.info("没有获取分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单任务结束");
    }

    public void closeOrderTaskV3(){
        log.info("关闭订单任务开始");
        long lockTimeout= Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50000"));
        Long setnxResult= RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult!=null&&setnxResult.intValue()==1){
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            //如果返回值是1，代表设置成功，获取锁
        }else{
           // log.info("没有获取分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            String lockTimeoutValue=RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if(lockTimeoutValue!=null&&System.currentTimeMillis()>Long.parseLong(lockTimeoutValue)){
                String getSetResult=RedisShardedPoolUtil.getset(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                if(getSetResult==null&&(getSetResult!=null&& StringUtils.equals(getSetResult,lockTimeoutValue))){
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("关闭订单任务结束");
                }
            }else{
                log.info("关闭订单任务结束");
            }
        }

        log.info("关闭订单任务结束");
    }

    public void closeOrderTaskV4(){
        RLock lock=redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        boolean getLock=false;
        try{
            if(getLock=lock.tryLock(2,5, TimeUnit.SECONDS)){
                log.info("Redisson获取分布式锁：{}，",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour"));
            }else{
                log.info("Redisson没有获取到分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }catch(InterruptedException e){
            log.error("Redisson 分布式锁获取异常",e);
        }finally{
            if(!getLock){
                return ;
            }
            lock.unlock();
        }
    }

    private void closeOrder(String lockName){
        RedisShardedPoolUtil.expire(lockName,50);
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        int hour=Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(lockName);
        log.info("释放{}，threadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread());
        log.info("=============");
    }


}
