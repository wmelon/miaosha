package com.xigua.miaosha.controller;


import com.xigua.miaosha.controller.viewobject.ItemVO;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.response.CommonReturnType;
import com.xigua.miaosha.service.ItemService;
import com.xigua.miaosha.service.model.ItemModel;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller("/item")
@RequestMapping("/item")
@CrossOrigin(origins = {"*"},allowCredentials = "true")
public class ItemController extends BaseController{

    @Autowired
    private ItemService itemService;

    @RequestMapping(value = {"/createItem"}, method={RequestMethod.POST},consumes={CONTENT_TYPE_APPLICATION_JSON}, produces={CONTENT_TYPE_APPLICATION_JSON})
    @ResponseBody
    @ApiOperation(value = "创建项目")
    public CommonReturnType createItem(@RequestBody ItemModel itemModel) throws BusinessException {

        /// 调用service层创建商品
        ItemModel resultItemModel = itemService.createItem(itemModel);

        ItemVO itemVO = this.convertFromItemModel(itemModel);
        if (itemVO == null){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
        return CommonReturnType.create(itemVO);
    }

    private ItemVO convertFromItemModel(ItemModel itemModel) {
        if (itemModel == null) {
            return null;
        }
        ItemVO itemVO = new ItemVO();
        BeanUtils.copyProperties(itemModel,itemVO);
        return itemVO;
    }

    @RequestMapping(value = "/itemDetail",method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "创建项目")
    public CommonReturnType itemById(@RequestParam(name = "id") Integer id) throws BusinessException{
        ItemModel itemModel = itemService.getItemById(id);
        if (itemModel == null) {
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR);
        }
        ItemVO itemVO = this.convertFromItemModel(itemModel);
        return CommonReturnType.create(itemVO);
    }

    @RequestMapping(value = "itemList",method = {RequestMethod.GET})
    @ResponseBody
    @ApiOperation(value = "项目列表")
    public CommonReturnType itemList() throws BusinessException {
        List<ItemModel> itemModelList = itemService.itemList();

        List<ItemVO> itemVOList = itemModelList.stream().map(itemModel -> {
            ItemVO itemVO = this.convertFromItemModel(itemModel);
            return itemVO;
        }).collect(Collectors.toList());

        return CommonReturnType.create(itemVOList);
    }
}
