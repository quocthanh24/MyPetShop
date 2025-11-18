package com.thanhluu.tlcn.Mapper;

import com.thanhluu.tlcn.DTO.response.Cart.CartResp;
import com.thanhluu.tlcn.DTO.response.Product.Product_CartResp;
import com.thanhluu.tlcn.DTO.response.Product.ViewProductFromCartResp;
import com.thanhluu.tlcn.Entity.CartEntity;
import com.thanhluu.tlcn.Entity.CartItemEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductMapper.class})
public interface CartMapper {
    
    @Mapping(target = "cartId", source = "id")
    @Mapping(target = "products", source = "items")
    CartResp toCartResp(CartEntity cartEntity);
    
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    Product_CartResp toProductCartResp(CartItemEntity cartItemEntity);

    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "productName", source = "product.name")
    @Mapping(target = "thumbnailUrl", source = "product.thumbnailUrl")
    @Mapping(target = "price", source = "product.price")
    ViewProductFromCartResp toViewProductFromCartResp(CartItemEntity cartItemEntity);
}
