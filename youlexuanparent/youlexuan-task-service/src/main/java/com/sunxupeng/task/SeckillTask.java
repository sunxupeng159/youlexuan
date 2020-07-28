package com.sunxupeng.task;

import com.sunxupeng.mapper.TbSeckillGoodsMapper;
import com.sunxupeng.pojo.TbSeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class SeckillTask {
@Autowired
private RedisTemplate redisTemplate;

@Autowired
private TbSeckillGoodsMapper seckillGoodsMapper;
    @Scheduled(cron = "* * * * * ?")
    public void refreshSeckillGoods() {
        System.out.println("执行任务调度：" + new Date());
    }
    @Scheduled(cron = "* * * * * *")
    public void removeSeckillGoods() {
        System.out.println("缓存的秒杀商品列表监控中...");
        // 扫描缓存中秒杀商品列表，发现过期的移除
        List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        for (TbSeckillGoods seckill : seckillGoodsList) {
            // 如果结束日期小于当前日期，则表示过期
            if (seckill.getEndTime().getTime() < new Date().getTime()) {
                seckillGoodsMapper.updateByPrimaryKey(seckill);// 向数据库保存记录
                redisTemplate.boundHashOps("seckillGoods").delete(seckill.getId());// 移除缓存数据
                System.out.println("移除过期秒杀商品：" + seckill.getId());
            }
        }
    }

}