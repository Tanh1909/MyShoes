package com.example.common.data.request;

import com.example.common.config.constant.PageConstant;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Data
public class PageRequest {
    private Integer page;
    private Integer size;

    public Integer getPage() {
        return ObjectUtils.isEmpty(this.page) ? PageConstant.DEFAULT_PAGE.getValue() : this.page;
    }

    public Integer getSize() {
        return ObjectUtils.isEmpty(this.size) ? PageConstant.DEFAULT_SIZE.getValue() : this.size;
    }
}
