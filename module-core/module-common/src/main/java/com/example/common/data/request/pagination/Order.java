package com.example.common.data.request.pagination;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
public class Order {
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIRECTION = "asc";

    private String sortBy;
    private String sortDirection;


    public Order() {
        sortBy = DEFAULT_SORT_BY;
        sortDirection = DEFAULT_SORT_DIRECTION;
    }

    public Order(String sortBy) {
        this(sortBy, DEFAULT_SORT_DIRECTION);
    }

    public Order(String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    @Getter
    @AllArgsConstructor
    public enum Direction {
        ASC("asc"), DESC("desc");
        private String value;
    }
}
