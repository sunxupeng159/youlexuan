package com.sunxupeng.solrutil;

import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.sunxupeng.mapper.TbItemMapper;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.pojo.TbItemExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:spring-*.xml")
public class SolrUtil {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private SolrTemplate solrTemplate;

    @Test
    public void importData() {
        // 先从tb_item表中查询需要导入的item列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        // 已审核
        criteria.andStatusEqualTo("1");
        List<TbItem> itemList = itemMapper.selectByExample(example);

        for (TbItem item : itemList) {
            Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);

            Map<String, String> newMap = new HashMap<>();

            for(String key : map.keySet()) {
                // 处理中文的键
                newMap.put(Pinyin.toPinyin(key, "").toLowerCase(), map.get(key));
            }

            item.setSpecMap(newMap);

            System.out.println(item.getTitle() + ">>>>" + item.getSpec());
        }

        solrTemplate.saveBeans(itemList);
        solrTemplate.commit();

        System.out.println("===结束===");
    }


    @Test
    public void delData() {
        Query query = new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    @Test
    public void testPinyin() {
        System.out.println(Pinyin.toPinyin("重复", "").toLowerCase());
    }


}
