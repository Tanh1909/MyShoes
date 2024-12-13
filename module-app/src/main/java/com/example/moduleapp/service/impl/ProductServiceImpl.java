package com.example.moduleapp.service.impl;

import com.example.common.data.request.pagination.Order;
import com.example.common.data.request.pagination.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.common.utils.StringUtils;
import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.mapper.ImageMapper;
import com.example.moduleapp.data.mapper.ProductAttributeMapper;
import com.example.moduleapp.data.mapper.ProductMapper;
import com.example.moduleapp.data.mapper.ProductVariantMapper;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductDetailResponse;
import com.example.moduleapp.data.response.ProductResponse;
import com.example.moduleapp.model.tables.pojos.*;
import com.example.moduleapp.repository.impl.*;
import com.example.moduleapp.service.ImageService;
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

import static com.example.common.config.constant.CommonConstant.SUCCESS;
import static com.example.common.config.constant.ErrorCodeBase.NOT_FOUND;
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
    private final ImageService imageService;
    private final ImageMapper imageMapper;

    private static final Integer SKU_CODE_LENGTH = 8;

    @Transactional
    @Override
    public Single<String> create(ProductRequest productRequest) {
        Product product = productRepository.insertReturnBlocking(productMapper.toProduct(productRequest));
        Integer productId = product.getId();
        productRequest.getVariants().forEach(variantsRequest -> {
            String skuCode = variantsRequest.getSkuCode();
            variantsRequest.setSkuCode(StringUtils.isEmpty(skuCode) ? generateSkuCode(SKU_CODE_LENGTH) : skuCode);
        });
        imageService.updateImagesBlocking(productRequest.getImages(), productId, ImageEnum.PRODUCT);
        //insert attr and options
        List<ProductAttributeOption> productAttributeOptions = insertAttrAndReturnOption(productRequest);
        //insert product variant req
        List<ProductVariant> productVariantsResult = insertAndGetProductVariants(productRequest, productId);
        insertPVAO(productRequest, productAttributeOptions, productVariantsResult);
        return Single.just(SUCCESS);
    }

    private void insertPVAO(ProductRequest productRequest, List<ProductAttributeOption> productAttributeOptions, List<ProductVariant> productVariantsResult) {
        Map<String, Integer> valueAndId = productAttributeOptions.stream()
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
    }

    private List<ProductVariant> insertAndGetProductVariants(ProductRequest productRequest, Integer productId) {
        List<ProductVariant> productVariantReq = productVariantMapper.toProductVariant(productRequest.getVariants()).stream()
                .map(productVariant -> productVariant.setProductId(productId))
                .toList();
        return productVariantRepository.insertAndFindBlocking(productVariantReq, productId);
    }

    public List<ProductAttributeOption> insertAttrAndReturnOption(ProductRequest productRequest) {
        List<ProductAttribute> productAttributesResult = productAttributeRepository.insertAndFindBlocking(productAttributeMapper.toProductAttribute(productRequest.getAttributes()));
        Map<String, Integer> mapReq = productAttributesResult.stream()
                .collect(Collectors.toMap(ProductAttribute::getName, ProductAttribute::getId));
        List<ProductAttributeOption> productAttributeOptions = productRequest.getAttributes().stream()
                .flatMap(attributeRequest -> attributeRequest.getOptions().stream()
                        .map(s -> new ProductAttributeOption()
                                .setValue(s)
                                .setProductAttributeId(mapReq.get(attributeRequest.getName()))
                        )).toList();
        return productAttributeOptionRepository.insertAndFindBlocking(productAttributeOptions, mapReq.values());
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
        return timestamp + "-" + randomPart;
    }


    @Transactional
    @Override
    public Single<String> update(Integer id, ProductRequest productRequest) {
        productRequest.getVariants().forEach(variantsRequest -> {
            String skuCode = variantsRequest.getSkuCode();
            variantsRequest.setSkuCode(StringUtils.isEmpty(skuCode) ? generateSkuCode(SKU_CODE_LENGTH) : skuCode);
        });
        Optional<Product> productOptional = productRepository.findByIdBlocking(id);
        Product product = productOptional.orElseThrow(() -> new AppException(NOT_FOUND, "PRODUCT"));
        productMapper.toProduct(product, productRequest);
        productRepository.updateBlocking(id, product);
        imageService.updateImagesBlocking(productRequest.getImages(), id, ImageEnum.PRODUCT);
        List<ProductAttributeOption> productAttributeOptions = insertAttrAndReturnOption(productRequest);
        List<ProductVariant> productVariants = insertAndGetProductVariants(productRequest, product.getId());
        setDeleteFieldForProductVariants(productRequest, productVariants);
        insertPVAO(productRequest, productAttributeOptions, productVariants);
        return Single.just(SUCCESS);
    }

    private void setDeleteFieldForProductVariants(ProductRequest productRequest, List<ProductVariant> productVariants) {
        Set<String> skuCodeReqs = productRequest.getVariants().stream()
                .map(ProductRequest.VariantsRequest::getSkuCode)
                .collect(Collectors.toSet());
        List<ProductVariant> missingProductVariant = productVariants.stream()
                .filter(productVariant -> !skuCodeReqs.contains(productVariant.getSkuCode()))
                .peek(productVariant -> productVariant.setDeletedAt(LocalDateTime.now()))
                .toList();
        productVariantRepository.insertUpdateOnDuplicateKeyBlocking(missingProductVariant);
    }

    @Transactional
    @Override
    public Single<String> delete(Integer id) {
        Product product = productRepository.findByIdBlocking(id).orElseThrow(() -> new AppException(NOT_FOUND, "PRODUCT"));
        List<ProductVariant> productVariants = productVariantRepository.findByProductIdBlocking(product.getId());
        List<ProductVariant> productVariantsReq = productVariants.stream().peek(productVariant -> productVariant.setDeletedAt(LocalDateTime.now()))
                .toList();
        product.setDeletedAt(LocalDateTime.now());
        productVariantRepository.insertUpdateOnDuplicateKeyBlocking(productVariantsReq);
        productRepository.updateBlocking(id, product);
        return Single.just(SUCCESS);
    }

    @Override
    public Single<PageResponse<ProductResponse>> findAll(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest)
                .flatMap(productPageResponse -> getPageResponseSingle(pageRequest, productPageResponse));
    }

    @Override
    public Single<PageResponse<ProductResponse>> findByNameLike(String name, PageRequest pageRequest) {
        return productRepository.findByNameLike(name, pageRequest)
                .flatMap(productPageResponse -> getPageResponseSingle(pageRequest, productPageResponse));
    }

    private Single<PageResponse<ProductResponse>> getPageResponseSingle(PageRequest pageRequest, PageResponse<Product> productPageResponse) {
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

                    Order soldOrder = pageRequest.getOrders()
                            .stream()
                            .filter(order -> order.getSortBy().equalsIgnoreCase("sold"))
                            .findFirst().orElse(null);
                    Order ratingOrder = pageRequest.getOrders()
                            .stream()
                            .filter(order -> order.getSortBy().equalsIgnoreCase("rating"))
                            .findFirst().orElse(null);
                    productResponses.forEach(productResponse -> {
                        BigDecimal tempRate = ratingMap.get(productResponse.getId());
                        double rating = tempRate == null ? 0 : tempRate.setScale(1, ROUND_HALF_UP).doubleValue();
                        String imageUrl = imageMap.get(productResponse.getId());
                        Integer sold = paidMap.get(productResponse.getId());
                        productResponse.setImageUrl(imageUrl == null ? "" : imageUrl);
                        productResponse.setSold(sold == null ? 0 : sold);
                        productResponse.setRating(rating);
                    });

                    if (soldOrder != null) {
                        boolean isDesc = Order.Direction.DESC.value().equalsIgnoreCase(soldOrder.getSortDirection());
                        productResponses.sort((o1, o2) -> isDesc ? o2.getSold().compareTo(o1.getSold()) : o1.getSold().compareTo(o2.getSold()));
                    }

                    if (ratingOrder != null) {
                        boolean isDesc = Order.Direction.DESC.value().equalsIgnoreCase(ratingOrder.getSortDirection());
                        productResponses.sort((o1, o2) -> isDesc ? o2.getRating().compareTo(o1.getRating()) : o1.getRating().compareTo(o2.getRating()));
                    }
                    return PageResponse.toPageResponse(productResponses, productPageResponse);
                }
        );
    }

    @Override
    public Single<PageResponse<ProductResponse>> findByCategoryId(Integer categoryId, PageRequest pageRequest) {
        return productRepository.findByCategoryId(categoryId, pageRequest)
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
                                return PageResponse.toPageResponse(productResponses, productPageResponse);
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
                                productDetailResponse.setImages(imageMapper.toImageResponses(images));
                                productDetailResponse.setProductVariants(productVariantDetails);
                                productDetailResponse.setSold(payCount == null ? 0 : payCount);
                                productDetailResponse.setRating(rating);
                                return productDetailResponse;
                            }
                    );
                });
    }


}
