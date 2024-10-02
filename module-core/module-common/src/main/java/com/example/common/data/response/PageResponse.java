package com.example.common.data.response;

import lombok.Builder;
import lombok.Data;

import java.util.Collection;

@Data
@Builder
public class PageResponse<T> {
    private Collection<T> data;
    private Integer page;
    private Integer size;
    private Integer totalPage;
}
