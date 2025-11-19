package com.thanhluu.tlcn.Service.Customer.Impl;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.Entity.*;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.OrderMapper;
import com.thanhluu.tlcn.Repository.*;
import com.thanhluu.tlcn.Service.Customer.ICartItemService;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import com.thanhluu.tlcn.Service.Shipping.IShippingFeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderService {
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private OrderDetailRepository orderDetailRepository;
  @Autowired
  private OrderMapper orderMapper;
  @Autowired
  private OrderRepository orderRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private DiscountRepository discountRepository;
  @Autowired
  private CartRepository cartRepository;
  @Autowired
  private CartItemRepository cartItemRepository;
  @Autowired
  private UserDiscountRepository userDiscountRepository;
  @Autowired
  private ICartItemService cartItemService;

  @Override
  public OrderResp cartOrder(String userId, CartOrderReq request) {

    UserEntity customer = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    CartEntity shoppingCart = cartRepository.findByCustomer(customer)
      .orElseThrow(() -> new BadRequestException(ErrorCode.CART_NOT_FOUND));

    List<CartItemEntity> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

    if (cartItems.isEmpty()) {
      throw new BadRequestException(ErrorCode.NO_PRODUCT_SELECTED);
    }

    OrderEntity order = OrderEntity.builder()
      .orderNumber(generateOrderNumber())
      .shippingAddress(request.getShippingAddress())
//      .shippingFee(150000d)
      .phoneNumber(request.getPhoneNumber())
      .note(request.getNote())
      .paymentMethod(request.getPaymentMethod())
      .orderDate(LocalDateTime.now())
      .orderStatus(OrderStatus.PENDING)
      .customer(customer)
      .build();

    orderRepository.save(order);

    List<OrderDetailEntity> orderDetails = new ArrayList<>();

    cartItems.forEach(cartItem -> {
      ProductEntity product = productRepository.findById(cartItem.getProduct().getId())
        .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));
      OrderDetailEntity orderDetail = OrderDetailEntity.builder()
        .product(product)
        .quantity(cartItem.getQuantity())
        .price(product.getPrice())
        .totalPrice(product.getPrice() * cartItem.getQuantity())
        .order(order)
        .build();
      orderDetails.add(orderDetail);
    });

    Double totalPrice = orderDetails.stream().mapToDouble(OrderDetailEntity::getTotalPrice).sum();
    totalPrice = applyDiscountIfValid(request.getDiscountCode(), customer, order, totalPrice);
    order.setTotalPrice(totalPrice);
    order.setOrderDetails(orderDetails);
    orderDetailRepository.saveAll(orderDetails);

    // Cập nhật shopping cart
    log.info("Cart Item Id is {}", request.getCartItemIds());
    cartItemService.deleteAllByIds(request.getCartItemIds());
    return orderMapper.toOrderResp(order);
  }

  @Override
  public OrderResp buyNowOrder(OrderCreateReq request) {
    // Lấy thông tin khách hàng
    UserEntity customer = userRepository.findById(UUID.fromString(request.getUserId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    // Lấy thông tin sản phẩm
    OrderItemReq orderItem = request.getOrderItems().get(0);
    ProductEntity product = productRepository.findById(UUID.fromString(orderItem.getProductId()))
      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

    // Kiểm tra còn đủ số lượng hàng không
    if (!hasSufficientStock(orderItem.getQuantity(), product.getStockQuantity())) {
      throw new BadRequestException(ErrorCode.INSUFFICIENT_STOCK);
    }


    OrderDetailEntity orderDetail = OrderDetailEntity.builder()
      .product(product)
      .quantity(orderItem.getQuantity())
      .price(product.getPrice())
      .totalPrice(product.getPrice() * orderItem.getQuantity())
      .build();

    OrderEntity newOrder = OrderEntity.builder()
      .orderNumber(generateOrderNumber())
      .shippingAddress(request.getShippingAddress())
      .phoneNumber(request.getPhoneNumber())
      .note(request.getNote())
      .totalPrice(orderDetail.getTotalPrice())
//      .shippingFee(1500d)
      .paymentMethod(request.getPaymentMethod())
      .orderDate(LocalDateTime.now())
      .orderStatus(OrderStatus.PENDING)
      .build();

    String discountCode = request.getDiscountCode();
    Double totalPrice = orderDetail.getTotalPrice();

    // Kiểm tra có áp mã giảm giá không
    totalPrice = applyDiscountIfValid(discountCode, customer, newOrder, totalPrice);

    orderDetail.setOrder(newOrder);
    newOrder.setOrderDetails(List.of(orderDetail));
    newOrder.setTotalPrice(totalPrice);
    orderRepository.save(newOrder);

    return orderMapper.toOrderResp(newOrder);
  }


  @Override
  public OrderStatusResp updateOrderStatus(String orderNumber, OrderStatus orderStatus) {
    OrderEntity order = orderRepository.findByOrderNumber(orderNumber)
      .orElseThrow(() -> new BadRequestException(ErrorCode.ORDER_NOT_FOUND));
    order.setOrderStatus(orderStatus);
    orderRepository.save(order);
    return orderMapper.toOrderStatusResp(order);
  }

  @Override
  public Page<OrderStatusResp> getAllOrdersArePaid(OrderStatus orderStatus, Pageable pageable) {
    Page<OrderEntity> orders = orderRepository.findAllByOrderStatus(orderStatus, pageable);
    return orders.map(order -> orderMapper.toOrderStatusResp(order));
  }

  @Override
  public Page<OrderStatusResp> getAllOrderByUser(String userId, Pageable pageable) {
    UserEntity customer = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    Page<OrderEntity> orders = orderRepository.findAllByCustomer(customer, pageable);
    return orders.map(order -> orderMapper.toOrderStatusResp(order));
  }

  private String generateOrderNumber() {
    String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
    int rand = new Random().nextInt(9000) + 1000;
    return "ORD-" + ts + "-" + rand;
  }

  private boolean hasSufficientStock(Integer quantity, Integer stockQuantity) {
    return quantity <= stockQuantity;
  }

  private boolean isUsed(DiscountEntity discount, UserEntity customer) {
    return userDiscountRepository.findByCustomerAndDiscount(customer, discount)
      .map(entity -> entity.getUsedAt() != null)
      .orElse(false);
  }

  private Double applyDiscountIfValid(String discountCode, UserEntity customer, OrderEntity newOrder, Double totalPrice) {
    if (discountCode == null || discountCode.isBlank()) {
      return totalPrice; // Không có mã giảm giá => bỏ qua
    }

    DiscountEntity discount = discountRepository.findByDiscountCode(discountCode)
      .orElseThrow(() -> new BadRequestException(ErrorCode.DISCOUNT_NOT_FOUND));

    if (discount.getStatus() != DiscountStatus.ACTIVE) {
      throw new BadRequestException(ErrorCode.DISCOUNT_IS_NOT_ACTIVE);
    }

    if (isUsed(discount, customer)) {
      throw new BadRequestException(ErrorCode.DISCOUNT_IS_USED);
    }

    newOrder.setDiscount(discount);
    newOrder.setTotalPrice(totalPrice * discount.getPercent());
    UserDiscountEntity userDiscount = UserDiscountEntity.builder()
      .customer(customer)
      .discount(discount)
      .usedAt(LocalDateTime.now())
      .build();
    userDiscountRepository.save(userDiscount);
    return totalPrice * (1 - discount.getPercent() / 100);
  }


}


