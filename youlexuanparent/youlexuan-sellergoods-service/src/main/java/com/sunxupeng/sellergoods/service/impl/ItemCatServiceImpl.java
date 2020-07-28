package com.sunxupeng.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.pojo.TbItemCatExample;
import com.sunxupeng.pojo.TbItemCatExample.Criteria;
import com.sunxupeng.mapper.TbItemCatMapper;
import com.sunxupeng.pojo.TbItemCat;
import com.sunxupeng.sellergoods.service.ItemCatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * item_cat服务实现层
 * @author senqi
 *
 */
@Service(timeout = 3000)
public class ItemCatServiceImpl implements ItemCatService {

	@Autowired
	private TbItemCatMapper itemCatMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbItemCat> findAll() {
		return itemCatMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<TbItemCat> page = (Page<TbItemCat>) itemCatMapper.selectByExample(null);

		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbItemCat itemCat) {
		itemCatMapper.insert(itemCat);
	}


	/**
	 * 修改
	 */
	@Override
	public void update(TbItemCat itemCat){
		itemCatMapper.updateByPrimaryKey(itemCat);
	}

	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbItemCat findOne(Long id){
		return itemCatMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public String delete(Long[] ids) {
		StringBuilder sb=new StringBuilder();
		for (Long id : ids) {
			List<TbItemCat> result = findByParentId(id);
			if(result != null && result.size() > 0) {
				sb.append(id+",");
			} else {
				itemCatMapper.deleteByPrimaryKey(id);
			}
		}
	return sb.toString();
	}

	/**
	 * 分页+查询
	 */
	@Override
	public PageResult findPage(TbItemCat itemCat, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);

		TbItemCatExample example=new TbItemCatExample();
		Criteria criteria = example.createCriteria();

		if(itemCat != null){
						if(itemCat.getName() != null && itemCat.getName().length() > 0){
				criteria.andNameLike("%" + itemCat.getName() + "%");
			}
		}

		Page<TbItemCat> page= (Page<TbItemCat>)itemCatMapper.selectByExample(example);
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<TbItemCat> findByParentId(Long parentId) {

        TbItemCatExample ex = new TbItemCatExample();
        Criteria c = ex.createCriteria();
        c.andParentIdEqualTo(parentId);

        // 将分类表的数据添加到缓存
        // 目的：为了在搜索的时候，可以根据 分类名 快速 获得 typeId

        List<TbItemCat> itemCatList = itemCatMapper.selectByExample(null);
        for (TbItemCat itemCat : itemCatList) {
            // 以分类名 作为小键， 以模板id作为值
            redisTemplate.boundHashOps("itemCatList").put(itemCat.getName(), itemCat.getTypeId());
        }

        return itemCatMapper.selectByExample(ex);
    }


}
