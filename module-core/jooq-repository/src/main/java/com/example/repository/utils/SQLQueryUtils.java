package com.example.repository.utils;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.Table;

import java.util.HashMap;
import java.util.Map;

public class SQLQueryUtils {
    public static  Map<Field<?>, Object> toInsertQueries(Table table, Object o) {
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
}
