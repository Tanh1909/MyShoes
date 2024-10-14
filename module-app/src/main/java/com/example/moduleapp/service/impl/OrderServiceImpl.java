package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.model.tables.pojos.Address;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.impl.AddressRepository;
import com.example.moduleapp.repository.impl.OrderItemRepository;
import com.example.moduleapp.repository.impl.OrderRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.OrderService;
import com.example.security.config.service.UserDetailImpl;
import com.example.security.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AddressRepository addressRepository;
    private final AuthService authService;
    private final OrderItemRepository orderItemRepository;

    @Override
    public Single<String> create(OrderRequest orderRequest) {
        Map<Integer, Integer> productVariantsReq = orderRequest.getProductVariants().stream()
                .collect(Collectors.toMap(
                        OrderRequest.ProductVariantRequest::getId,
                        OrderRequest.ProductVariantRequest::getQuantity));
        UserDetailImpl userDetail = (UserDetailImpl) authService.getCurrentUser();
        Order orderReq = new Order();
        orderReq.setUserId(userDetail.getId());
        orderReq.setAddressId(orderRequest.getAddressId());
        orderReq.setStatus(OrderEnum.PENDING.getValue());
        return Single.zip(
                        productVariantRepository.findByIdIn(productVariantsReq.keySet()),
                        addressRepository.findById(orderRequest.getAddressId()),
                        orderRepository.insertReturn(orderReq),
                        (productVariants, addressOptional, order) -> {
                            Address address = addressOptional.orElse(null);
                            validateVariant(productVariants, productVariantsReq.keySet());
                            validateAddress(orderRequest.getAddressId(), address);
                            return productVariants.stream().map(productVariant -> {
                                OrderItem orderItem = new OrderItem();
                                orderItem.setOrderId(order.getId());
                                orderItem.setProductVariantId(productVariant.getId());
                                orderItem.setPrice(productVariant.getPrice());
                                orderItem.setQuantity(productVariantsReq.get(productVariant.getId()));
                                return orderItem;
                            }).toList();
                        }
                )
                .flatMap(orderItemRepository::insertReturn)
                .map(orderItems -> "SUCCESS");
    }

    private static void validateAddress(Integer id, Address address) {
        if (address == null) {
            log.debug("address id: {} not found", id);
            throw new AppException(ErrorCodeBase.NOT_FOUND, "Address");
        }
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
