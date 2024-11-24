package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.exception.AppException;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.mapper.ProductAttributeMapper;
import com.example.moduleapp.data.mapper.ProductMapper;
import com.example.moduleapp.data.mapper.ProductVariantMapper;
import com.example.moduleapp.data.request.ImageRequest;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductDetailResponse;
import com.example.moduleapp.data.response.ProductResponse;
import com.example.moduleapp.model.tables.pojos.*;
import com.example.moduleapp.repository.impl.*;
import com.example.moduleapp.service.ProductService;
import com.example.moduleapp.service.ProductVariantService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ROUND_HALF_UP;

@Service
@AllArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final ProductMapper productMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductVariantMapper productVariantMapper;
    private final ProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeOptionRepository productAttributeOptionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeOptionRepository productVariantAttributeOptionRepository;
    private final ImageRepository imageRepository;
    private final ReviewRepository reviewRepository;
    private final ProductVariantService productVariantService;

    private static final Integer SKU_CODE_LENGTH = 8;

    @Transactional
    @Override
    public Single<String> create(ProductRequest productRequest) {
        Map<Integer, ImageRequest> mapImageReq = productRequest.getImages().stream()
                .collect(Collectors.toMap(ImageRequest::getId, img -> img));
        productRequest.getVariants().forEach(variantsRequest -> variantsRequest.setSkuCode(generateSkuCode(SKU_CODE_LENGTH)));
        Product productResult = productRepository.insertReturnBlocking(productMapper.toProduct(productRequest));
        List<ProductAttribute> productAttributesResult = productAttributeRepository.insertAndFindBlocking(productAttributeMapper.toProductAttribute(productRequest.getAttributes()));
        List<Image> imageResult = imageRepository.findByIdsBlocking(mapImageReq.keySet());
        validateImage(imageResult, mapImageReq.keySet());
        List<Image> imageReq = imageResult.stream().map(image -> {
            image.setType(ImageEnum.PRODUCT.getValue());
            image.setTargetId(productResult.getId());
            if (mapImageReq.get(image.getId()).getIsPrimary()) image.setIsPrimary(Byte.valueOf("1"));
            return image;
        }).toList();
        Map<String, Integer> mapReq = productAttributesResult.stream()
                .collect(Collectors.toMap(ProductAttribute::getName, ProductAttribute::getId));
        List<ProductAttributeOption> productAttributeOptions = productRequest.getAttributes().stream()
                .flatMap(attributeRequest -> attributeRequest.getOptions().stream()
                        .map(s -> new ProductAttributeOption()
                                .setValue(s)
                                .setProductAttributeId(mapReq.get(attributeRequest.getName()))
                        )).toList();
        List<ProductVariant> productVariantReq = productVariantMapper.toProductVariant(productRequest.getVariants()).stream()
                .map(productVariant -> productVariant.setProductId(productResult.getId()))
                .toList();

        List<ProductVariant> productVariantsResult = productVariantRepository.insertAndFindBlocking(productVariantReq);
        List<ProductAttributeOption> productAttributeOptionsResult = productAttributeOptionRepository.insertAndFindBlocking(productAttributeOptions, mapReq.values());
        imageRepository.insertUpdateOnDuplicateKeyBlocking(imageReq);
        Map<String, Integer> valueAndId = productAttributeOptionsResult.stream()
                .collect(Collectors.toMap(
                        ProductAttributeOption::getValue,
                        ProductAttributeOption::getId
                ));
        Map<String, Integer> skuCodeAndId = productVariantsResult.stream()
                .collect(Collectors.toMap(ProductVariant::getSkuCode, ProductVariant::getId));
        List<ProductVariantsAttributeOption> pvaos = new ArrayList<>();
        productRequest.getVariants().forEach(variantsRequest -> {
            variantsRequest.getAttributeOptions().forEach(attributeOptionRequest -> {
                ProductVariantsAttributeOption pvao = new ProductVariantsAttributeOption();
                pvao.setVariantId(skuCodeAndId.get(variantsRequest.getSkuCode()));
                pvao.setProductAttributeOptionId(valueAndId.get(attributeOptionRequest));
                pvaos.add(pvao);
            });
        });
        productVariantAttributeOptionRepository.insertBlocking(pvaos);
        return Single.just("SUCCESS");
    }

    public static String generateSkuCode(int length) {
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        SecureRandom random = new SecureRandom();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmm");
        String timestamp = LocalDateTime.now().format(formatter);
        StringBuilder randomPart = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            randomPart.append(CHARACTERS.charAt(index));
        }
        return timestamp + "-" + randomPart.toString();
    }

    private static void validateImage(List<Image> images, Set<Integer> imageIds) {
        Set<Integer> imageIdsResult = images.stream().map(Image::getId).collect(Collectors.toSet());
        imageIds.forEach(id -> {
            if (!imageIdsResult.contains(id)) {
                log.debug("IMAGE ID: {} NOT FOUND", id);
                throw new AppException(ErrorCodeBase.NOT_FOUND, "IMAGE ID");
            }
        });
    }

    @Override
    public Single<String> update(Integer id, ProductRequest productRequest) {
        return productRepository.existsById(id).flatMap(isExist ->
                {
                    if (!isExist) throw new AppException(ErrorCodeBase.NOT_FOUND, "PRODUCT");
                    Product product = productMapper.toProduct(productRequest);
//                    if (productRequest.getImage() != null) {
//                        return uploadFileService.rxUpload(productRequest.getImage())
//                                .flatMap(image -> {
//                                    product.setImage(image);
//                                    return productRepository.update(id, product);
//                                });
//                    }
                    return productRepository.update(id, product);
                }
        ).map(integer -> "SUCCESS");
    }

    @Override
    public Single<String> delete(Integer id) {
        return productRepository.existsById(id)
                .flatMap(isExist -> {
                    if (!isExist) throw new AppException(ErrorCodeBase.NOT_FOUND, "PRODUCT");
                    return productRepository.deleteById(id);
                }).map(integer -> "SUCCESS");
    }

    @Override
    public Single<List<ProductResponse>> findAll(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest)
                .flatMap(productPageResponse -> {
                    List<Product> products = productPageResponse.getData().stream().toList();
                    List<ProductResponse> productResponses = productMapper.toProductResponse(products);
                    List<Integer> productIds = products.stream().map(Product::getId).toList();
                    return Single.zip(
                            imageRepository.findPrimaryByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                            productRepository.getNumberOfPaid(productIds),
                            reviewRepository.getRatedByProductIdIn(productIds),
                            (images, paidMap, ratingMap) -> {
                                Map<Integer, String> imageMap = images.stream().collect(Collectors.toMap(
                                        Image::getTargetId,
                                        Image::getUrl,
                                        (s, s2) -> s
                                ));
                                productResponses.forEach(productResponse -> {
                                    BigDecimal tempRate = ratingMap.get(productResponse.getId());
                                    double rating = tempRate == null ? 0 : tempRate.setScale(1, ROUND_HALF_UP).doubleValue();
                                    String imageUrl = imageMap.get(productResponse.getId());
                                    Integer sold = paidMap.get(productResponse.getId());
                                    productResponse.setImageUrl(imageUrl == null ? "" : imageUrl);
                                    productResponse.setSold(sold == null ? 0 : sold);
                                    productResponse.setRating(rating);
                                });
                                return productResponses;
                            }
                    );
                });
    }

    @Override
    public Single<ProductDetailResponse> findDetail(Integer id) {
        return productRepository.findById(id)
                .flatMap(productOptional -> {
                    Product product = ValidateUtils.getOptionalValue(productOptional, Product.class);
                    return Single.zip(
                            imageRepository.findByTargetIdAndType(id, ImageEnum.PRODUCT.getValue()),
                            productRepository.getNumberOfPaid(id),
                            reviewRepository.getRatedByProductId(id),
                            productVariantService.findDetailsByProductId(id),
                            (images, payCount, ratingCount, productVariantDetails) -> {
                                double rating = ratingCount.map(bigDecimal -> bigDecimal.setScale(1, ROUND_HALF_UP).doubleValue()).orElse(0.0);
                                ProductDetailResponse productDetailResponse = productMapper.toProductDetailResponse(product);
                                productDetailResponse.setImages(images);
                                productDetailResponse.setProductVariants(productVariantDetails);
                                productDetailResponse.setSold(payCount == null ? 0 : payCount);
                                productDetailResponse.setRating(rating);
                                return productDetailResponse;
                            }
                    );
                });
    }

    @Override
    public Single<String> addToCart(Integer id) {
        return productRepository.existsById(id)
                .flatMap(isExist -> {
                    if (!isExist) throw new AppException(ErrorCodeBase.NOT_FOUND, "PRODUCT");
//                    UserDetailImpl userDetail = (UserDetailImpl) authService.getCurrentUser();
                    return null;
                });
    }


}
