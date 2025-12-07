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
    USER_NOT_FOUND(1110, "User not found", HttpStatus.NOT_FOUND),
    OWNER_NOT_FOUND(1111, "Owner not found", HttpStatus.NOT_FOUND),
    EMPLOYEE_NOT_FOUND(1112, "Employee not found", HttpStatus.NOT_FOUND),
    MEDICAL_RECORD_NOT_FOUND(1113, "Medical Record not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(1114, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_DELETE_FAILED(1115, "Product deleted failed", HttpStatus.CONFLICT),
    CATEGORY_NOT_FOUND(1116, "Category not found", HttpStatus.NOT_FOUND),
    NO_DATA(1117, "No data", HttpStatus.NOT_FOUND),
    FORM_REGISTRATION_NOT_FOUND(1118, "Form registration not found", HttpStatus.NOT_FOUND),
    CART_NOT_FOUND(1119, "Cart not found", HttpStatus.NOT_FOUND),
    DISCOUNT_NOT_FOUND(1120, "Discount not found", HttpStatus.NOT_FOUND),
    DISCOUNT_IS_NOT_ACTIVE(1121, "Discount is not active", HttpStatus.BAD_REQUEST),
    DISCOUNT_IS_USED(1122, "Discount is used", HttpStatus.BAD_REQUEST),
    INVALID_DISCOUNT_STATUS_CHANGE(1123, "Invalid discount status change", HttpStatus.BAD_REQUEST),
    INVALID_QUANTITY(1124, "Invalid quantity. Quantity must be greater than 0", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_STOCK(1125, "Insufficient stock available", HttpStatus.BAD_REQUEST),
    ORDER_NOT_FOUND(1126, "Order not found", HttpStatus.NOT_FOUND),
    APPOINTMENT_NOT_FOUND(1127, "Appointment not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1128, "User with this email already exists", HttpStatus.BAD_REQUEST),
    INVALID_OTP(1129, "Invalid or expired OTP code", HttpStatus.BAD_REQUEST),
    USER_NOT_PURCHASED_PRODUCT(1130, "User not purchased this product", HttpStatus.BAD_REQUEST),
    DUPLICATE_RATING(1131, "Duplicate rating", HttpStatus.BAD_REQUEST),
    RATING_UPDATED_FAILED(1132, "Rating updated failed", HttpStatus.INTERNAL_SERVER_ERROR),
    NO_PRODUCT_SELECTED(1133, "No product selected", HttpStatus.NOT_FOUND),
    CART_ITEM_NOT_BELONG_TO_USER(1134, "Cart Item Do Not Belong to User", HttpStatus.BAD_REQUEST),
    ITEM_NOT_FOUND(1135, "Item not found", HttpStatus.NOT_FOUND),
    INVALID_SIGNATURE(1136, "Invalid signature", HttpStatus.BAD_REQUEST),
    MISSING_RESULT_CODE(1137, "Missing result code", HttpStatus.BAD_REQUEST),
    PROVINCES_EXISTED(1138, "Provinces already exist, Do not import", HttpStatus.BAD_REQUEST),
    DISTRICTS_EXISTED(1139, "Districts already exist", HttpStatus.BAD_REQUEST),
    WARDS_EXISTED(1140, "Wards already exist, Do not import", HttpStatus.BAD_REQUEST),
    INVALID_ADDRESS(1141, "Invalid address format", HttpStatus.BAD_REQUEST),
    WARD_NOT_FOUND(1142, "Ward not found", HttpStatus.NOT_FOUND),
    DISTRICT_NOT_FOUND(1143, "District not found", HttpStatus.NOT_FOUND),
    OTHER_EXCEPTIONS(1999, "System error has occurred. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int responseCode;
    private final String message;
    private final HttpStatus httpStatus;
}
