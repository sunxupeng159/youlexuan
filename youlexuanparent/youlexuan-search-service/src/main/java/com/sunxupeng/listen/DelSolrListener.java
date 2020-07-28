package com.sunxupeng.listen;

import com.sunxupeng.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;


@Component
public class DelSolrListener implements MessageListener {

    @Autowired
    private ItemSearchService itemSearchService;

    @Override
    public void onMessage(Message message) {
        try {
            ObjectMessage obj = (ObjectMessage)message;

            Long[] ids = (Long[])obj.getObject();

            itemSearchService.deleteList(ids);

            System.out.println(">>>>>receive del solr msg");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
