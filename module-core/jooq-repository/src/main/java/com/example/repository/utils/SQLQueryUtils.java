package com.example.repository.utils;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.pagination.Order;
import com.example.common.exception.AppException;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SortField;
import org.jooq.Table;

import java.util.*;

public class SQLQueryUtils {
    public static Map<Field<?>, Object> toInsertQueries(Table<?> table, Object o) {
        Record record = table.newRecord();
        record.from(o);
        Map<Field<?>, Object> values = new HashMap<>();
        for (Field<?> f : record.fields()) {
            if (record.getValue(f) != null) {
                values.put(f, record.getValue(f));
            }
        }
        return values;
    }

    public static List<? extends SortField<?>> getSortFields(Collection<Order> orders, Table<?> table) {
        return orders.stream()
                .map(order -> getSortField(order, table))
                .toList();
    }

    public static SortField<?> getSortField(Order order, Table<?> table) {
        String sortBy = order.getSortBy();
        String sortDirection = order.getSortDirection();
        Field<?> fieldSortBy = Arrays.stream(table.fields())
                .filter(field -> field.getName().equalsIgnoreCase(sortBy))
                .findFirst().orElseThrow(() -> new AppException(ErrorCodeBase.INVALID_SORT_BY, sortBy));
        if (Order.Direction.ASC.value().equalsIgnoreCase(sortDirection)) {
            return fieldSortBy.asc();
        } else if (Order.Direction.DESC.value().equalsIgnoreCase(sortDirection)) {
            return fieldSortBy.desc();
        } else {
            throw new AppException(ErrorCodeBase.INVALID_SORT_DIRECTION, sortDirection);
        }
    }

}
