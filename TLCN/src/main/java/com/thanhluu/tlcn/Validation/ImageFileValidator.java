package com.thanhluu.tlcn.Validation;

import com.thanhluu.tlcn.Annotation.ValidImage;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

public class ImageFileValidator implements ConstraintValidator<ValidImage, MultipartFile> {

  private long maxSize;
  private String[] allowedTypes;

  @Override
  public void initialize(ValidImage constraintAnnotation) {
    this.maxSize = constraintAnnotation.maxSize();
    this.allowedTypes = constraintAnnotation.allowedTypes();
  }

  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    // Cho phép null/empty (dùng @NotNull riêng nếu bắt buộc)
    if (file == null || file.isEmpty()) {
      return true;
    }

    // Validate file size
    if (file.getSize() > maxSize) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
        "File size must not exceed " + (maxSize / 1024 / 1024) + "MB"
      ).addConstraintViolation();
      return false;
    }

    // Validate content type
    String contentType = file.getContentType();
    if (contentType == null || !Arrays.asList(allowedTypes).contains(contentType)) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate(
        "Only " + String.join(", ", allowedTypes) + " files are allowed"
      ).addConstraintViolation();
      return false;
    }

    // Validate file extension
    String originalFilename = file.getOriginalFilename();
    if (originalFilename != null) {
      String extension = originalFilename.substring(
        originalFilename.lastIndexOf(".") + 1
      ).toLowerCase();

      if (!Arrays.asList("jpg", "jpeg", "png").contains(extension)) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
          "Only JPG, JPEG, PNG files are allowed"
        ).addConstraintViolation();
        return false;
      }
    }

    return true; // Validation passed
  }


}
