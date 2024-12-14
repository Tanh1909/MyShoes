package com.example.moduleapp.service.impl;

import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.AppErrorCode;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.mapper.CartMapper;
import com.example.moduleapp.data.request.CartRequest;
import com.example.moduleapp.data.request.CartUpdateRequest;
import com.example.moduleapp.data.response.CartResponse;
import com.example.moduleapp.model.tables.pojos.Cart;
import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.impl.CartRepository;
import com.example.moduleapp.repository.impl.ImageRepository;
import com.example.moduleapp.repository.impl.ProductRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.CartService;
import com.example.moduleapp.service.ProductVariantService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantService productVariantService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final AuthService authService;
    private final CartMapper cartMapper;

    @Override
    public Single<PageResponse<CartResponse>> findByUser(PageRequest pageRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        return cartRepository.findByUserId(userId.longValue(), pageRequest)
                .flatMap(cartPageResponse -> {
                    List<Cart> carts = cartPageResponse.getData().stream().toList();
                    List<Integer> productIds = carts.stream().map(Cart::getProductId).toList();
                    Set<Integer> productVariantIds = carts.stream().map(Cart::getProductVariantId).collect(Collectors.toSet());
                    return Single.zip(
                            productRepository.findByIds(productIds),
                            productVariantService.findDetailsByProductIdIn(productIds),
                            imageRepository.findPrimaryByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                            (products, productVariantDetails, images) -> {
                                Map<Integer, String> mapImages = images.stream().collect(Collectors.toMap(
                                        Image::getTargetId,
                                        Image::getUrl,
                                        (s, s2) -> s
                                ));
                                Map<Integer, Product> mapProduct = products.stream().collect(Collectors.toMap(
                                        Product::getId,
                                        o -> o
                                ));
                                Map<Integer, List<CartResponse.ProductVariant>> mapProductCart = productVariantDetails.stream()
                                        .collect(Collectors.groupingBy(
                                                ProductVariantDetail::getProductId,
                                                Collectors.mapping(productVariantDetail -> {
                                                            Product product = mapProduct.getOrDefault(productVariantDetail.getProductId(), new Product());
                                                            CartResponse.ProductVariant cartProductVariant = cartMapper.toCartProductVariant(productVariantDetail);
                                                            cartProductVariant.setName(product.getName());
                                                            cartProductVariant.setImageUrl(mapImages.getOrDefault(productVariantDetail.getProductId(), ""));
                                                            return cartProductVariant;
                                                        }, Collectors.toList()
                                                )));
                                Map<Integer, CartResponse.ProductVariant> mapProductVariant = mapProductCart.values().stream()
                                        .flatMap(Collection::stream)
                                        .filter(productVariant -> productVariantIds.contains(productVariant.getId()))
                                        .collect(Collectors.toMap(CartResponse.ProductVariant::getId, productVariant -> productVariant));

                                List<CartResponse> cartResponses = carts.stream().map(cart -> {
                                            CartResponse cartResponse = cartMapper.toCartResponse(cart);
                                            cartResponse.setProductVariant(mapProductVariant.get(cart.getProductVariantId()));
                                            cartResponse.setOptions(mapProductCart.get(cart.getProductId()));
                                            return cartResponse;
                                        })
                                        .filter(cartResponse -> !ObjectUtils.isEmpty(cartResponse.getProductVariant()))
                                        .toList();
                                return PageResponse.toPageResponse(cartResponses, cartPageResponse);
                            }
                    );
                });
    }

    @Override
    public Single<String> addToCart(Integer productVariantId, CartRequest cartRequest) {
        Integer userId = authService.getCurrentUser().getUserInfo().getId();
        return productVariantRepository.findById(productVariantId)
                .flatMap(productVariantOptional -> {
                    ProductVariant productVariant = ValidateUtils.getOptionalValue(productVariantOptional, ProductVariant.class);
                    Cart cart = new Cart();
                    cart.setUserId(userId.longValue());
                    cart.setProductVariantId(productVariant.getId());
                    cart.setProductId(productVariant.getProductId());
                    cart.setQuantity(cartRequest.getQuantity());
                    return cartRepository.insertOrUpdate(cart,cartRequest.getQuantity(), productVariant.getStock())
                            .map(integer -> "SUCCESS");
                });
    }

    @Override
    public Single<String> deleteFromCart(Integer cartId) {
        return cartRepository.findById(cartId)
                .flatMap(cartOptional -> {
                    ValidateUtils.getOptionalValue(cartOptional, Cart.class);
                    return cartRepository.deleteById(cartId)
                            .map(integer -> "SUCCESS");
                });
    }

    @Override
    public Single<String> updateCart(Integer cartId, CartUpdateRequest cartUpdateRequest) {
        return Single.zip(
                        cartRepository.findById(cartId),
                        productVariantRepository.findById(cartUpdateRequest.getProductVariantId()),
                        (cartOptional, productVariantOptional) -> {
                            Cart cart = ValidateUtils.getOptionalValue(cartOptional, Cart.class);
                            ProductVariant productVariant = ValidateUtils.getOptionalValue(productVariantOptional, ProductVariant.class);
                            if (cartUpdateRequest.getQuantity() > productVariant.getStock()) {
                                throw new AppException(AppErrorCode.OVER_STOCK);
                            }
                            cartMapper.toCart(cart, cartUpdateRequest);
                            return cart;
                        }
                )
                .flatMap(cart -> cartRepository.update(cartId, cart))
                .map(integer -> "SUCCESS");
    }
}
