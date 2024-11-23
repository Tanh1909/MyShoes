package com.example.common.data.request.pagination;

import lombok.Data;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class PageRequest {
    private static final int DEFAULT_PAGE_SIZE = 0;
    private static final int DEFAULT_PAGE_NO = 7;
    private Integer page;
    private Integer size;
    private List<Order> orders;

    public PageRequest() {
        this.page = DEFAULT_PAGE_NO;
        this.size = DEFAULT_PAGE_SIZE;
        this.orders = new ArrayList<>();
    }

    public PageRequest(Integer page) {
        this();
        if (page == null || page < 0) {
            this.page = DEFAULT_PAGE_NO;
        } else {
            this.page = page;
        }
    }

    public PageRequest(Integer page, Integer size) {
        this(page);
        if (size == null || size < 0) {
            this.size = DEFAULT_PAGE_SIZE;
        } else {
            this.size = size;
        }
    }

    public PageRequest(Integer page, Integer size, Order... orders) {
        this(page, size);
        this.orders.addAll(Arrays.stream(orders).toList());
    }

    public Integer getOffset() {
        return this.getPage() * this.getSize();
    }

    public Integer getPage() {
        return ObjectUtils.isEmpty(this.page) ? DEFAULT_PAGE_NO : this.page;
    }

    public Integer getSize() {
        return ObjectUtils.isEmpty(this.size) ? DEFAULT_PAGE_SIZE : this.size;
    }
}
