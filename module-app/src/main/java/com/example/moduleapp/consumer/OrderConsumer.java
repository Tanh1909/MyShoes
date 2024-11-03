package com.example.moduleapp.consumer;

import com.example.common.utils.JsonUtils;
import com.example.moduleapp.model.tables.pojos.Order;
import com.example.moduleapp.model.tables.pojos.OrderItem;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.impl.OrderItemRepository;
import com.example.moduleapp.repository.impl.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderItemRepository orderItemRepository;
    private final ReviewRepository reviewRepository;

    @KafkaListener(topics = "${messing.kafka.topic.order-success}", groupId = "review")
    public void createReview(String message) {
        Order order = JsonUtils.decode(message, Order.class);
        if (order == null) {
            log.error("error when consumer message: {}", message);
            return;
        }
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId()).blockingGet();
        List<Review> reviews = new ArrayList<>();
        orderItems.forEach(orderItem -> {
            Review review = new Review();
            review.setProductVariantId(orderItem.getProductVariantId());
            review.setProductId(orderItem.getProductId());
            review.setUserId(order.getUserId());
            reviews.add(review);
        });
        reviewRepository.insert(reviews).blockingGet();
    }
}
