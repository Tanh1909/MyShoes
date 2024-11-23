package com.example.moduleapp.service.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.data.mapper.CartMapper;
import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.impl.CartRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.CartService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final AuthService authService;
    private final CartMapper cartMapper;

    @Override
    public PageResponse<Cart> findByUser(PageRequest pageRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();

        return null;
    }

    @Override
    public Single<String> addToCart(CartRequest cartRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        Cart cart = cartMapper.toCart(cartRequest);
        cart.setUserId(userId.longValue());
        return productVariantRepository.findById(cartRequest.getProductVariantId())
                .flatMap(productVariantOptional -> {
                    ProductVariant productVariant = ValidateUtils.getOptionalValue(productVariantOptional, ProductVariant.class);
                    cart.setProductVariantId(productVariant.getId());
                    cart.setProductId(productVariant.getProductId());
                    return cartRepository.insert(cart).map(integer -> "SUCCESS");
                });
    }
}
