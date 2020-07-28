package com.sunxupeng.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.sunxupeng.entity.PageResult;
import com.sunxupeng.group.Goods;
import com.sunxupeng.mapper.*;
import com.sunxupeng.pojo.*;
import com.sunxupeng.pojo.TbGoodsExample.Criteria;
import com.sunxupeng.sellergoods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;


import java.util.*;

/**
 * goods服务实现层
 * @author senqi
 *
 */
@Service
public class GoodsServiceImpl implements GoodsService {

	@Autowired
	private TbGoodsMapper goodsMapper;
	@Autowired
	private TbGoodsDescMapper goodsDescMapper;

	@Autowired
	private TbItemMapper itemMapper;
	@Autowired
	private TbBrandMapper brandMapper;
	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private TbSellerMapper sellerMapper;

	/**
	 * 查询全部
	 */
	@Override
	public List<TbGoods> findAll() {
		return goodsMapper.selectByExample(null);
	}

	/**
	 * 分页
	 *
	 * @return
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}


	/**
	 * 修改
	 */
	@Override
	public void update(Goods goods) {
		goods.getGoods().setAuditStatus("0");//设置未审核状态:经过修改的商品，需要重新设置状态
		goodsMapper.updateByPrimaryKey(goods.getGoods());//保存商品表
		goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());//保存商品扩展表
		//先：删除原有的sku列表数据
		TbItemExample example = new TbItemExample();
		com.sunxupeng.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(goods.getGoods().getId());
		itemMapper.deleteByExample(example);
		//再：添加新的sku列表数据（和add方法的添加sku相同，因此抽取一个方法）
		saveItem(goods);//插入商品SKU列表数据

	}

	/**
	 * 根据ID获取实体
	 *
	 * @param id
	 * @return
	 */
	@Override
	public Goods findOne(Long id) {
		Goods goods = new Goods();
		TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
		goods.setGoods(tbGoods);
		TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
		goods.setGoodsDesc(tbGoodsDesc);

		//查询SKU商品列表
		TbItemExample example = new TbItemExample();
		com.sunxupeng.pojo.TbItemExample.Criteria criteria = example.createCriteria();
		criteria.andGoodsIdEqualTo(id);//查询条件：商品ID
		List<TbItem> itemList = itemMapper.selectByExample(example);
		goods.setItemList(itemList);
		return goods;

	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			// 这次。我们要重新定义一下 删除
			// 使用逻辑删除，对商品进行屏蔽

			TbGoods goods = goodsMapper.selectByPrimaryKey(id);
			// 1表示删除
			goods.setIsDelete("1");
			goodsMapper.updateByPrimaryKey(goods);

			// 将对应的sku的状态改为 3
			TbItemExample ex = new TbItemExample();
			TbItemExample.Criteria c = ex.createCriteria();
			c.andGoodsIdEqualTo(id);
			List<TbItem> itemList = itemMapper.selectByExample(ex);

			for (TbItem item : itemList) {
				// 3表示删除
				item.setStatus("3");
				itemMapper.updateByPrimaryKey(item);
			}
		}
	}

	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbGoodsExample example = new TbGoodsExample();
		Criteria criteria = example.createCriteria();
		criteria.andIsDeleteIsNull();//非删除状态

		if (goods != null) {
			if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
				criteria.andSellerIdEqualTo(goods.getSellerId());
			}
			if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
				criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
			}
			if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
				criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
			}
			if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
				criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
			}
			if (goods.getCaption() != null && goods.getCaption().length() > 0) {
				criteria.andCaptionLike("%" + goods.getCaption() + "%");
			}
			if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
				criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
			}
			if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
				criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
			}
			if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
				criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
			}
		}

		Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public void updateStatus(Long[] ids, String status) {

			for(Long id:ids){
				TbGoods goods = goodsMapper.selectByPrimaryKey(id);
				goods.setAuditStatus(status);
				goodsMapper.updateByPrimaryKey(goods);
			}
		}

	@Override
	public List<TbItem> findItemListByGoodsId(Long[] ids, String status) {
		TbItemExample ex = new TbItemExample();
		TbItemExample.Criteria c = ex.createCriteria();
		c.andGoodsIdIn(Arrays.asList(ids));
		c.andStatusEqualTo(status);
		return itemMapper.selectByExample(ex);
	}


	@Override
	public void add(Goods goods) {

		goods.getGoods().setAuditStatus("0");//设置未申请状态
		goodsMapper.insert(goods.getGoods());
		goods.getGoodsDesc().setGoodsId(goods.getGoods().getId());//设置ID
		goodsDescMapper.insert(goods.getGoodsDesc());//插入商品扩展数据
		saveItem(goods);//插入商品SKU列表数据

    }
	private void saveItem(Goods goods) {
		// 启用规格
		if (goods.getGoods().getIsEnableSpec().equals("1")) {
			// 插入sku列表
			for (TbItem item : goods.getItemList()) {
				// title
				Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
				String title = goods.getGoods().getGoodsName();
				for (String key : map.keySet()) {
					title += " " + map.get(key);
				}
				item.setTitle(title);

				setItem(goods, item);

				itemMapper.insert(item);
			}
		}
		// 不启用，就按照spu的信息插入1条sku
		else {
			TbItem item = new TbItem();

			//价格
			item.setPrice(goods.getGoods().getPrice());
			//库存数量
			item.setNum(999);
			//状态
			item.setStatus("1");
			//是否默认
			item.setIsDefault("1");
			item.setSpec("{}");

			// spu的名字
			item.setTitle(goods.getGoods().getGoodsName());

			// alt + shift + M：抽取方法
			setItem(goods, item);

			itemMapper.insert(item);
		}
	}
	private void setItem(Goods goods, TbItem item) {
		item.setGoodsId(goods.getGoods().getId());
		item.setSellerId(goods.getGoods().getSellerId());
		item.setCategoryid(goods.getGoods().getCategory3Id());
		item.setCreateTime(new Date());
		item.setUpdateTime(new Date());
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId());
		String brandName = tbBrand.getName();
		item.setBrand(brandName);
		TbItemCat tbItemCat = itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id());
		item.setCategory(tbItemCat.getName());
		TbSeller tbSeller = sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId());
		item.setSeller(tbSeller.getNickName());
		List<Map> maps = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
		if(maps.size()>0){
			item.setImage((String) maps.get(0).get("url"));

		}
	}

}
