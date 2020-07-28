package com.sunxupeng.sellergoods.controller;
import com.alibaba.dubbo.config.annotation.Reference;
import com.sunxupeng.entity.PageResult;
import com.sunxupeng.entity.Result;
import com.sunxupeng.pojo.TbBrand;

import com.sunxupeng.sellergoods.service.BrandService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("/findAll")
    @ResponseBody
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    @RequestMapping("/findPage")
    @ResponseBody
    public PageResult findPage(int page, int size){
        return brandService.findPage(page, size);
    }

    @RequestMapping("/add")
    @ResponseBody
    public Result add(@RequestBody TbBrand brand){
        try {
            brandService.add(brand);
            return new Result(true, "增加成品牌功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "增加品牌失败");
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public Result update(@RequestBody TbBrand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改品牌成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"修改品牌失败");
        }
    }

    @RequestMapping("/findOne")
    @ResponseBody
    public TbBrand findone(Long id){
       return brandService.findOne(id);
    }
    @RequestMapping("/delete")
    @ResponseBody
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"删除失败");
        }
    }

    @RequestMapping("/search")
    @ResponseBody
    public PageResult search(@RequestBody TbBrand brand, int page, int size){
        return brandService.findPage(brand, page, size);
    }

    @RequestMapping("/selectOptionList")
    @ResponseBody
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }
    }



