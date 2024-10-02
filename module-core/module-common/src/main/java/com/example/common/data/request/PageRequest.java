package com.example.common.data.request;

import lombok.Builder;
import lombok.Data;
import com.example.common.config.constant.PageConstant;

@Data
@Builder
public class PageRequest {
    private Integer page = PageConstant.DEFAULT_PAGE.getValue();
    private Integer size = PageConstant.DEFAULT_SIZE.getValue();
}
