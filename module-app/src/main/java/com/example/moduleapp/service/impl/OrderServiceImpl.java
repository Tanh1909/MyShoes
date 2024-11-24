package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.context.UserPrincipal;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.common.utils.JsonUtils;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.config.constant.OrderEnum;
import com.example.moduleapp.config.constant.OrderErrorCode;
import com.example.moduleapp.config.constant.PaymentErrorCode;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.mapper.AddressMapper;
import com.example.moduleapp.data.mapper.OrderItemMapper;
import com.example.moduleapp.data.mapper.OrderMapper;
import com.example.moduleapp.data.request.OrderRequest;
import com.example.moduleapp.data.request.OrderStatusRequest;
import com.example.moduleapp.data.response.OrderItemResponse;
import com.example.moduleapp.data.response.OrderResponse;
import com.example.moduleapp.model.tables.pojos.*;
import com.example.moduleapp.repository.impl.*;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.OrderService;
import com.example.moduleapp.service.ProductVariantService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.common.utils.ValidateUtils.getOptionalValue;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final AddressRepository addressRepository;
    private final OrderItemRepository orderItemRepository;
    private final AuthService authService;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductVariantService productVariantService;
    private final ImageRepository imageRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final AddressMapper addressMapper;
    private final PaymentRepository paymentRepository;


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
                            orderReq.setTotalAmount(total);
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
    @Transactional
    public Single<String> updateStatus(OrderStatusRequest orderStatusRequest) {
        Optional<Order> orderOptional = orderRepository.findByIdBlocking(orderStatusRequest.getOrderId());
        Order order = getOptionalValue(orderOptional, Order.class);
        OrderEnum orderEnum = OrderEnum.getValue(orderStatusRequest.getStatus());
        switch (orderEnum) {
            case SUCCESS: {
                validateAndUpdateBusinessOrderStatus(OrderEnum.PAYMENT_CONFIRM, OrderEnum.SUCCESS, order);
                Boolean isPaid = paymentRepository.findPaymentSuccessBlocking(order.getId());
                if (!isPaid) {
                    throw new AppException(PaymentErrorCode.ORDER_HAS_NOT_BEEN_PAYED);
                }
                kafkaTemplate.send(oderSuccessTopic, JsonUtils.encode(order));
                break;
            }
            case CANCEL:
                validateAndUpdateBusinessOrderStatus(OrderEnum.PENDING, OrderEnum.CANCEL, order);
            case REFUND:
                validateAndUpdateBusinessOrderStatus(OrderEnum.SUCCESS, OrderEnum.REFUND, order);
            default:
                throw new AppException(ErrorCodeBase.NOT_FOUND, "ERROR STATUS");
        }
        return Single.just("SUCCESS");
    }

    private void validateAndUpdateBusinessOrderStatus(OrderEnum beforeStatus, OrderEnum afterStatus, Order order) {
        if (order.getStatus().equals(beforeStatus.getValue())) {
            order.setStatus(afterStatus.getValue());
        } else {
            throw new AppException(OrderErrorCode.WRONG_BUSINESS_UPDATE_STATUS);
        }
        orderRepository.updateBlocking(order.getId(), order);
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

    @Override
    public Single<PageResponse<OrderResponse>> getOrderResponseByStatus(String status, PageRequest pageRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        OrderEnum.getValue(status);
        return orderRepository.findByUserIdAndStatus(userId, status, pageRequest)
                .flatMap(orderPageResponse -> {
                    List<Order> orders = orderPageResponse.getData().stream().toList();
                    List<Integer> addressIds = orders.stream().map(Order::getAddressId).toList();
                    List<Integer> orderIds = orders.stream().map(Order::getId).toList();
                    return Single.zip(
                                    orderItemRepository.findByOrderIdIn(orderIds),
                                    addressRepository.findByIds(addressIds),
                                    (Pair::of)
                            )
                            .flatMap(pair -> {
                                        List<OrderItem> orderItems = pair.getLeft();
                                        List<Integer> productIds = orderItems.stream().map(OrderItem::getProductId).toList();
                                        List<Integer> variantIds = orderItems.stream().map(OrderItem::getProductVariantId).toList();
                                        return Single.zip(
                                                productRepository.findByIds(productIds),
                                                productVariantService.findDetailsByIdIn(variantIds),
                                                imageRepository.findPrimaryByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                                                (products, productVariantDetails, images) -> {
                                                    Map<Integer, Product> mapProduct = products.stream()
                                                            .collect(Collectors.toMap(Product::getId, o -> o));
                                                    Map<Integer, ProductVariantDetail> mapPVD = productVariantDetails.stream()
                                                            .collect(Collectors.toMap(ProductVariantDetail::getId, o -> o));
                                                    Map<Integer, String> mapImage = images.stream()
                                                            .collect(Collectors.toMap(Image::getTargetId, Image::getUrl, (s, s2) -> s));
                                                    Map<Integer, OrderResponse.AddressResponse> mapAddressResponse = pair.getRight().stream()
                                                            .map(addressMapper::toAddressOrderResponse)
                                                            .collect(Collectors.toMap(OrderResponse.AddressResponse::getId, o -> o));
                                                    Map<Integer, List<OrderItemResponse>> groupByOrder = orderItems.stream()
                                                            .collect(Collectors.groupingBy(
                                                                    OrderItem::getOrderId,
                                                                    Collectors.mapping(
                                                                            orderItem -> {
                                                                                Product product = mapProduct.getOrDefault(orderItem.getProductId(), new Product());
                                                                                ProductVariantDetail productVariantDetail = mapPVD.getOrDefault(orderItem.getProductVariantId(), new ProductVariantDetail());
                                                                                String image = mapImage.getOrDefault(orderItem.getProductId(), "");
                                                                                OrderItemResponse orderItemResponse = orderItemMapper.toOrderItemResponse(orderItem);
                                                                                orderItemResponse.setAttributes(productVariantDetail.getAttributes());
                                                                                orderItemResponse.setName(product.getName());
                                                                                orderItemResponse.setImageUrl(image);
                                                                                return orderItemResponse;
                                                                            }, Collectors.toList()
                                                                    )
                                                            ));
                                                    List<OrderResponse> orderResponses = orders.stream()
                                                            .map(order -> {
                                                                OrderResponse orderResponse = orderMapper.toOrderResponse(order);
                                                                orderResponse.setItems(groupByOrder.get(order.getId()));
                                                                orderResponse.setShippingAddress(mapAddressResponse.get(order.getAddressId()));
                                                                return orderResponse;
                                                            })
                                                            .toList();
                                                    return PageResponse.<OrderResponse>builder()
                                                            .data(orderResponses)
                                                            .page(orderPageResponse.getPage())
                                                            .size(orderPageResponse.getSize())
                                                            .totalPage(orderPageResponse.getTotalPage())
                                                            .build();

                                                }
                                        );
                                    }
                            );
                });
    }
}
