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
    PRODUCT_DELETE_FAILED(1113, "Product deleted failed", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(1114, "Category not found", HttpStatus.NOT_FOUND),
    NO_DATA(1115, "No data", HttpStatus.NOT_FOUND),
    FORM_REGISTRATION_NOT_FOUND(1116, "Form registration not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1117, "Cart not found", HttpStatus.NOT_FOUND),
    DISCOUNT_NOT_FOUND(1118, "Discount not found", HttpStatus.NOT_FOUND),
    DISCOUNT_IS_NOT_ACTIVE(1119, "Discount is not active", HttpStatus.BAD_REQUEST),
    DISCOUNT_IS_USED(1120, "Discount is used", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_STATUS_CHANGE(1121, "Invalid discount status change", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(1122, "Invalid quantity. Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(1123, "Insufficient stock available", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1124, "Order not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_FOUND(1125, "Appointment not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1126, "User with this email already exists", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1127, "Invalid or expired OTP code", HttpStatus.BAD_REQUEST),
    USER_NOT_PURCHASED_PRODUCT(1128, "User not purchased this product", HttpStatus.BAD_REQUEST),
    DUPLICATE_RATING(1129, "Duplicate rating", HttpStatus.BAD_REQUEST),
    RATING_UPDATED_FAILED(1130, "Rating updated failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_PRODUCT_SELECTED(1131, "No product selected", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_BELONG_TO_USER(1132, "Cart Item Do Not Belong to User", HttpStatus.BAD_REQUEST),
    ITEM_NOT_FOUND(1133, "Item not found", HttpStatus.NOT_FOUND),
    INVALID_SIGNATURE(1134, "Invalid signature", HttpStatus.BAD_REQUEST),
    MISSING_RESULT_CODE(1135, "Missing result code", HttpStatus.BAD_REQUEST),
    PROVINCES_EXISTED(1136, "Provinces already exist, Do not import", HttpStatus.BAD_REQUEST),
    DISTRICTS_EXISTED(1137, "Districts already exist", HttpStatus.BAD_REQUEST),
    WARDS_EXISTED(1138, "Wards already exist, Do not import", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS(1139, "Invalid address format", HttpStatus.BAD_REQUEST),
    WARD_NOT_FOUND(1140, "Ward not found", HttpStatus.NOT_FOUND),
    DISTRICT_NOT_FOUND(1141, "District not found", HttpStatus.NOT_FOUND),
    OTHER_EXCEPTIONS(1999, "System error has occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int responseCode;
    private final String message;
    private final HttpStatus httpStatus;
}
