package com.sunxupeng.page.service.impl;


import com.alibaba.dubbo.config.annotation.Service;
import com.sunxupeng.mapper.TbGoodsDescMapper;
import com.sunxupeng.mapper.TbGoodsMapper;
import com.sunxupeng.mapper.TbItemCatMapper;
import com.sunxupeng.mapper.TbItemMapper;
import com.sunxupeng.page.service.ItemPageService;
import com.sunxupeng.pojo.TbGoods;
import com.sunxupeng.pojo.TbGoodsDesc;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.pojo.TbItemExample;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class ItemPageServiceImpl implements ItemPageService {



        @Value("${pagedir}")
        private String pagedir;

        @Autowired
        private FreeMarkerConfigurer freeMarkerConfigurer;

        @Autowired
        private TbGoodsMapper goodsMapper;

        @Autowired
        private TbGoodsDescMapper goodsDescMapper;

        @Autowired
        private TbItemCatMapper itemCatMapper;

        @Autowired
        private TbItemMapper itemMapper;

        // 传递过来一个商品id，就使用freemarker根据 模板 生成一个页面
        @Override
        public boolean genItemHtml(Long goodsId) {
            FileWriter out = null;
            try {
                Configuration conf = freeMarkerConfigurer.getConfiguration();
                Template template = conf.getTemplate("item.ftl");

                Map map = new HashMap();

                // goods的信息
                TbGoods goods = goodsMapper.selectByPrimaryKey(goodsId);
                // goods_desc的信息
                TbGoodsDesc goodsDesc = goodsDescMapper.selectByPrimaryKey(goodsId);

                // 3级分类的名称
                String cat1Name = itemCatMapper.selectByPrimaryKey(goods.getCategory1Id()).getName();
                String cat2Name = itemCatMapper.selectByPrimaryKey(goods.getCategory2Id()).getName();
                String cat3Name = itemCatMapper.selectByPrimaryKey(goods.getCategory3Id()).getName();

                // 对应的sku列表
                TbItemExample ex = new TbItemExample();
                TbItemExample.Criteria c = ex.createCriteria();
                c.andGoodsIdEqualTo(goodsId);
                // 1 表示正常状态
                c.andStatusEqualTo("1");
                // 按照是否默认列  降序排列：保证默认的sku在列表的 第一个
                ex.setOrderByClause("is_default desc");

                List<TbItem> itemList = itemMapper.selectByExample(ex);

                map.put("goods", goods);
                map.put("goodsDesc", goodsDesc);

                map.put("itemCat1", cat1Name);
                map.put("itemCat2", cat2Name);
                map.put("itemCat3", cat3Name);

                map.put("itemList", itemList);

                out = new FileWriter(pagedir + goodsId + ".html");
                template.process(map, out);

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                if(out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    @Override
    public boolean deleteItemHtml(Long[] goodsIds) {
        try {
            for(Long goodsId:goodsIds){
                new File(pagedir+goodsId+".html").delete();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}
