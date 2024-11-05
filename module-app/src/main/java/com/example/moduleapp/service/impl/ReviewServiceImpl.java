package com.example.moduleapp.service.impl;

import com.example.common.context.SimpleSecurityUser;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.config.constant.ReviewErrorCode;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.mapper.ReviewMapper;
import com.example.moduleapp.data.request.ReviewRequest;
import com.example.moduleapp.data.response.ReviewResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.repository.impl.ImageRepository;
import com.example.moduleapp.repository.impl.ProductRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.repository.impl.ReviewRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.ReviewService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final ReviewMapper reviewMapper;

    @Override
    public Single<PageResponse<ReviewResponse>> findReview(PageRequest pageRequest, boolean isReview) {
        SimpleSecurityUser userInfo = authService.getCurrentUser().getUserInfo();
        return reviewRepository.getReviewByUserId(pageRequest, userInfo.getId(), isReview)
                .flatMap(pageResponse -> {
                    List<Review> reviews = pageResponse.getData().stream().toList();
                    List<Integer> productIds = reviews.stream().map(Review::getProductId).toList();
                    List<Integer> variantsIds = reviews.stream().map(Review::getProductVariantId).toList();
                    return Single.zip(
                            productRepository.findByIds(productIds),
                            productVariantRepository.findDetailByIdsIn(variantsIds),
                            imageRepository.findPrimaryByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                            (products, productVariantDetails, images) -> {
                                Map<Integer, Product> mapProduct = products.stream().collect(Collectors.toMap(Product::getId, product -> product));
                                Map<Integer, Image> mapImage = images.stream().collect(Collectors.toMap(Image::getTargetId, image -> image));
                                Map<Integer, ProductVariantDetail> mapPVD = productVariantDetails.stream().collect(Collectors.toMap(ProductVariantDetail::getId, pvd -> pvd, (t, t2) -> t));
                                Map<Integer, List<String>> groupValue = productVariantDetails.stream()
                                        .collect(Collectors.groupingBy(
                                                ProductVariantDetail::getId,
                                                Collectors.mapping(ProductVariantDetail::getValue, Collectors.toList())
                                        ));
                                List<ReviewResponse> results = reviews.stream().map(review -> {
                                    ReviewResponse.Product product = ReviewResponse.Product.builder()
                                            .id(review.getId())
                                            .name(mapProduct.get(review.getProductId()).getName())
                                            .price(mapPVD.get(review.getProductVariantId()).getPrice())
                                            .variants(groupValue.get(review.getProductVariantId()))
                                            .image(mapImage.get(review.getProductId()).getUrl())
                                            .build();
                                    ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
                                    reviewResponse.setProduct(product);
                                    return reviewResponse;
                                }).toList();
                                return PageResponse.<ReviewResponse>builder()
                                        .page(pageResponse.getPage())
                                        .size(pageResponse.getSize())
                                        .totalPage(pageResponse.getTotalPage())
                                        .data(results)
                                        .build();
                            }
                    );
                });
    }

    @Override
    public Single<Boolean> review(Integer reviewId, ReviewRequest reviewRequest) {
        return reviewRepository.findById(reviewId)
                .flatMap(reviewOptional -> {
                    Review review = ValidateUtils.getOptionalValue(reviewOptional, Review.class);
                    authService.validateOwner(review.getUserId());
                    if (review.getIsReview().toString().equals("1")) {
                        throw new AppException(ReviewErrorCode.HAS_REVIEWED);
                    }
                    reviewMapper.toReview(review, reviewRequest);
                    review.setIsReview(Byte.valueOf("1"));
                    return reviewRepository.update(reviewId, review);
                }).map(reviewOptional -> Boolean.TRUE);
    }
}
