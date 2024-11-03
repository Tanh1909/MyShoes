package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.PageRequest;
import com.example.common.exception.AppException;
import com.example.moduleapp.config.constant.ImageEnum;
import com.example.moduleapp.data.mapper.ProductAttributeMapper;
import com.example.moduleapp.data.mapper.ProductMapper;
import com.example.moduleapp.data.mapper.ProductVariantMapper;
import com.example.moduleapp.data.request.ImageRequest;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.data.response.ProductResponse;
import com.example.moduleapp.model.tables.pojos.*;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.moduleapp.repository.impl.*;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ProductServiceImpl implements ProductService {
    private final IRxCartRepository cartRepository;
    private final ProductMapper productMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductVariantMapper productVariantMapper;
    private final IRxProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeOptionRepository productAttributeOptionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeOptionRepository productVariantAttributeOptionRepository;
    private final ImageRepository imageRepository;

    private static final Integer SKU_CODE_LENGTH = 8;

    @Override
    public Single<String> create(ProductRequest productRequest) {
        Map<Integer, ImageRequest> mapImageReq = productRequest.getImages().stream()
                .collect(Collectors.toMap(ImageRequest::getId, img -> img));
        productRequest.getVariants().forEach(variantsRequest -> variantsRequest.setSkuCode(generateSkuCode(SKU_CODE_LENGTH)));
        return Single.zip(
                        productRepository.insertReturn(productMapper.toProduct(productRequest)),
                        productAttributeRepository.insertAndFind(productAttributeMapper.toProductAttribute(productRequest.getAttributes())),
                        imageRepository.findByIds(mapImageReq.keySet()),
                        (product, productAttributes, images) -> {
                            validateImage(images, mapImageReq.keySet());
                            List<Image> imageReq = images.stream().map(image -> {
                                image.setType(ImageEnum.PRODUCT.getValue());
                                image.setTargetId(product.getId());
                                if (mapImageReq.get(image.getId()).isPrimary()) image.setIsPrimary(Byte.valueOf("1"));
                                return image;
                            }).toList();
                            Map<String, Integer> mapReq = productAttributes.stream()
                                    .collect(Collectors.toMap(ProductAttribute::getName, ProductAttribute::getId));
                            List<ProductAttributeOption> productAttributeOptions = productRequest.getAttributes().stream()
                                    .flatMap(attributeRequest -> attributeRequest.getOptions().stream()
                                            .map(s -> new ProductAttributeOption()
                                                    .setValue(s)
                                                    .setProductAttributeId(mapReq.get(attributeRequest.getName()))
                                            )).toList();
                            List<ProductVariant> productVariantReq = productVariantMapper.toProductVariant(productRequest.getVariants()).stream()
                                    .map(productVariant -> productVariant.setProductId(product.getId()))
                                    .toList();
                            return Single.zip(
                                    productVariantRepository.insertAndFind(productVariantReq),
                                    productAttributeOptionRepository.insertAndFind(productAttributeOptions, mapReq.values()),
                                    imageRepository.insertUpdateOnDuplicateKey(imageReq),
                                    (productVariants, productAttributeOptionResult, imageResults) -> {
                                        Map<String, Integer> valueAndId = productAttributeOptionResult.stream()
                                                .collect(Collectors.toMap(
                                                        ProductAttributeOption::getValue,
                                                        ProductAttributeOption::getId
                                                ));
                                        Map<String, Integer> skuCodeAndId = productVariants.stream()
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
                                        return productVariantAttributeOptionRepository.insert(pvaos);
                                    }

                            );
                        }
                )
                .flatMap(singleSingle -> singleSingle)
                .flatMap(listSingle -> listSingle)
                .map(integers -> "SUCCESS");
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
    public Single<String> update(Long id, ProductRequest productRequest) {
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
    public Single<String> delete(Long id) {
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
                            imageRepository.findByTargetIdInAndType(productIds, ImageEnum.PRODUCT.getValue()),
                            productRepository.getNumberOfPaid(productIds),
                            (images, paidMap) -> {
                                Map<Integer, String> imageMap = images.stream().collect(Collectors.toMap(
                                        Image::getTargetId,
                                        Image::getUrl,
                                        (s, s2) -> s
                                ));
                                productResponses.forEach(productResponse -> {
                                    productResponse.setImageUrl(imageMap.get(productResponse.getId()));
                                    productResponse.setSold(paidMap.get(productResponse.getId()));
                                });
                                return productResponses;
                            }
                    );
                });
    }

    @Override
    public Single<String> addToCart(Long id) {
        return productRepository.existsById(id)
                .flatMap(isExist -> {
                    if (!isExist) throw new AppException(ErrorCodeBase.NOT_FOUND, "PRODUCT");
//                    UserDetailImpl userDetail = (UserDetailImpl) authService.getCurrentUser();
                    return null;
                });
    }
}
