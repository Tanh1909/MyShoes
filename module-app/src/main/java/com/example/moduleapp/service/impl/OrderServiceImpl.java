package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.context.UserPrincipal;
import com.example.common.exception.AppException;
import com.example.common.utils.JsonUtils;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.config.constant.OrderErrorCode;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.impl.AddressRepository;
import com.example.moduleapp.repository.impl.OrderItemRepository;
import com.example.moduleapp.repository.impl.OrderRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.OrderService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.utils.ValidateUtils.getOptionalValue;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    @Value("${messing.kafka.topic.order-success}")
    private String oderSuccessTopic;

    @Transactional
    @Override
    public Single<String> create(OrderRequest orderRequest) {
        Map<Integer, Integer> productVariantsReq = orderRequest.getProductVariants().stream()
                .collect(Collectors.toMap(
                        OrderRequest.ProductVariantRequest::getId,
                        OrderRequest.ProductVariantRequest::getQuantity));
        UserPrincipal user = authService.getCurrentUser();
        Order orderReq = new Order();
        orderReq.setUserId(user.getUserInfo().getId().longValue());
        orderReq.setStatus(OrderEnum.PENDING.getValue());
        return Single.zip(
                        productVariantRepository.findByIdIn(productVariantsReq.keySet()),
                        addressRepository.findById(orderRequest.getAddressId()),
                        (productVariants, addressOptional) -> {
                            Address address = getOptionalValue(addressOptional, Address.class);
                            validateVariant(productVariants, productVariantsReq.keySet());
                            BigDecimal total = productVariants.stream().map(productVariant -> productVariant.getPrice()
                                            .multiply(new BigDecimal(productVariantsReq.get(productVariant.getId()))))
                                    .reduce(BigDecimal.ZERO, BigDecimal::add);
                            orderReq.setTotalAmout(total);
                            orderReq.setAddressId(address.getId());
                            Order order = orderRepository.insertReturnBlocking(orderReq);
                            List<OrderItem> orderItems = productVariants.stream().map(productVariant -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setOrderId(order.getId());
                                orderItem.setProductVariantId(productVariant.getId());
                                orderItem.setProductId(productVariant.getProductId());
                                orderItem.setPrice(productVariant.getPrice());
                                orderItem.setQuantity(productVariantsReq.get(productVariant.getId()));
                                return orderItem;
                            }).toList();
                            return orderItemRepository.insertReturnBlocking(orderItems);
                        }
                )
                .map(orderItems -> "SUCCESS");
    }

    @Override
    public Single<String> updateStatus(OrderStatusRequest orderStatusRequest) {
        return orderRepository.findById(orderStatusRequest.getOrderId())
                .flatMap(orderOptional -> {
                    Order order = getOptionalValue(orderOptional, Order.class);
                    OrderEnum orderEnum = OrderEnum.getValue(orderStatusRequest.getStatus());
                    switch (orderEnum) {
                        case SUCCESS: {
                            Single<Integer> updateStatus = validateAndUpdateBusinessOrderStatus(OrderEnum.PAYMENT_CONFIRM, OrderEnum.SUCCESS, order);
                            kafkaTemplate.send(oderSuccessTopic, JsonUtils.encode(order));
                            return updateStatus;
                        }
                        case CANCEL:
                            return validateAndUpdateBusinessOrderStatus(OrderEnum.PENDING, OrderEnum.CANCEL, order);
                        case REFUND:
                            return validateAndUpdateBusinessOrderStatus(OrderEnum.SUCCESS, OrderEnum.REFUND, order);
                        default:
                            throw new AppException(ErrorCodeBase.NOT_FOUND, "ERROR STATUS");
                    }
                }).map(order -> "SUCCESS");
    }

    private Single<Integer> validateAndUpdateBusinessOrderStatus(OrderEnum beforeStatus, OrderEnum afterStatus, Order order) {
        if (order.getStatus().equals(beforeStatus.getValue())) {
            order.setStatus(afterStatus.getValue());
        } else {
            throw new AppException(OrderErrorCode.WRONG_BUSINESS_UPDATE_STATUS);
        }
        return orderRepository.update(order.getId(), order);
    }


    private static void validateVariant(List<ProductVariant> productVariants, Set<Integer> variantReqIds) {
        Set<Integer> variantIds = productVariants.stream().map(ProductVariant::getId).collect(Collectors.toSet());
        variantReqIds.forEach(id -> {
            if (!variantIds.contains(id)) {
                log.debug("Product variant id: {} not found", id);
                throw new AppException(ErrorCodeBase.NOT_FOUND, "Product");
            }
        });
    }
}
