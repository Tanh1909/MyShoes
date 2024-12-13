package com.example.common.data.request.pagination;

import com.example.common.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Setter;

@Setter
public class Order {
    private static final String DEFAULT_SORT_BY = "id";
    private static final String DEFAULT_SORT_DIRECTION = "asc";

    private String sortBy;
    private String sortDirection;

    public String getSortBy() {
        return StringUtils.isEmpty(sortBy) ? DEFAULT_SORT_BY : sortBy;
    }

    public String getSortDirection() {
        return StringUtils.isEmpty(sortDirection) ? DEFAULT_SORT_DIRECTION : sortDirection;
    }

    public Order() {
        sortBy = DEFAULT_SORT_BY;
        sortDirection = DEFAULT_SORT_DIRECTION;
    }

    public Order(String sortBy) {
        this(sortBy, DEFAULT_SORT_DIRECTION);
    }

    public boolean isAsc() {
        return sortDirection.equals(DEFAULT_SORT_DIRECTION);
    }

    public Order(String sortBy, String sortDirection) {
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }

    @AllArgsConstructor
    public enum Direction {
        ASC("asc"), DESC("desc");
        private final String value;

        public String value() {
            return value;
        }

    }
}
