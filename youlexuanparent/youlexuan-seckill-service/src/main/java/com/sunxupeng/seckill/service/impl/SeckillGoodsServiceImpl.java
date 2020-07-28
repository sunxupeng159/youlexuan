package com.sunxupeng.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.pojo.TbSeckillGoodsExample;
import com.sunxupeng.pojo.TbSeckillGoodsExample.Criteria;
import com.sunxupeng.mapper.TbSeckillGoodsMapper;
import com.sunxupeng.pojo.TbSeckillGoods;
import com.sunxupeng.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;
import java.util.List;

/**
 * seckill_goods服务实现层
 * @author senqi
 *
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

	@Autowired
	private TbSeckillGoodsMapper seckillGoodsMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbSeckillGoods> findAll() {
		return seckillGoodsMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbSeckillGoods> page = (Page<TbSeckillGoods>) seckillGoodsMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbSeckillGoods seckillGoods) {
		seckillGoodsMapper.insert(seckillGoods);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbSeckillGoods seckillGoods){
		seckillGoodsMapper.updateByPrimaryKey(seckillGoods);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbSeckillGoods findOne(Long id){
		return seckillGoodsMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			seckillGoodsMapper.deleteByPrimaryKey(id);
		}		
	}
	
	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbSeckillGoods seckillGoods, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbSeckillGoodsExample example=new TbSeckillGoodsExample();
		Criteria criteria = example.createCriteria();
		
		if(seckillGoods != null){			
						if(seckillGoods.getTitle() != null && seckillGoods.getTitle().length() > 0){
				criteria.andTitleLike("%" + seckillGoods.getTitle() + "%");
			}			if(seckillGoods.getSmallPic() != null && seckillGoods.getSmallPic().length() > 0){
				criteria.andSmallPicLike("%" + seckillGoods.getSmallPic() + "%");
			}			if(seckillGoods.getSellerId() != null && seckillGoods.getSellerId().length() > 0){
				criteria.andSellerIdLike("%" + seckillGoods.getSellerId() + "%");
			}			if(seckillGoods.getStatus() != null && seckillGoods.getStatus().length() > 0){
				criteria.andStatusLike("%" + seckillGoods.getStatus() + "%");
			}			if(seckillGoods.getIntroduction() != null && seckillGoods.getIntroduction().length() > 0){
				criteria.andIntroductionLike("%" + seckillGoods.getIntroduction() + "%");
			}
		}
		
		Page<TbSeckillGoods> page= (Page<TbSeckillGoods>)seckillGoodsMapper.selectByExample(example);		
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public List<TbSeckillGoods> findList() {
		//获取秒杀商品列表
		List<TbSeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
		if(seckillGoodsList==null || seckillGoodsList.size()==0){
		TbSeckillGoodsExample example =new TbSeckillGoodsExample();
		Criteria criteria=example.createCriteria();
		criteria.andStatusEqualTo("1");
		criteria.andStockCountGreaterThan(0);//剩余库存大于0
		criteria.andStartTimeLessThan(new Date());
		criteria.andEndTimeGreaterThan(new Date());
			seckillGoodsList= seckillGoodsMapper.selectByExample(example);
			//将商品列表装入缓存
			System.out.println("将秒杀商品加入缓存>>>>>");
			for(TbSeckillGoods seckillGoods:seckillGoodsList){
				redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
			}
		}
		return seckillGoodsList;
	}

	@Override
	public TbSeckillGoods findOneFromRedis(Long id) {
		return  (TbSeckillGoods)redisTemplate.boundHashOps("seckillGoods").get(id);
	}

}
