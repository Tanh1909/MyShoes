package com.example.common.config.webMVC;

import com.example.common.annotation.Pageable;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.pagination.Order;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.exception.AppException;
import com.example.common.utils.ParseUtils;
import com.example.common.utils.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaginationParseResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Pageable.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        Integer page = ParseUtils.parseInt(webRequest.getParameter("page"));
        Integer size = ParseUtils.parseInt(webRequest.getParameter("size"));
        List<Order> sortBy = getOrder(webRequest.getParameter("sortBy"));
        return new PageRequest(page, size, sortBy);
    }

    private static List<Order> getOrder(String sortByParam) {
        if (sortByParam == null) {
            return new ArrayList<>();
        }
        List<Order> orders = new ArrayList<>();
        String[] sortByArray = sortByParam.split(",");
        for (String sort : sortByArray) {
            if (StringUtils.isEmpty(sort)) {
                continue;
            }
            String[] parts = sort.split("_");
            if (parts.length == 1) {
                Order order = new Order();
                order.setSortBy(parts[0]);
                orders.add(order);

            } else if (parts.length == 2) {
                Order order = new Order();
                order.setSortBy(parts[0]);  // "name"
                order.setSortDirection(parts[1]);  // "asc"
                orders.add(order);
            } else {
                throw new AppException(ErrorCodeBase.INVALID_SORT_PARAMETER, sortByParam);
            }
        }
        return orders;
    }
}
