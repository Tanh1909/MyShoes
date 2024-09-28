package org.example.common.data.response.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}