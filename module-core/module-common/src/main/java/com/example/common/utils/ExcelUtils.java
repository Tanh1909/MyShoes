package com.example.common.utils;

import com.example.common.annotation.ExcelColumn;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Log4j2
public class ExcelUtils {

    public static <T> Map<Integer, T> mapToObject(Class<T> clazz, MultipartFile file, String sheetName, Integer headerRowIndex) {
        Workbook workbook = getSheets(file);
        if (workbook == null) return null;
        Sheet sheet = workbook.getSheet(sheetName);
        Map<Integer, T> result = new HashMap<>();
        if (sheet == null) {
            log.debug("not found sheet name: {}", sheetName);
            return null;
        }
        return handleMapToObject(clazz, headerRowIndex, sheet, result);
    }

    public static <T> Map<Integer, T> mapToObject(Class<T> clazz, MultipartFile file, Integer sheetIndex, Integer headerRowIndex) {
        Workbook workbook = getSheets(file);
        if (workbook == null) return null;
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        Map<Integer, T> result = new HashMap<>();
        int totalNumberOfSheets = workbook.getNumberOfSheets();
        if (sheetIndex > totalNumberOfSheets - 1 || sheetIndex < 0) {
            log.debug("sheet index is out of range: {} out of {}", sheetIndex, totalNumberOfSheets);
            return null;
        }
        return handleMapToObject(clazz, headerRowIndex, sheet, result);
    }


    private static <T> Map<Integer, T> handleMapToObject(Class<T> clazz, Integer headerRowIndex, Sheet sheet, Map<Integer, T> result) {
        Iterator<Row> rowIterator = sheet.rowIterator();
        while (headerRowIndex > 0) {
            rowIterator.next();
            headerRowIndex -= 1;
        }
        Row headerRow = rowIterator.hasNext() ? rowIterator.next() : null;
        if (headerRow == null) {
            return result;
        }
        Map<String, Integer> indexColumn = new HashMap<>();
        headerRow.forEach(cell -> {
            int columnIndex = cell.getColumnIndex();
            indexColumn.put(cell.getStringCellValue(), columnIndex);
        });
        Map<Integer, Method> mapMethod = new HashMap<>();
        Arrays.stream(clazz.getDeclaredFields())
                .forEach(field -> {
                    if (field.isAnnotationPresent(ExcelColumn.class)) {
                        try {
                            ExcelColumn excelColumn = field.getDeclaredAnnotation(ExcelColumn.class);
                            String methodName = "set" + capString(field.getName());
                            Method method = clazz.getDeclaredMethod(methodName, field.getType());
                            if (indexColumn.get(excelColumn.name()) != null) {
                                mapMethod.put(indexColumn.get(excelColumn.name()), method);
                            }
                        } catch (NoSuchMethodException e) {
                            log.error("not found method set{}", capString(field.getName()));
                        }
                    }
                });
        rowIterator.forEachRemaining(cells -> {
            try {
                T obj = clazz.newInstance();
                cells.cellIterator().forEachRemaining(cell -> {
                    Method method = mapMethod.get(cell.getColumnIndex());
                    if (method != null) {
                        setValue(method, cell, obj);
                    }
                });
                result.put(cells.getRowNum(), obj);
            } catch (Exception e) {
                log.error("error when create new instance {}", e.getMessage());
            }
        });
        return result;
    }

    private static void setValue(Method method, Cell cell, Object o) {
        Class<?> paramType = method.getParameterTypes()[0];
        try {
            if (String.class.equals(paramType)) {
                method.invoke(o, cell.getStringCellValue());
            } else if (Double.class.equals(paramType)) {
                method.invoke(o, Double.valueOf(cell.getNumericCellValue()));
            } else if (Integer.class.equals(paramType)) {
                method.invoke(o, Double.valueOf(cell.getNumericCellValue()).intValue());
            }
        } catch (IllegalAccessException e) {
            log.error("error when set value: {}", e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("error when set value: {}", e.getMessage());
        } catch (Exception e) {
            log.error("some thing wrong when set vale: {}", e.getMessage());
        }

    }

    private static String capString(String value) {
        if (StringUtils.isEmpty(value)) {
            return "";
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    private static Workbook getSheets(MultipartFile file) {
        Workbook workbook = null;
        String fileName = file.getOriginalFilename();
        try {
            if (fileName.endsWith("xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith("xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            }
        } catch (IOException e) {
            log.error("have error when get InputStream: {}", e.getMessage());
            return null;
        }
        return workbook;
    }
}
