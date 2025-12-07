package com.thanhluu.tlcn.Service.Customer;

import com.thanhluu.tlcn.DTO.request.Rating.RatingReq;
import com.thanhluu.tlcn.DTO.response.Rating.RatingResp;

import java.util.UUID;

public interface IRatingService {
  RatingResp rate(UUID productId, RatingReq req);
}
