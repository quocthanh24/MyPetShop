package com.thanhluu.tlcn.Service.Customer.Impl;

import com.thanhluu.tlcn.DTO.request.Rating.RatingReq;
import com.thanhluu.tlcn.DTO.response.Rating.RatingResp;
import com.thanhluu.tlcn.Entity.ProductEntity;
import com.thanhluu.tlcn.Entity.RatingEntity;
import com.thanhluu.tlcn.Entity.UserEntity;
import com.thanhluu.tlcn.Enum.ErrorCode;
import com.thanhluu.tlcn.Exeception.BadRequestException;
import com.thanhluu.tlcn.Mapper.RatingMapper;
import com.thanhluu.tlcn.Repository.OrderDetailRepository;
import com.thanhluu.tlcn.Repository.ProductRepository;
import com.thanhluu.tlcn.Repository.RatingRepository;
import com.thanhluu.tlcn.Repository.UserRepository;
import com.thanhluu.tlcn.Service.Customer.IRatingService;
import io.minio.errors.InternalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class RatingService implements IRatingService {
  @Autowired
  private RatingRepository ratingRepository;
  @Autowired
  private ProductRepository productRepository;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private OrderDetailRepository orderDetailRepository;
  @Autowired
  private RatingMapper ratingMapper;

  @Override
  public RatingResp rate(UUID productId, RatingReq req) {

    // Tìm user và product (chỉnh exception thành NotFound nếu có class riêng)
    UserEntity customer = userRepository.findById(req.getCustomerId())
      .orElseThrow(() -> new BadRequestException(ErrorCode.USER_NO_EXIST));
    ProductEntity product = productRepository.findById(productId)
      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

    // Check đã mua sản phẩm
    if (!orderDetailRepository.hasUserPurchasedProduct(req.getCustomerId(), productId)) {
      throw new BadRequestException(ErrorCode.USER_NOT_PURCHASED_PRODUCT);
    }

    // Check duplicate rating (tùy chọn, tránh user rate nhiều lần)
    if (ratingRepository.existsByCustomerIdAndProductId(req.getCustomerId(), productId)) {
      throw new BadRequestException(ErrorCode.DUPLICATE_RATING);  // Giả sử enum này
    }

    // Tạo và save rating TRƯỚC (để COUNT bao gồm rating mới)
    RatingEntity rating = ratingMapper.toEntity(req);  // Giả sử mapper set score/comment từ req
    rating.setCustomer(customer);
    rating.setProduct(product);
    rating.setCreatedDate(LocalDateTime.now());
    ratingRepository.save(rating);
    log.info("Rating saved successfully for product {} by user {}", productId, req.getCustomerId());

    // Update average SAU khi save (bây giờ COUNT đúng)
    int updatedRows = productRepository.updateAverageRating(productId, req.getRatingScore());
    if (updatedRows == 0) {
      log.error("Failed to update average rating for product {}", productId);  // Log error thay info
      throw new BadRequestException(ErrorCode.RATING_UPDATED_FAILED);  // Enum riêng cho server error
    }

    // Load Product sau update để lấy average mới nhất từ DB (không cần tính động)
    ProductEntity updatedProduct = productRepository.findById(productId)
      .orElseThrow(() -> new BadRequestException(ErrorCode.PRODUCT_NOT_FOUND));

    // Trả DTO với data đầy đủ
    RatingResp resp = ratingMapper.toDto(rating);
    resp.setAverageScore(updatedProduct.getAverageRating());
    resp.setRatingCount(updatedProduct.getRatingCount());
    return resp;
  }
}
