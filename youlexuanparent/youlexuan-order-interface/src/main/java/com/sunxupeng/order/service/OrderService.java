package com.sunxupeng.order.service;

import com.sunxupeng.entity.PageResult;

import com.sunxupeng.pojo.TbOrder;
import com.sunxupeng.pojo.TbPayLog;

import java.util.List;

/**
 * order服务层接口
 * @author sunxupeng
 *
 */
public interface OrderService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbOrder> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbOrder order);


	/**
	 * 修改
	 */
	public void update(TbOrder order);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbOrder findOne(Long orderId);


	/**
	 * 批量删除
	 * @param ids
	 */
	public void delete(Long[] orderIds);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbOrder order, int pageNum, int pageSize);
	public TbPayLog searchPayLogFromRedis(String userId);
	public void updateOrderStatus(String out_trade_no,String transaction_id);
}
