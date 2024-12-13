package com.example.moduleapp.service.impl;

import com.example.common.context.SimpleSecurityUser;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.AppErrorCode;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.mapper.ReviewMapper;
import com.example.moduleapp.data.mapper.UserMapper;
import com.example.moduleapp.data.request.ReviewRequest;
import com.example.moduleapp.data.response.ReviewResponse;
import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.model.tables.pojos.Review;
import com.example.moduleapp.model.tables.pojos.User;
import com.example.moduleapp.repository.impl.ImageRepository;
import com.example.moduleapp.repository.impl.ProductRepository;
import com.example.moduleapp.repository.impl.ReviewRepository;
import com.example.moduleapp.repository.impl.UserRepository;
import com.example.moduleapp.service.AuthService;
import com.example.moduleapp.service.ProductVariantService;
import com.example.moduleapp.service.ReviewService;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AuthService authService;
    private final ProductVariantService productVariantService;
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;

    @Override
    public Single<PageResponse<ReviewResponse>> findReview(PageRequest pageRequest, boolean isReview) {
        SimpleSecurityUser userInfo = authService.getCurrentUser().getUserInfo();
        Integer userId = userInfo.getId();
        return Single.zip(
                        reviewRepository.getReviewByUserId(pageRequest, userId, isReview),
                        userRepository.findById(userId),
                        Pair::of
                )
                .flatMap(pair -> {
                    PageResponse<Review> pageResponse = pair.getLeft();
                    User user = ValidateUtils.getOptionalValue(pair.getRight(), User.class);
                    List<Review> reviews = pageResponse.getData().stream().toList();
                    List<Integer> productIds = reviews.stream().map(Review::getProductId).toList();
                    List<Integer> variantsIds = reviews.stream().map(Review::getProductVariantId).toList();
                    return Single.zip(
                            productRepository.findByIds(productIds),
                            productVariantService.findDetailsByIdIn(variantsIds),
                            imageRepository.findPrimaryByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                            (products, productVariantDetails, images) -> {
                                Map<Integer, Product> mapProduct = products.stream().collect(Collectors.toMap(Product::getId, product -> product));
                                Map<Integer, Image> mapImage = images.stream().collect(Collectors.toMap(Image::getTargetId, image -> image));
                                Map<Integer, ProductVariantDetail> mapPVD = productVariantDetails.stream().collect(Collectors.toMap(ProductVariantDetail::getId, pvd -> pvd, (t, t2) -> t));
                                List<ReviewResponse> results = reviews.stream().map(review -> {
                                    Product productReview = mapProduct.get(review.getProductId());
                                    ProductVariantDetail productVariantDetail = mapPVD.get(review.getProductVariantId());
                                    Image image = mapImage.get(review.getProductId());
                                    ReviewResponse.Product product = ReviewResponse.Product.builder()
                                            .id(productReview.getId())
                                            .name(productReview.getName())
                                            .price(productVariantDetail == null ? 0 : productVariantDetail.getPrice())
                                            .variants(productVariantDetail.getAttributes())
                                            .image(image == null ? "" : image.getUrl())
                                            .build();
                                    ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
                                    reviewResponse.setProduct(product);
                                    reviewResponse.setUser(userMapper.toUserResponse(user));
                                    return reviewResponse;
                                }).toList();
                                return PageResponse.toPageResponse(results, pageResponse);
                            }
                    );
                });
    }

    @Override
    public Single<PageResponse<ReviewResponse>> findReviewByProductId(PageRequest pageRequest, Integer productId) {
        return reviewRepository.getReviewByProductId(pageRequest, productId, true)
                .flatMap(pageResponse -> {
                    List<Review> reviews = pageResponse.getData().stream().toList();
                    List<Integer> reviewIds = reviews.stream().map(Review::getId).toList();
                    List<Integer> userIds = reviews.stream().map(review -> review.getUserId().intValue()).toList();
                    List<Integer> variantsIds = reviews.stream().map(Review::getProductVariantId).toList();
                    return Single.zip(
                            userRepository.findByIds(userIds),
                            productVariantService.findDetailsByIdIn(variantsIds),
                            productRepository.findById(productId),
                            imageRepository.findPrimaryByTargetIdAndType(productId, ImageEnum.PRODUCT.getValue()),
                            imageRepository.findByTargetIdInAndType(reviewIds, ImageEnum.REVIEW.getValue()),
                            (users, productVariantDetails, productOptional, imageOptional, imageReviews) -> {
                                Product product = ValidateUtils.getOptionalValue(productOptional, Product.class);
                                Map<Long, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));
                                Map<Integer, ProductVariantDetail> mapPVD = productVariantDetails.stream().collect(Collectors.toMap(ProductVariantDetail::getId, pvd -> pvd, (t, t2) -> t));
                                Map<Integer, List<String>> groupByReviewIds = imageReviews.stream().collect(Collectors.groupingBy(Image::getTargetId, Collectors.mapping(Image::getUrl, Collectors.toList())));
                                List<ReviewResponse> results = reviews.stream().map(review -> {
                                    ProductVariantDetail productVariantDetail = mapPVD.get(review.getProductVariantId());
                                    User user = userMap.getOrDefault(review.getUserId(), new User());
                                    ReviewResponse.Product productResponse = ReviewResponse.Product.builder()
                                            .id(productId)
                                            .name(product.getName())
                                            .price(productVariantDetail == null ? 0 : productVariantDetail.getPrice())
                                            .variants(productVariantDetail.getAttributes())
                                            .image(imageOptional.isEmpty() ? "" : imageOptional.get().getUrl())
                                            .build();
                                    ReviewResponse reviewResponse = reviewMapper.toReviewResponse(review);
                                    reviewResponse.setProduct(productResponse);
                                    reviewResponse.setUser(userMapper.toUserResponse(user));
                                    reviewResponse.setImageUrls(groupByReviewIds.get(review.getId()));
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
        return Single.zip(
                reviewRepository.findById(reviewId),
                imageRepository.findByIds(reviewRequest.getImageIds()),
                Pair::of
        ).flatMap(pair -> {
            Review review = ValidateUtils.getOptionalValue(pair.getLeft(), Review.class);
            authService.validateOwner(review.getUserId());
            if (review.getIsReview().toString().equals("1")) {
                throw new AppException(AppErrorCode.HAS_REVIEWED);
            }
            reviewMapper.toReview(review, reviewRequest);
            review.setIsReview(Byte.valueOf("1"));
            review.setReviewedAt(LocalDateTime.now());
            List<Image> imageReq = pair.getRight().stream().peek(image -> {
                image.setType(ImageEnum.REVIEW.getValue());
                image.setTargetId(reviewId);
            }).toList();
            return Single.zip(
                    reviewRepository.update(reviewId, review),
                    imageRepository.insertUpdateOnDuplicateKey(imageReq),
                    (reviewOptional, integers) -> true
            );
        });
    }
}
