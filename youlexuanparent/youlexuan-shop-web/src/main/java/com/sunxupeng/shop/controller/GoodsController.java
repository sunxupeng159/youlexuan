package com.sunxupeng.shop.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.entity.Result;
import com.sunxupeng.group.Goods;
import com.sunxupeng.pojo.TbGoods;

import com.sunxupeng.sellergoods.service.GoodsService;
import org.apache.activemq.command.ActiveMQQueue;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.security.core.context.SecurityContextHolder;
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
	private ActiveMQQueue queueSolrDeleteDestination;

	@Autowired
	private ActiveMQTopic topicPageDeleteDestination;
	@Autowired
	private JmsTemplate jmsTemplate;

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
			String s= SecurityContextHolder.getContext().getAuthentication().getName();
			goods.getGoods().setSellerId(s);
			goodsService.add(goods);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}


	/**
	 * 更新状态
	 * @param ids
	 * @param status
	 */
	@RequestMapping("/updateStatus")
	public Result updateStatus(Long[] ids, String status){
		try {
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			goodsService.updateStatus(ids, status);
			return new Result(true, "提交成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交失败");
		}
	}
	/**
	 * 修改
	 * @param goods
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody Goods goods){

			//校验是否是当前商家的id
			Goods goods2 = goodsService.findOne(goods.getGoods().getId());
			//获取当前登录的商家ID
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			//如果传递过来的商家ID并不是当前登录的用户的ID,则属于非法操作
			if(!goods2.getGoods().getSellerId().equals(sellerId) || !goods.getGoods().getSellerId().equals(sellerId) ){
				return new Result(false, "操作非法");
			}
			try {

				jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(new Long[] {goods.getGoods().getId()});
					}
				});
				jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						return session.createObjectMessage(new Long[] {goods.getGoods().getId()});
					}
				});
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
			jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
				@Override
				public javax.jms.Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});

			jmsTemplate.send(topicPageDeleteDestination, new MessageCreator() {
				@Override
				public Message createMessage(Session session) throws JMSException {
					return session.createObjectMessage(ids);
				}
			});
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param
	 * @param page
	 * @param
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbGoods goods, int page, int size){

		String id = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(id);
		return goodsService.findPage(goods, page, size);		
	}
	
}
