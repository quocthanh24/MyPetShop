package com.thanhluu.tlcn.Exeception;

import com.thanhluu.tlcn.Enum.ErrorCode;
import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

  private final ErrorCode errorCode;

  public BadRequestException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
