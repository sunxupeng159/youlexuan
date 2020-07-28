package com.sunxupeng.seckill.service;

import com.sunxupeng.entity.PageResult;
import com.sunxupeng.pojo.TbSeckillOrder;

import java.util.List;

/**
 * seckill_order服务层接口
 * @author sunxupeng
 *
 */
public interface SeckillOrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbSeckillOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbSeckillOrder seckillOrder);


	/**
	 * 修改
	 */
	public void update(TbSeckillOrder seckillOrder);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbSeckillOrder findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbSeckillOrder seckillOrder, int pageNum, int pageSize);
	public void submitOrder(Long seckillId,String userId);
	public TbSeckillOrder searchOrderFromRedisByUserId(String userId);
	public void saveOrderFromRedisToDb(String userId,Long orderId,String transactionId);
	public void deleteOrderFromRedis(String userId,Long orderId);
}
