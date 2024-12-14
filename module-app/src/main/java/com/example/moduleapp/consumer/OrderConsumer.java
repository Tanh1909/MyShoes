package com.example.moduleapp.consumer;

import com.example.common.utils.JsonUtils;
import com.example.moduleapp.config.constant.OrderItemEnum;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.impl.OrderItemRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.repository.impl.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;
    private final ProductVariantRepository productVariantRepository;


    @KafkaListener(topics = "${messing.kafka.topic.push-order-request}", groupId = "order")
    @Transactional
    public void createOrder(String message) {
        try {
            OrderItem orderItem = JsonUtils.decode(message, OrderItem.class);
            if (orderItem == null) {
                log.error("Invalid message received");
                return;
            }
            ProductVariant productVariant = productVariantRepository.findByIdBlocking(orderItem.getProductVariantId())
                    .orElse(null);
            if (productVariant == null) {
                log.error("not found product variant");
                return;
            }
            Integer stock = productVariant.getStock();
            Integer quantity = orderItem.getQuantity();
            if (stock == 0 || quantity > stock) {
                orderItem.setStatus(OrderItemEnum.CANCEL.getValue());
            } else {
                orderItem.setStatus(OrderItemEnum.SUCCESS.getValue());
                productVariant.setStock(stock - quantity);
            }
            orderItemRepository.updateByCodeBlocking(orderItem.getCode(), orderItem);
            if (OrderItemEnum.SUCCESS.getValue().equals(orderItem.getStatus())) {
                productVariantRepository.updateBlocking(productVariant.getId(), productVariant);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }


    @KafkaListener(topics = "${messing.kafka.topic.order-success}", groupId = "review")
    public void createReview(String message) {
        try {
            Order order = JsonUtils.decode(message, Order.class);
            if (order == null) {
                log.error("error when consumer message: {}", message);
                return;
            }
            List<OrderItem> orderItems = orderItemRepository.findByOrderIdBlocking(order.getId());
            List<Review> reviews = new ArrayList<>();
            orderItems.forEach(orderItem -> {
                Review review = new Review();
                review.setProductVariantId(orderItem.getProductVariantId());
                review.setProductId(orderItem.getProductId());
                review.setUserId(order.getUserId());
                reviews.add(review);
            });
            reviewRepository.insertBlocking(reviews);
        } catch (Exception e) {
            log.error("error when consumer message: {}", message);
        }

    }

}
