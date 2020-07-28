package com.sunxupeng.listen;

import com.alibaba.fastjson.JSON;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.util.List;

@Component
public class ImportSolrListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            TextMessage textMessage=(TextMessage)message;
            String text = textMessage.getText();
            List<TbItem> list = JSON.parseArray(text, TbItem.class);
            itemSearchService.importList(list);
            System.out.println("成功导入到索引库");
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
