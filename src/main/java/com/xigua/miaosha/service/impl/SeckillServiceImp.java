package com.xigua.miaosha.service.impl;

import com.xigua.miaosha.common.aop.Servicelock;
import com.xigua.miaosha.constant.SeckillStatusConstant;
import com.xigua.miaosha.dao.SeckillDOMapper;
import com.xigua.miaosha.dao.SeckillSuccessDOMapper;
import com.xigua.miaosha.dao.ext.ExtSeckillMapper;
import com.xigua.miaosha.dataobject.SeckillDO;
import com.xigua.miaosha.dataobject.SeckillSuccessDO;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.service.SeckillService;
import com.xigua.miaosha.service.model.SeckillModel;
import net.bytebuddy.implementation.bytecode.Throw;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillServiceImp implements SeckillService {

    /**
     * 思考：用synchronized 和 lock 有什么区别
     *
     * service 默认是单例的，并发下lock只有一个实例
     */
    private Lock lock = new ReentrantLock(true);//互斥锁 参数默认false，不公平锁

    @Autowired
    private SeckillDOMapper seckillDOMapper;

    @Autowired
    private ExtSeckillMapper extSeckillMapper;

    @Autowired
    private SeckillSuccessDOMapper seckillSuccessDOMapper;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    private RedissonClient redissonClient;

    @Override
    public List<SeckillModel> getSeckillList() {

        return null;
    }

    @Override
    public void deleteSeckillSuccess(long seckillId) {
        seckillSuccessDOMapper.deleteBySeckillId(seckillId);
    }

    @Override
    public void updateByPrimaryKeySelective(SeckillModel seckillModel) {
        seckillDOMapper.updateByPrimaryKeySelective(convertSeckillModel(seckillModel));
    }

    private SeckillDO convertSeckillModel(SeckillModel seckillModel){
        if (seckillModel == null){
            return null;
        }
        SeckillDO seckillDO = new SeckillDO();
        BeanUtils.copyProperties(seckillModel,seckillDO);
        return seckillDO;
    }

    private void dealSeckill(long seckillId, String userPhone, String note) {
        /// 获取秒杀库存数据
        SeckillDO seckillDO = seckillDOMapper.selectByPrimaryKey(seckillId);
        /// 库存数量大于0才可以进行秒杀
        if (seckillDO.getNumber() > 0) {
            /// 扣库存
            extSeckillMapper.reduceNumber(seckillId,new Date());

            /// 创建订单
            SeckillSuccessDO seckillSuccessDO = new SeckillSuccessDO();
            seckillSuccessDO.setCreateTime(new Date());
            seckillSuccessDO.setStatus(SeckillStatusConstant.IN_PROGRESS);
            seckillSuccessDO.setSeckillId(seckillId);
            seckillSuccessDO.setUserPhone(userPhone);
//            try {
//                InetAddress localHost = InetAddress.getLocalHost();
//                seckillSuccessDO.
//            } catch (UnknownHostException e) {
//                System.out.println("请求被未知IP处理");
//                e.printStackTrace();
//            }
            seckillSuccessDOMapper.insertSelective(seckillSuccessDO);

            /// 调用支付

        } else  {
            /// mq 通知没有库存
//            throw new BusinessException(EmBussinessError.SECKILL_UNDERSTOCK);
            System.out.println("库存不够了  ，别买了  " + userPhone);
        }
    }

    @Transactional
    @Override
    public void executeWithSynchronized(long seckillId, int requestCount) throws BusinessException{
        CountDownLatch latch = new CountDownLatch(requestCount);
        for (int i = 1 ; i <= requestCount; i++) {
            int userId = i;
//            这种方式会出现超卖现象，，，我的猜测是  runnable new出了多个task对象，这个时候的同步锁就不起作用了。
//            Runnable task = new Runnable() {
//                @Override
//                public void run() {
//                    synchronized (this) {
//                        dealSeckill(seckillId,String.valueOf(userId),"秒杀场景一(sychronized同步锁实现)");
//                        latch.countDown();
//                    }
//                }
//            };
//            taskExecutor.execute(task);
            taskExecutor.execute(() -> {
                synchronized (this) {
                    dealSeckill(seckillId,String.valueOf(userId),"秒杀场景一(sychronized同步锁实现)");
                    latch.countDown();
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    @Override
    @Transactional
    public void executeWithLock(long seckillId, int requestCount) {
        for (int i = 1; i <= requestCount; i++) {
            int userId = i;
            taskExecutor.execute(() -> {
                try {
                    lock.lock();
                    dealSeckill(seckillId,String.valueOf(userId),"秒杀场景二(同步锁实现)");
                } catch (Exception e){
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        }
    }

    @Override
    public void executeWithRedisson(long seckillId, int userPhone) {
        RLock lock = redissonClient.getLock(seckillId + "");
        lock.lock();
        try {
            dealSeckill(seckillId,String.valueOf(userPhone),"秒杀场景三 （Redis锁实现）");
        } finally {
            lock.unlock();
        }
    }

    @Servicelock
    @Override
    public void executeWithAopLock(long seckillId, int userPhone) {
        dealSeckill(seckillId,String.valueOf(userPhone),"秒杀场景四（AOP同步锁）");
    }
}
