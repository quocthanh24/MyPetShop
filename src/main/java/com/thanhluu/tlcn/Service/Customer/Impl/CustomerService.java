package com.thanhluu.tlcn.Service.Customer.Impl;

import com.thanhluu.tlcn.DTO.request.Cart.AddToCartRequest;
import com.thanhluu.tlcn.DTO.response.Cart.CartResp;
import com.thanhluu.tlcn.DTO.response.Product.Product_CartResp;
import com.thanhluu.tlcn.DTO.response.Product.ViewProductFromCartResp;
import com.thanhluu.tlcn.Entity.CartEntity;
import com.thanhluu.tlcn.Entity.CartItemEntity;
import com.thanhluu.tlcn.Entity.ProductEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.CartMapper;
import com.thanhluu.tlcn.Repository.CartItemRepository;
import com.thanhluu.tlcn.Repository.CartRepository;
import com.thanhluu.tlcn.Repository.ProductRepository;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Customer.ICustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CustomerService implements ICustomerService {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private CartRepository cartRepository;
  @Autowired
  private CartItemRepository cartItemRepository;
  @Autowired
  private CartMapper cartMapper;

  @Override
  @Transactional
  public CartResp addToCart(AddToCartRequest req) {
    UserEntity customer = userRepository.findById(UUID.fromString(req.getUserId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    
    ProductEntity product = productRepository.findById(UUID.fromString(req.getProductId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

    Integer requestQuantity = req.getQuantity();
    if (product.getStockQuantity() < requestQuantity) {
      throw new BadRequestException(ErrorCode.INSUFFICIENT_STOCK);
    }

    CartEntity cart = getOrCreateCart(customer);

    // Tạo cart item mới
    CartItemEntity cartItem = CartItemEntity.builder()
      .cart(cart)
      .product(product)
      .quantity(requestQuantity)
      .build();

    cartItemRepository.save(cartItem);
    cart.getItems().add(cartItem);
    cartRepository.save(cart);

    // Tính toán lại totalPrice
    calculateCartTotals(cart);
    cart = cartRepository.save(cart);
    
    return cartMapper.toCartResp(cart);
  }

  @Override
  @Transactional
  public CartResp updateCart(AddToCartRequest req) {
    UserEntity customer = userRepository.findById(UUID.fromString(req.getUserId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    
    ProductEntity product = productRepository.findById(UUID.fromString(req.getProductId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));
    
    CartEntity cart = customer.getCart();
    if (cart == null) {
      throw new BadRequestException(ErrorCode.CART_NOT_FOUND);
    }
    
    // Validate stock quantity
    Integer requestQuantity = req.getQuantity() != null ? req.getQuantity() : 1;
    if (requestQuantity <= 0) {
      throw new BadRequestException(ErrorCode.INVALID_QUANTITY);
    }
    if (product.getStockQuantity() < requestQuantity) {
      throw new BadRequestException(ErrorCode.INSUFFICIENT_STOCK);
    }
    
    // Tìm cart item đã tồn tại
    CartItemEntity existingCartItem = cartItemRepository.findByCartAndProduct(cart, product)
      .orElse(null);
    
    if (existingCartItem != null) {
      // Cập nhật quantity nếu đã có item này trong cart
      existingCartItem.setQuantity(requestQuantity);
      cartItemRepository.save(existingCartItem);
    } else {
      // Tạo cart item mới nếu chưa có
      CartItemEntity newCartItem = CartItemEntity.builder()
        .cart(cart)
        .product(product)
        .quantity(requestQuantity)
        .build();
      cartItemRepository.save(newCartItem);
    }
    
    // Refresh cart để load lại items từ database
    cart = cartRepository.findById(cart.getId())
      .orElseThrow(() -> new BadRequestException(ErrorCode.CART_NOT_FOUND));
    
    // Tính toán lại totalPrice và quantity
    calculateCartTotals(cart);
    cart = cartRepository.save(cart);
    
    return cartMapper.toCartResp(cart);
  }

  @Override
  public CartEntity getOrCreateCart(UserEntity customer) {
    return cartRepository.findByCustomer(customer)
      .orElseGet(() -> {
        CartEntity cart = CartEntity.builder()
          .customer(customer)
          .items(new ArrayList<>())
          .totalPrice(0.0)
          .build();
        return cartRepository.save(cart);
      });
  }

  @Override
  public CartResp deleteItemFromCart(String userId, String itemId) {
    return null;
  }

  @Override
  public Page<ViewProductFromCartResp> getProductsFromCart(String userId, Pageable pageable) {
    UserEntity user = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    CartEntity cart = cartRepository.findByCustomer(user)
      .orElseThrow(() -> new BadRequestException(ErrorCode.CART_NOT_FOUND));
    return cartItemRepository.findByCart(cart, pageable)
      .map(cartMapper::toViewProductFromCartResp);
  }

  @Override
  public CartResp removeProductFromCart(String userId, List<UUID> cartItemIds) {
    UserEntity customer = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    CartEntity cart = cartRepository.findByCustomer(customer)
      .orElseThrow(() -> new BadRequestException(ErrorCode.CART_NOT_FOUND));

    List<CartItemEntity> items = cartItemRepository.findAllById(cartItemIds);
    items.forEach(cartItem -> {
      if (!cartItem.getCart().getId().equals(cart.getId())) {
        throw new BadRequestException(ErrorCode.CART_ITEM_NOT_BELONG_TO_USER);
      }
    });

    cartItemRepository.deleteAllById(cartItemIds);

    double newTotal = cart.getItems().stream()
      .mapToDouble(i -> i.getProduct().getPrice() * i.getQuantity())
      .sum();

    cart.setTotalPrice(newTotal);
    cartRepository.save(cart);


    return null;
  }

  private void calculateCartTotals(CartEntity cart) {
    double totalPrice = 0.0;

    for (CartItemEntity item : cart.getItems()) {
      if (item.getProduct() != null && item.getQuantity() != null) {
        totalPrice += item.getProduct().getPrice() * item.getQuantity();
      }
    }
    log.info("Total price is {}", totalPrice);
    cart.setTotalPrice(totalPrice);
  }
}
