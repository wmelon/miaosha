package com.xigua.miaosha.dao.ext;

import com.xigua.miaosha.dao.SeckillDOMapper;

import java.util.Date;

public interface ExtSeckillMapper  extends SeckillDOMapper {

    /**
     * 普通秒杀扣库存方法
     *
     *
     * @param seckillId
     * @param killTime
     * @return
     */
    int reduceNumber(long seckillId, Date killTime);
}
