package com.sunxupeng.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.search.service.ItemSearchService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private SolrTemplate solrTemplate;
@Autowired
private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> search(Map searchMap) {

        String keywords = (String) searchMap.get("keywords");
        searchMap.put("keywords", keywords.replace(" ", ""));
        Map<String, Object> map = new HashMap();
        hiSearch(searchMap, map);

        // 根据关键字进行分类查询
        categoryListSearch(searchMap, map);
// 3.查询品牌和规格列表
        String categoryName = (String) searchMap.get("category");

        // 如果有分类名称
        if (!"".equals(categoryName)) {
            brandAndSpecSearch(categoryName, map);
        }
        // 如果没有分类名称，按照第一个查询
        else {
            List<String> categoryList = (List<String>) map.get("categoryList");
            if (categoryList.size() > 0) {
                // 获取第一个分类名称对应的品牌、规格
                brandAndSpecSearch(categoryList.get(0), map);
            }
        }

        return map;
    }

    @Override
    public void importList(List<TbItem> itemList) {
        for (TbItem item : itemList) {
            Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
            Map<String, String> newMap = new HashMap<>();
            for(String key : map.keySet()) {
                // 处理中文的键
                newMap.put(Pinyin.toPinyin(key, "").toLowerCase(), map.get(key));
            }
            item.setSpecMap(newMap);
        }
        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();
    }

    @Override
    public void deleteList(Long[] ids) {
        Query query=new SimpleQuery();
        Criteria criteria = new Criteria("item_goodsid");
        criteria.in(ids);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     *  根据分类名获取对应的品牌、规格列表
     * @param catName
     * @param map
     */
    private void brandAndSpecSearch(String catName, Map<String, Object> map) {

        Long typeId = (Long) redisTemplate.boundHashOps("itemCatList").get(catName);

        // 从redis缓存中获取品牌、规格列表
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

        map.put("brandList", brandList);
        map.put("specList", specList);
    }


    /**
     * 查询分类列表
     *
     * @param searchMap
     * @return
     */
    private void categoryListSearch(Map searchMap, Map<String, Object> map) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();

        // 按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);

        // 设置分组选项
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);

        // 得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        // 根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        // 得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        // 得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();

        for (GroupEntry<TbItem> entry : content) {
            list.add(entry.getGroupValue());
        }

        map.put("categoryList", list);
    }
    private void hiSearch(Map searchMap, Map<String, Object> map) {
        HighlightQuery query=new SimpleHighlightQuery();
        HighlightOptions highlightOptions=new HighlightOptions().addField("item_title");//设置高亮的域
        highlightOptions.setSimplePrefix("<em style='color:red'>");//高亮前缀
        highlightOptions.setSimplePostfix("</em>");//高亮后缀
        query.setHighlightOptions(highlightOptions);//设置高亮选项
        // 关键字查询


        Criteria c = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(c);
        //按分类筛选
        if(!"".equals(searchMap.get("category"))){
            Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if(!"".equals(searchMap.get("brand"))){
            Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        // 1.4过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + Pinyin.toPinyin(key, "").toLowerCase()).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        if (!"".equals(searchMap.get("price"))){
            String[] price =((String) searchMap.get("price")).split("-");
            if(!price[0].equals("0")){
                Criteria minCriteria=new Criteria("item_price").greaterThanEqual(price[0]);
                FilterQuery filterQuery=new SimpleFilterQuery(minCriteria);
                query.addFilterQuery(filterQuery);
            }
            if(!price[1].equals("*")){//如果区间终点不等于*
                Criteria maxCriteria=new  Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery=new SimpleFilterQuery(maxCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //分页查询
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if(pageNo==null){
            pageNo=1;
        }
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if(pageSize==null){
            pageSize=20;
        }
        query.setOffset((pageNo-1)*pageSize);
        query.setRows(pageSize);
        //按照价格排序
        String sortValue = (String) searchMap.get("sort");
        String sortField = (String) searchMap.get("sortField");
        if(sortValue!=null&&!sortValue.equals("")){
            if(sortValue.equals("ASC")){
                Sort sort = new Sort(Sort.Direction.ASC, sortField);
                query.addSort(sort);
            }
            if(sortValue.equals("DESC")){
                Sort sort = new Sort(Sort.Direction.DESC, sortField);
                query.addSort(sort);
            }
        }
        //高亮显示
        HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query, TbItem.class);
        for(HighlightEntry<TbItem> h: page.getHighlighted()){//循环高亮入口集合
            TbItem item = h.getEntity();//获取原实体类
            if(h.getHighlights().size() > 0 && h.getHighlights().get(0).getSnipplets().size()>0){
                item.setTitle(h.getHighlights().get(0).getSnipplets().get(0));//设置高亮的结果
            }
        }

        map.put("totalPages", page.getTotalPages());//返回总页数
        map.put("total", page.getTotalElements());//返回总记录数
        map.put("rows", page.getContent());
    }
}

