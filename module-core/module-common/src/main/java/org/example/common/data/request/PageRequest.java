package org.example.common.data.request;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.example.common.config.constant.PageConstant;

@Data
@Builder
public class PageRequest {
    private Integer page = PageConstant.DEFAULT_PAGE.getValue();
    private Integer size = PageConstant.DEFAULT_SIZE.getValue();
}
