package com.thanhluu.tlcn.Service.Customer;

import com.thanhluu.tlcn.DTO.response.Cart.Item.ItemResp;

import java.util.List;
import java.util.UUID;

public interface ICartItemService {
  ItemResp updateItem(String itemId,Integer quantity);
  String deleteItem(String itemId);
  String deleteAllByIds(List<UUID> itemIds);
}
