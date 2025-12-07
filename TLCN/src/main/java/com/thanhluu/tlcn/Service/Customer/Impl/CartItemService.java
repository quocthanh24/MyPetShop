package com.thanhluu.tlcn.Service.Customer.Impl;

import com.thanhluu.tlcn.DTO.response.Cart.Item.ItemResp;
import com.thanhluu.tlcn.Entity.CartItemEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.CartItemMapper;
import com.thanhluu.tlcn.Repository.CartItemRepository;
import com.thanhluu.tlcn.Service.Customer.ICartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CartItemService implements ICartItemService {
  @Autowired
  private CartItemRepository cartItemRepository;
  @Autowired
  private CartItemMapper cartItemMapper;

  @Override
  public ItemResp updateItem(String itemId, Integer quantity) {
    CartItemEntity item = cartItemRepository.findById(UUID.fromString(itemId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.ITEM_NOT_FOUND));
    item.setQuantity(quantity);
    cartItemRepository.save(item);
    return cartItemMapper.toDTO(item);
  }

  @Override
  public String deleteItem(String itemId) {
    cartItemRepository.deleteById(UUID.fromString(itemId));
    return "Item deleted successfully";
  }

  @Override
  public String deleteAllByIds(List<UUID> itemIds) {
    if (itemIds == null || itemIds.isEmpty()) {
      throw new BadRequestException(ErrorCode.INVALID_REQUEST_BODY);
    }
    for (UUID itemId : itemIds) {
      cartItemRepository.findById(itemId)
        .orElseThrow(() -> new BadRequestException(ErrorCode.ITEM_NOT_FOUND));
    }
    cartItemRepository.deleteAllById(itemIds);
    return "Items deleted successfully";
  }
}
