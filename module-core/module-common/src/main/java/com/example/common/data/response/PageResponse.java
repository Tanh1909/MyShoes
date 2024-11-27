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
    private Integer totalElements;
    private Boolean isLoadMore;


    public static <T> PageResponse<T> toPageResponse(Collection<T> data, Integer page, Integer size, Integer totalPage, Integer totalElements) {
        return PageResponse.<T>builder()
                .data(data)
                .page(page)
                .size(size)
                .totalPage(totalPage)
                .totalElements(totalElements)
                .isLoadMore(page < totalPage - 1)
                .build();
    }


    public static <T> PageResponse<T> toPageResponse(Collection<T> data, PageResponse<?> pageResponse) {
        return PageResponse.<T>builder()
                .data(data)
                .page(pageResponse.getPage())
                .size(pageResponse.getSize())
                .totalPage(pageResponse.getTotalPage())
                .totalElements(pageResponse.getTotalElements())
                .isLoadMore(pageResponse.getIsLoadMore())
                .build();
    }
}
