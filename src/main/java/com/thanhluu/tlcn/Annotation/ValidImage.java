package com.thanhluu.tlcn.Annotation;

import com.thanhluu.tlcn.Validation.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ImageFileValidator.class)
public @interface ValidImage {
  String message() default "Invalid image file";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};

  long maxSize() default 5 * 1024 * 1024;
  String[] allowedTypes() default {"image/jpeg", "image/png"};
}
