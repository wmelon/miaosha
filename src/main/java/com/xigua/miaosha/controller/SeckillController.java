package com.xigua.miaosha.controller;


import com.xigua.miaosha.constant.SeckillStatusConstant;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.response.CommonReturnType;
import com.xigua.miaosha.service.SeckillService;
import com.xigua.miaosha.service.model.SeckillModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

@Controller("/seckill")
@RequestMapping("/seckill")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class SeckillController extends BaseController{

    @Autowired
    private SeckillService seckillService;

    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;

    /**
     * 通过sychronized同步锁实现同步锁控制秒杀并发 （秒杀未完成会阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共2000人参与秒杀，期望值为最后成功1000笔
     * 结果：多次运行，最终的结果都是1000
     * 总结：速度挺快，公平竞争资源，不会只有顺序请求用户抢占到资源
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景一 (sychronized同步锁实现）",nickname = "西瓜车")
    @ResponseBody
    @RequestMapping(value = "/doWithSychronized",method = {RequestMethod.GET})
    public CommonReturnType doWithSychronized(@RequestParam(name = "seckillId") long seckillId,
                                         @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                         @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum) throws BusinessException {

        prepareSeckill(seckillId,seckillNum);
        /// 开启秒杀
        seckillService.executeWithSynchronized(seckillId,requestNum);
        return CommonReturnType.create(null);
    }

    /**
     * 通过lock同步锁实现同步锁控制秒杀并发 （秒杀未完成会阻塞主线程）
     * 场景一：初始化当前库存为1000，通过线程池调度，模拟总共2000人参与秒杀，期望值为最后成功1000笔
     * 结果：多次运行，最终的结果都是1000
     * 总结：速度没有sychronized同步锁快，不公平竞争资源，前面强求的用户抢占资源
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景二 (程序锁实现）")
    @ResponseBody
    @RequestMapping(value = "/doWithLock",method = {RequestMethod.GET})
    public CommonReturnType doWithLock(@RequestParam(name = "seckillId") long seckillId,
                                       @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                       @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum) {

        /// 初始化数据
        prepareSeckill(seckillId,seckillNum);
        /// 开启秒杀
        seckillService.executeWithLock(seckillId,requestNum);
        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景三 (Redis分布式锁实现）")
    @ResponseBody
    @RequestMapping(value = "/doWithRedissionLock",method = {RequestMethod.GET})
    public CommonReturnType doWithRedissionLock(@RequestParam(name = "seckillId") long seckillId,
                                                @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){

        prepareSeckill(seckillId,seckillNum);
        for (int i = 1; i <= requestNum; i++) {
            int userId = i;
            taskExecutor.execute(() -> {
                seckillService.executeWithRedisson(seckillId, userId);
            });
        }
        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景四 (AOP程序锁）")
    @ResponseBody
    @RequestMapping(value = "/doWithAopLock",method = {RequestMethod.GET})
    public CommonReturnType doWithAopLock(@RequestParam(name = "seckillId") long seckillId,
                                          @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                          @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){

        prepareSeckill(seckillId,seckillNum);
        AtomicInteger atomicInteger = new AtomicInteger(0);
        for (int i = 1; i <= requestNum ; i++) {
            int userId = i;
            taskExecutor.execute(() -> {
                seckillService.executeWithAopLock(seckillId, userId);
            });
        }
        return CommonReturnType.create(null);
    }


    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景五 (activemq消息队列）")
    @ResponseBody
    @RequestMapping(value = "/doWithActiveMqMessage",method = {RequestMethod.GET})
    public CommonReturnType doWithActiveMqMessage (@RequestParam(name = "seckillId") long seckillId,
                                                   @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                   @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){


        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景六 (KafkaMq消息队列）")
    @ResponseBody
    @RequestMapping(value = "/doWithKafkaMqMessage",method = {RequestMethod.GET})
    public CommonReturnType doWithKafkaMqMessage(@RequestParam(name = "seckillId") long seckillId,
                                                 @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                 @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){


        return CommonReturnType.create(null);
    }

    @ApiOperation(value = "秒杀场景七 (数据库悲观锁一）")
    @ResponseBody
    @RequestMapping(value = "/doWithDBPessimisticLock_ONE",method = {RequestMethod.GET})
    public CommonReturnType doWithDBPessimisticLock_ONE(@RequestParam(name = "seckillId") long seckillId,
                                                    @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                    @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){


        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景八 (数据库悲观锁二）")
    @ResponseBody
    @RequestMapping(value = "/doWithDBPessimisticLock_TWO",method = {RequestMethod.GET})
    public CommonReturnType doWithDBPessimisticLock_TWO(@RequestParam(name = "seckillId") long seckillId,
                                                      @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                      @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){


        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景九 (数据库乐观锁）")
    @ResponseBody
    @RequestMapping(value = "/doWithDBOptimisticLock",method = {RequestMethod.GET})
    public CommonReturnType doWithDBOptimisticLock(@RequestParam(name = "seckillId") long seckillId,
                                                   @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                   @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){

        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景十 (进程内队列）")
    @ResponseBody
    @RequestMapping(value = "/doWithQueue",method = {RequestMethod.GET})
    public CommonReturnType doWithQueue(@RequestParam(name = "seckillId") long seckillId,
                                        @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                        @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){


        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景十一 (Disruptor队列）")
    @ResponseBody
    @RequestMapping(value = "/doWithDisruptorQueue",method = {RequestMethod.GET})
    public CommonReturnType doWithDisruptorQueue(@RequestParam(name = "seckillId") long seckillId,
                                                 @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                 @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){

        return CommonReturnType.create(null);
    }

    /**
     *
     * @param seckillId
     * @param seckillNum
     * @param requestNum
     * @return
     */
    @ApiOperation(value = "秒杀场景十二 (zookeeper Lock）")
    @ResponseBody
    @RequestMapping(value = "/doWithZookeeperLock",method = {RequestMethod.GET})
    public CommonReturnType doWithZookeeperLock(@RequestParam(name = "seckillId") long seckillId,
                                                @RequestParam(name = "seckillNum" ,required = false, defaultValue = "1000") int seckillNum,
                                                @RequestParam(name = "requestNum" ,required = false, defaultValue = "2000") int requestNum){

        return CommonReturnType.create(null);
    }


    /// 测试之前初始化数据库数据
    private void prepareSeckill(long seckillId,int seckillNum){
        SeckillModel seckillModel = new SeckillModel();
        seckillModel.setSeckillId(seckillId);
        seckillModel.setNumber(seckillNum);
        seckillModel.setStatus(SeckillStatusConstant.IN_PROGRESS);
        /// 更新秒杀库存数据
        seckillService.updateByPrimaryKeySelective(seckillModel);
        // 清理已成功秒杀记录
        seckillService.deleteSeckillSuccess(seckillId);
    }
}
