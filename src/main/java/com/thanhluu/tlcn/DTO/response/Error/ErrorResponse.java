package com.thanhluu.tlcn.DTO.response.Error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thanhluu.tlcn.Enum.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private ErrorCode errorCode;
    private String path;
    private LocalDateTime timestamp;
    private String debugMessage;
}
