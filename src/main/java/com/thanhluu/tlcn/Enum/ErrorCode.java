package com.thanhluu.tlcn.Enum;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ErrorCode {
    INVALID_HTTP(1100, "Invalid HTTP Request", HttpStatus.METHOD_NOT_ALLOWED),
    INVALID_PARAMETER_TYPE(1101, "Invalid parameter type", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST_BODY(1102, "Invalid request body format", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_PARAMETER(1103, "Missing required request parameter", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_MEDIA_TYPE(1104, "Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    RESOURCE_NOT_FOUND(1105, "Requested resource not found", HttpStatus.NOT_FOUND),
    SERVICE_UNAVAILABLE(1106, "The service is temporarily unavailable. Please try again later.", HttpStatus.SERVICE_UNAVAILABLE),
    ACCESS_DENIED(1107, "You do not have permission to perform this action!", HttpStatus.FORBIDDEN),
    USER_NO_EXIST(1108, "User with this id does not exist", HttpStatus.NOT_FOUND),
    LOGIN_FAILED(1109, "Login failed", HttpStatus.UNAUTHORIZED),
    OWNER_NOT_FOUND(1110, "Owner not found", HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_FOUND(1110, "Employee not found", HttpStatus.NOT_FOUND),
    MEDICAL_RECORD_NOT_FOUND(1111, "Medical Record not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(1112, "Product not found", HttpStatus.NOT_FOUND),
    CATEGORY_NOT_FOUND(1113, "Category not found", HttpStatus.NOT_FOUND),
    NO_DATA(1114, "No data", HttpStatus.NOT_FOUND),
    FORM_REGISTRATION_NOT_FOUND(1115, "Form registration not found", HttpStatus.NOT_FOUND),
    OTHER_EXCEPTIONS(1999, "System error has occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int responseCode;
    private final String message;
    private final HttpStatus httpStatus;
}
