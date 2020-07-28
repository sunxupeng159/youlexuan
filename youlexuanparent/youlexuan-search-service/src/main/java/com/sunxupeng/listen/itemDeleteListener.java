package com.sunxupeng.listen;

import com.sunxupeng.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.Arrays;

@Component
public class itemDeleteListener implements MessageListener {
    @Autowired
    private ItemSearchService itemSearchService;
    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage objectMessage = (ObjectMessage) message;
            Long[] goodsIds = (Long[]) objectMessage.getObject();

            System.out.println("ItemDeleteListener接收消息：" + goodsIds);

            itemSearchService.deleteList( goodsIds);

            System.out.println("成功删除索引库中的记录");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    }

