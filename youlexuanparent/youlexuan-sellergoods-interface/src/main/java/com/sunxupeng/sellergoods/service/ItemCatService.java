package com.sunxupeng.sellergoods.service;

import com.sunxupeng.entity.PageResult;
import com.sunxupeng.pojo.TbItemCat;

import java.util.List;

/**
 * item_cat服务层接口
 * @author sunxupeng
 *
 */
public interface ItemCatService {

	/**
	 * 返回全部列表
	 * @return
	 */
	public List<TbItemCat> findAll();
	
	
	/**
	 * 返回分页列表
	 * @return
	 */
	public PageResult findPage(int pageNum, int pageSize);


	/**
	 * 增加
	*/
	public void add(TbItemCat itemCat);


	/**
	 * 修改
	 */
	public void update(TbItemCat itemCat);


	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	public TbItemCat findOne(Long id);


	/**
	 * 批量删除
	 * @param ids
	 */
	public String delete(Long[] ids);

	/**
	 * 分页
	 * @param pageNum 当前页 码
	 * @param pageSize 每页记录数
	 * @return
	 */
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize);


	/**
	 * 根据上级ID返回列表
	 * @return
	 */
	public List<TbItemCat> findByParentId(Long parentId);
}
