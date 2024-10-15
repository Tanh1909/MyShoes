package com.example.common.data.response;

import com.example.common.exception.ErrorCode;
import com.example.common.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;

import java.io.IOException;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Log4j2
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse(200, "SUCCESS", data);
    }

    public static ApiResponse error(int code, String message) {
        return new ApiResponse(code, message, null);
    }

    public static <T> void writeResponseError(HttpServletResponse response, ErrorCode errorCode) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(errorCode.getCode());
        ApiResponse apiError = ApiResponse.error(errorCode.getCode(), errorCode.getMessage());
        try {
            response.getWriter().write(JsonUtils.toString(apiError));
            response.flushBuffer();
        } catch (IOException e) {
            log.debug("Error writing response: {}", e.getMessage());

        }
    }
}
