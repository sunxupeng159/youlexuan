package com.sunxupeng.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.entity.Result;
import com.sunxupeng.pojo.TbSeckillOrder;
import com.sunxupeng.seckill.service.SeckillOrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * seckill_ordercontroller
 * @author senqi
 *
 */
@RestController
@RequestMapping("/seckillOrder")
public class SeckillOrderController {

	@Reference
	private SeckillOrderService seckillOrderService;
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findAll")
	public List<TbSeckillOrder> findAll(){
		return seckillOrderService.findAll();
	}
	@RequestMapping("/submitOrderTest")
	public Result submitOrder(Long seckillId,String userId){
		if(userId==null) {
			userId = "senqi";
		}
		try {
			seckillOrderService.submitOrder(seckillId,userId);
			return new Result(true,"抢购成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false,"秒杀失败");
		}
	}
	@RequestMapping("/submitOrder")
	public Result submitOrder(Long seckillId){
		String userId = SecurityContextHolder.getContext().getAuthentication().getName();
		if("anonymousUser".equals(userId)){//如果未登录
			return new Result(false, "用户未登录");
		}
		try {
			seckillOrderService.submitOrder(seckillId, userId);
			return new Result(true, "提交成功");
		}catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "提交失败");
		}
	}
	
	/**
	 * 返回全部列表
	 * @return
	 */
	@RequestMapping("/findPage")
	public PageResult findPage(int page,int rows){			
		return seckillOrderService.findPage(page, rows);
	}
	
	/**
	 * 增加
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/add")
	public Result add(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.add(seckillOrder);
			return new Result(true, "增加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "增加失败");
		}
	}
	
	/**
	 * 修改
	 * @param seckillOrder
	 * @return
	 */
	@RequestMapping("/update")
	public Result update(@RequestBody TbSeckillOrder seckillOrder){
		try {
			seckillOrderService.update(seckillOrder);
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
	public TbSeckillOrder findOne(Long id){
		return seckillOrderService.findOne(id);		
	}
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	@RequestMapping("/delete")
	public Result delete(Long[] ids){
		try {
			seckillOrderService.delete(ids);
			return new Result(true, "删除成功"); 
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 查询+分页
	 * @param brand
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping("/search")
	public PageResult search(@RequestBody TbSeckillOrder seckillOrder, int page, int size){
		return seckillOrderService.findPage(seckillOrder, page, size);		
	}
	
}
