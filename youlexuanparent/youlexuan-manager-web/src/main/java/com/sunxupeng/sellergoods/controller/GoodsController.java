package com.sunxupeng.sellergoods.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.entity.Result;
import com.sunxupeng.group.Goods;

import com.sunxupeng.pojo.TbGoods;
import com.sunxupeng.pojo.TbItem;
import com.sunxupeng.sellergoods.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.List;


/**
 * goodscontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

	@Reference
	private GoodsService goodsService;
	@Autowired
	private JmsTemplate jmsTemplate;

	@Autowired
	@Qualifier("importSolrQueue")
	private ActiveMQQueue importSolrQueue;

	@Autowired
	@Qualifier("delSolrQueue")
	private ActiveMQQueue delSolrQueue;

	@Autowired
	private ActiveMQTopic topicPageDestination;
	/**
	 * 返回全部列表
	 * @return
	 */




	@RequestMapping("/findAll")
	public List<TbGoods> findAll(){
		return goodsService.findAll();
	}
	
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){			
		return goodsService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param goods
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody Goods goods){
		try {

			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){
		try {
			goodsService.update(goods);
			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}	
	
	/**
	 * 获取实体
	 * @param id
	 * @return
	 */
	@RequestMapping("/findOne")
	public Goods findOne(Long id){
		return goodsService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页

	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int size){
		return goodsService.findPage(goods, page, size);		
	}


	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){
		try {
			// 审核通过
			if("1".equals(status)) {
				List<TbItem> itemList = goodsService.findItemListByGoodsId(ids, status);
				jmsTemplate.send(topicPageDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
				if(itemList.size() > 0) {

					jmsTemplate.send(importSolrQueue, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							String s = JSON.toJSONString(itemList);
							return session.createTextMessage(s);
						}
					});

				}
			} else {
				// 将solr库中对应的商品 删除
				//itemSearchService.deleteList(ids);
				jmsTemplate.send(delSolrQueue, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(ids);
					}
				});
			}

			goodsService.updateStatus(ids, status);
			//静态页生成

			return new Result(true, "修改成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "修改失败");
		}
	}
}
