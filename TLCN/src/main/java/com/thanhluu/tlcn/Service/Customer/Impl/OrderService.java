package com.thanhluu.tlcn.Service.Customer.Impl;


import com.thanhluu.tlcn.DTO.request.Order.CartOrderReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderCreateReq;
import com.thanhluu.tlcn.DTO.request.Order.OrderItemReq;
import com.thanhluu.tlcn.DTO.request.Shipment.AddressReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShipmentRequestFromOwner;
import com.thanhluu.tlcn.DTO.request.Shipment.ShippingFeeReq;
import com.thanhluu.tlcn.DTO.request.Shipment.ShippingOrderReq;
import com.thanhluu.tlcn.DTO.response.Order.OrderStatusResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderResp;
import com.thanhluu.tlcn.DTO.response.Order.OrderTrackingResp;
import com.thanhluu.tlcn.DTO.response.Address.AddressResp;
import com.thanhluu.tlcn.Entity.*;
import com.thanhluu.tlcn.Enum.DiscountStatus;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Enum.OrderStatus;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.OrderMapper;
import com.thanhluu.tlcn.Repository.*;
import com.thanhluu.tlcn.Service.Customer.ICartItemService;
import com.thanhluu.tlcn.Service.Customer.IOrderService;
import com.thanhluu.tlcn.Service.Shipment.IShipmentService;
import com.thanhluu.tlcn.Util.AddressParingUtil;
import com.thanhluu.tlcn.Util.JavaMailUtil;
import com.thanhluu.tlcn.Util.ShippingDefaultUtil;
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
  private CartItemRepository cartItemRepository;
  @Autowired
  private UserDiscountRepository userDiscountRepository;
  @Autowired
  private ICartItemService cartItemService;
  @Autowired
  private IShipmentService shipmentService;
  @Autowired
  private AddressParingUtil addressParsingUtil;
  @Autowired
  private JavaMailUtil javaMailUtil;
  @Autowired
  private ShippingDefaultUtil shippingDefaultUtil;

  @Override
  public OrderResp cartOrder(String userId, CartOrderReq request) {

    UserEntity customer = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));

    List<CartItemEntity> cartItems = cartItemRepository.findAllById(request.getCartItemIds());

    if (cartItems.isEmpty()) {
      throw new BadRequestException(ErrorCode.NO_PRODUCT_SELECTED);
    }

    // Parse địa chỉ giao hàng để lấy districtId và wardId
    AddressResp fromAddressInfo = addressParsingUtil.parseAddress(request.getFromAddress());
    AddressResp toAddressInfo = addressParsingUtil.parseAddress(request.getToAddress());
    log.info("Parsed address - District: {}, Ward: {}", toAddressInfo.getDistrictId(), toAddressInfo.getWardId());


    // Tạo request tính cước vận chuyển
    ShippingFeeReq shippingFeeReq = new ShippingFeeReq();
    // Thông tin kho hàng (cần cấu hình từ application.properties)
    shippingFeeReq.setFromDistrictId(Integer.parseInt(fromAddressInfo.getDistrictId())); // Mặc định: HCM - Q1 (có thể cấu hình)
    shippingFeeReq.setFromWardCode(fromAddressInfo.getWardId()); // Mặc định: Bến Nghé (có thể cấu hình)
    
    // Thông tin địa chỉ giao hàng (từ việc parse)
    shippingFeeReq.setToDistrictId(Integer.parseInt(toAddressInfo.getDistrictId()));
    shippingFeeReq.setToWardCode(toAddressInfo.getWardId());
    shippingFeeReq.setServiceTypeId(request.getServiceType() != null ? request.getServiceType() : 2); // Default: 2 (hàng nhẹ)
    
    // Thông tin kích thước và cân nặng
    shippingFeeReq.setWeight(request.getWeight() != null ? request.getWeight() : 500); // Default: 500g
    shippingFeeReq.setLength(request.getLength() != null ? request.getLength() : 10); // Default: 10cm
    shippingFeeReq.setWidth(request.getWidth() != null ? request.getWidth() : 10); // Default: 10cm
    shippingFeeReq.setHeight(request.getHeight() != null ? request.getHeight() : 10); // Default: 10cm

    // Thông tin sản phẩm gửi đi
    List<OrderItemReq> items = new ArrayList<>();
    for (CartItemEntity cartItem : cartItems) {
      OrderItemReq itemReq = new OrderItemReq();
      itemReq.setName(cartItem.getProduct().getName());
      itemReq.setQuantity(cartItem.getQuantity());
      items.add(itemReq);
    }
    shippingFeeReq.setItems(items);

    // Gọi API GHN để tính cước vận chuyển
    Integer shippingFee = shipmentService.calculateFee(shippingFeeReq);
    Double shippingFeeAmount = shippingFee.doubleValue();
    log.info("Calculated shipping fee: {}", shippingFeeAmount);

    OrderEntity order = OrderEntity.builder()
      .orderNumber(generateOrderNumber())
      .shippingAddress(request.getToAddress())
      .shippingFee(shippingFeeAmount)
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
      // Reduct Product Quantity
      product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
      productRepository.save(product);
      //
      orderDetails.add(orderDetail);
    });

    Double totalPrice = orderDetails.stream().mapToDouble(OrderDetailEntity::getTotalPrice).sum();
    totalPrice = applyDiscountIfValid(request.getDiscountCode(), customer, order, totalPrice);
    // Cộng cước vận chuyển vào tổng tiền sau khi áp dụng discount
    totalPrice += shippingFeeAmount;
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
//    // Lấy thông tin khách hàng
//    UserEntity customer = userRepository.findById(UUID.fromString(request.getUserId()))
//      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
//    // Lấy thông tin sản phẩm
//    OrderItemReq orderItem = request.getOrderItems().get(0);
//    ProductEntity product = productRepository.findById(UUID.fromString(orderItem.getProductId()))
//      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));
//
//    // Kiểm tra còn đủ số lượng hàng không
//    if (!hasSufficientStock(orderItem.getQuantity(), product.getStockQuantity())) {
//      throw new BadRequestException(ErrorCode.INSUFFICIENT_STOCK);
//    }
//
//
//    OrderDetailEntity orderDetail = OrderDetailEntity.builder()
//      .product(product)
//      .quantity(orderItem.getQuantity())
//      .price(product.getPrice())
//      .totalPrice(product.getPrice() * orderItem.getQuantity())
//      .build();
//
//    OrderEntity newOrder = OrderEntity.builder()
//      .orderNumber(generateOrderNumber())
//      .shippingAddress(request.getShippingAddress())
//      .phoneNumber(request.getPhoneNumber())
//      .note(request.getNote())
//      .totalPrice(orderDetail.getTotalPrice())
////      .shippingFee(1500d)
//      .paymentMethod(request.getPaymentMethod())
//      .orderDate(LocalDateTime.now())
//      .orderStatus(OrderStatus.PENDING)
//      .build();
//
//    String discountCode = request.getDiscountCode();
//    Double totalPrice = orderDetail.getTotalPrice();
//
//    // Kiểm tra có áp mã giảm giá không
//    totalPrice = applyDiscountIfValid(discountCode, customer, newOrder, totalPrice);
//
//    orderDetail.setOrder(newOrder);
//    newOrder.setOrderDetails(List.of(orderDetail));
//    newOrder.setTotalPrice(totalPrice);
//    orderRepository.save(newOrder);
//
//    return orderMapper.toOrderResp(newOrder);
    return null;
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
  public Page<OrderStatusResp> getAllOrdersArePaidAndPending(List<OrderStatus> orderStatuses, Pageable pageable) {
    Page<OrderEntity> orders = orderRepository.findAllByOrderStatusIn(orderStatuses, pageable);
    return orders.map(order -> orderMapper.toOrderStatusResp(order));
  }

  @Override
  public Page<OrderTrackingResp> getAllOrderByUser(String userId, Pageable pageable) {
    UserEntity customer = userRepository.findById(UUID.fromString(userId))
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    Page<OrderEntity> orders = orderRepository.findAllByCustomerAndOrderStatusIsNot(customer, OrderStatus.CANCELED, pageable);
    return orders.map(order -> orderMapper.toOrderTrackingResp(order));
  }

  @Override
  public String createShippingOrderByOrderNumber(ShipmentRequestFromOwner request) {
    // Ordernumber , fromAdrress, height, width, length, weight, requirement Note
    log.info("Shipping Request : {}", request.toString());
    OrderEntity order = orderRepository.findByOrderNumber(request.getOrderNumber())
      .orElseThrow(() -> new BadRequestException(ErrorCode.ORDER_NOT_FOUND));

    UserEntity customer = order.getCustomer();
    UserEntity sender = UserEntity.builder()
      .address(request.getSenderAddress())
      .phoneNumber(request.getSenderPhone())
      .fullName(request.getSenderName())
      .build();
    log.info("Sender address is {}", sender.getAddress());
    log.info("Customer address is {}", customer.getAddress());
    AddressReq fromAddress = findAddress(sender);
    AddressReq toAddress = findAddress(customer);


    log.info("To name : {}", toAddress.toString());
    log.info("From name : {}", fromAddress.toString());
    ShippingOrderReq req = ShippingOrderReq.builder()
      .clientOrderCode(order.getOrderNumber())
      .items(order.getOrderDetails().stream()
        .map(orderMapper::toOrderItemReq)
        .toList())
      .codAmount(order.getTotalPrice().intValue())
      // From
      .fromAddress(fromAddress.getAddress())
      .fromPhone(fromAddress.getPhone())
      .fromDistrictId(fromAddress.getDistrictId())
      .fromWardCode(fromAddress.getWardCode())
      .fromName(fromAddress.getName())
      .fromProvinceName(fromAddress.getProvinceName())
      // To
      .toAddress(toAddress.getAddress())
      .toPhone(toAddress.getPhone())
      .toDistrictId(toAddress.getDistrictId())
      .toWardCode(toAddress.getWardCode())
      .toName(toAddress.getName())
      .toProvinceName(toAddress.getProvinceName())

      .height(request.getHeight())
      .width(request.getWidth())
      .length(request.getLength())
      .weight(request.getWeight())
      .paymentTypeId(order.getOrderStatus().equals(OrderStatus.PENDING) ? 1 : 2)
      .serviceTypeId(request.getWeight() <= 20000 ? 2 : 5)
      .requiredNote(request.getRequiredNote())
      .build();

    String orderCode = shipmentService.createShippingOrder(req);
    String email = customer.getGmail();
    String customerName = customer.getFullName();

     javaMailUtil.sendOrderCodeEmail(email, orderCode, customerName);

    return shipmentService.createShippingOrder(req);
  }


  private AddressReq findAddress(UserEntity user) {
    AddressReq req = new AddressReq();
    req.setName(user.getFullName());
    req.setPhone(user.getPhoneNumber());
    req.setAddress(user.getAddress());
    req.setWardCode(addressParsingUtil.getWardCodeFromAddress(user.getAddress()));
    req.setProvinceName(addressParsingUtil.getProvinceNameFromAddress(user.getAddress()));
    req.setDistrictId(addressParsingUtil.getDistrictIdFromAddress(user.getAddress()));
    return req;
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


