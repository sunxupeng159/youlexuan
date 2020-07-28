package com.sunxupeng.sellergoods.service;

import com.sunxupeng.entity.PageResult;
import com.sunxupeng.group.Goods;
import com.sunxupeng.pojo.TbGoods;
import com.sunxupeng.pojo.TbItem;

import java.util.List;

/**
 * goods服务层接口
 * @author sunxupeng
 *
 */
public interface GoodsService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbGoods> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(Goods goods);


	/**
	 * 修改
	 */
	public void update(Goods goods);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public Goods findOne(Long id);


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
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize);

	 //增加，传递组合参数

	//批量修改状态
	 public void updateStatus(Long []ids, String status);

	 public List<TbItem> findItemListByGoodsId(Long[]ids,String status);


}
