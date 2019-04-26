package com.xigua.miaosha.service;


import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.service.model.SeckillModel;

import java.util.List;

public interface SeckillService {

    /**
     * 获取秒杀项目列表
     * @return
     */
    List<SeckillModel> getSeckillList();


    /**
     * 根据id删除秒杀记录
     * @param seckillId
     */
    void deleteSeckillSuccess(long seckillId);

    /**
     * 更新秒杀库存数据
     *
     * @param seckillModel
     */
    void updateByPrimaryKeySelective(SeckillModel seckillModel);

    /**
     * 通过同步执行秒杀
     *
     * @param seckillId
     * @param requestCount
     */
    void executeWithSynchronized(long seckillId , int requestCount) throws BusinessException;


    void executeWithLock(long seckillId, int requestCount);

    /**
     * 通过Redis同步控制并发
     *
     * @param seckillId 秒杀库存id
     * @param userPhone 用户手机号
     */
    void executeWithRedisson(long seckillId, int userPhone);

    /**
     * 通过aop锁控制并发
     *
     * @param seckillId
     * @param userPhone
     */
    void executeWithAopLock(long seckillId ,int userPhone);
}
