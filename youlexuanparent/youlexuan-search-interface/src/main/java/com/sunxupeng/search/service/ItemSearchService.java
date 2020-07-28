package com.sunxupeng.search.service;

import com.sunxupeng.pojo.TbItem;

import java.util.List;
import java.util.Map;

public interface ItemSearchService {

    public Map<String,Object> search(Map searchMap);
    public void importList(List<TbItem> itemList);
    public void  deleteList(Long[] ids);

}
