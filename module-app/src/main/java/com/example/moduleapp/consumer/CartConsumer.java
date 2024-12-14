package com.example.moduleapp.consumer;

import com.example.common.utils.JsonUtils;
import com.example.moduleapp.data.request.RemoveCartRequest;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.repository.ICartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartConsumer {
    private final ICartRepository cartRepository;

    @KafkaListener(topics = "${messing.kafka.topic.remove-cart-request}", groupId = "review")
    public void createReview(String message) {
        try {
            RemoveCartRequest removeCartRequest = JsonUtils.decode(message, RemoveCartRequest.class);
            List<Integer> cartIds = cartRepository.findByUserIdAndProductVariantIdInBlocking(removeCartRequest.getUserId(), removeCartRequest.getProductVariantIds())
                    .stream()
                    .map(Cart::getId)
                    .toList();
            cartRepository.deleteByIdsBlocking(cartIds);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
