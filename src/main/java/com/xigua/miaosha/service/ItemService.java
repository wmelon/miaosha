package com.xigua.miaosha.service;

import com.xigua.miaosha.error.BusinessException;
import com.xigua.miaosha.service.model.ItemModel;

import java.util.List;

public interface ItemService {

    /// 创建商品
    ItemModel createItem(ItemModel itemModel) throws BusinessException;

    /// 商品列表浏览
    List<ItemModel> itemList();

    /// 商品详情浏览
    ItemModel getItemById(Integer id);

}
