package com.xigua.miaosha.service.impl;

import com.xigua.miaosha.dao.ItemDOMapper;
import com.xigua.miaosha.dao.ItemStockDOMapper;
import com.xigua.miaosha.dataobject.ItemDO;
import com.xigua.miaosha.dataobject.ItemStockDO;
import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.error.EmBussinessError;
import com.xigua.miaosha.service.ItemService;
import com.xigua.miaosha.service.model.ItemModel;
import com.xigua.miaosha.validator.ValidatorImpl;
import com.xigua.miaosha.validator.ValidatorResult;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException{

        /// 检验入参
        ValidatorResult validatorResult = validator.validate(itemModel);
        if (validatorResult.isHasError()){
            throw new BusinessException(EmBussinessError.PARAMETER_VALIDATION_ERROR,validatorResult.getErrMsg());
        }

        /// 转换ItemModel
        ItemDO itemDO = this.convertFromItemModel(itemModel);

        /// 写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());

        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);

        /// 返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    private ItemDO convertFromItemModel(ItemModel itemModel){
        if (itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();

        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    @Override
    public List<ItemModel> itemList() {
        List<ItemDO> itemDOS = itemDOMapper.selectItems();

        List<ItemModel> itemModelList = itemDOS.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());

        return itemModelList;
    }

    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if (itemDO == null) {
            return null;
        }
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());

        return this.convertModelFromDataObject(itemDO,itemStockDO);
    }

    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO) {
        if (itemDO == null) {
            return null;
        }
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setStock(itemStockDO.getStock());
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        return itemModel;
    }
}
