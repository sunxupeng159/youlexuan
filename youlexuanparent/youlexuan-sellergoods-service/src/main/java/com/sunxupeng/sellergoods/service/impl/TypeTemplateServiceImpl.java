package com.sunxupeng.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.mapper.TbSpecificationOptionMapper;
import com.sunxupeng.pojo.TbSpecificationOption;
import com.sunxupeng.pojo.TbSpecificationOptionExample;
import com.sunxupeng.pojo.TbTypeTemplateExample;
import com.sunxupeng.pojo.TbTypeTemplateExample.Criteria;
import com.sunxupeng.mapper.TbTypeTemplateMapper;
import com.sunxupeng.pojo.TbTypeTemplate;
import com.sunxupeng.sellergoods.service.TypeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;

/**
 * type_template服务实现层
 * @author senqi
 *
 */
@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TbSpecificationOptionMapper specificationOptionMapper;
	@Autowired
	private TbTypeTemplateMapper typeTemplateMapper;
	@Autowired
	private RedisTemplate redisTemplate;
	/**
	 * 查询全部
	 */
	@Override
	public List<TbTypeTemplate> findAll() {
		return typeTemplateMapper.selectByExample(null);
	}

	/**
	 * 分页
	 */
	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);		
		Page<TbTypeTemplate> page = (Page<TbTypeTemplate>) typeTemplateMapper.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/**
	 * 增加
	 */
	@Override
	public void add(TbTypeTemplate typeTemplate) {
		typeTemplateMapper.insert(typeTemplate);		
	}

	
	/**
	 * 修改
	 */
	@Override
	public void update(TbTypeTemplate typeTemplate){
		typeTemplateMapper.updateByPrimaryKey(typeTemplate);
	}	
	
	/**
	 * 根据ID获取实体
	 * @param id
	 * @return
	 */
	@Override
	public TbTypeTemplate findOne(Long id){
		return typeTemplateMapper.selectByPrimaryKey(id);
	}

	/**
	 * 批量删除
	 */
	@Override
	public void delete(Long[] ids) {
		for(Long id:ids){
			typeTemplateMapper.deleteByPrimaryKey(id);
		}		
	}
	
	/**
	 * 分页+查询
	 * @return
	 */
	@Override
	public PageResult findPage(TbTypeTemplate typeTemplate, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		TbTypeTemplateExample example=new TbTypeTemplateExample();
		Criteria criteria = example.createCriteria();
		
		if(typeTemplate != null){			
						if(typeTemplate.getName() != null && typeTemplate.getName().length() > 0){
				criteria.andNameLike("%" + typeTemplate.getName() + "%");
			}			if(typeTemplate.getSpecIds() != null && typeTemplate.getSpecIds().length() > 0){
				criteria.andSpecIdsLike("%" + typeTemplate.getSpecIds() + "%");
			}			if(typeTemplate.getBrandIds() != null && typeTemplate.getBrandIds().length() > 0){
				criteria.andBrandIdsLike("%" + typeTemplate.getBrandIds() + "%");
			}			if(typeTemplate.getCustomAttributeItems() != null && typeTemplate.getCustomAttributeItems().length() > 0){
				criteria.andCustomAttributeItemsLike("%" + typeTemplate.getCustomAttributeItems() + "%");
			}
		}
		
		Page<TbTypeTemplate> page= (Page<TbTypeTemplate>)typeTemplateMapper.selectByExample(example);

		saveToRedis();//存入数据到缓存
		return new PageResult(page.getTotal(), page.getResult());
	}

    @Override
    public List<Map> selectOptionList() {

		return typeTemplateMapper.selectOptionList();
    }

    @Override
    public List<Map> findSpecList(Long id) {
		TbTypeTemplate typeTemplate = typeTemplateMapper.selectByPrimaryKey(id);

		List<Map> list = JSON.parseArray(typeTemplate.getSpecIds(), Map.class)  ;
		for(Map map:list){
			//查询规格选项列表
			TbSpecificationOptionExample example=new TbSpecificationOptionExample();
			com.sunxupeng.pojo.TbSpecificationOptionExample.Criteria criteria = example.createCriteria();
			int specId=(Integer) map.get("id");
			long Id=specId;
			criteria.andSpecIdEqualTo( Id );
			List<TbSpecificationOption> options = specificationOptionMapper.selectByExample(example);
			map.put("options", options);
		}
		return list;
    }

	/**
	 * 将数据存入缓存
	 */
	private void saveToRedis(){
		//获取模板数据
		List<TbTypeTemplate> typeTemplateList = findAll();
		//循环模板
		for(TbTypeTemplate typeTemplate :typeTemplateList){
			//存储品牌列表
			List<Map> brandList = JSON.parseArray(typeTemplate.getBrandIds(), Map.class);
			redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);

			//存储规格列表，因为模板表中的规格缺少规格选项，因此需要使用之前写过的一个方法：由模板id得到 规格+规格选项列表
			List<Map> specList = findSpecList(typeTemplate.getId());
			redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
		}
		System.out.println("=====缓存：品牌数据、规格数据=====");
	}

}
