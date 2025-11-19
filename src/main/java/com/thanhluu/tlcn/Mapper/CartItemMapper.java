package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Cart.Item.ItemResp;
import com.thanhluu.tlcn.Entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {
  @Mapping(target = "cartItemId", source = "id")
  ItemResp toDTO(CartItemEntity cartItemEntity);
}
