package com.example.moduleapp.service.impl;

import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.moduleapp.data.mapper.ProductAttributeMapper;
import com.example.moduleapp.data.mapper.ProductMapper;
import com.example.moduleapp.data.mapper.ProductVariantMapper;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.*;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.moduleapp.repository.impl.ProductAttributeOptionRepository;
import com.example.moduleapp.repository.impl.ProductAttributeRepository;
import com.example.moduleapp.repository.impl.ProductVariantAttributeOptionRepository;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final IRxCartRepository cartRepository;
    private final ProductMapper productMapper;
    private final ProductAttributeMapper productAttributeMapper;
    private final ProductVariantMapper productVariantMapper;
    //    private final AuthService authService;
    private final IRxProductRepository productRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final ProductAttributeOptionRepository productAttributeOptionRepository;
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantAttributeOptionRepository productVariantAttributeOptionRepository;

    @Transactional
    @Override
    public Single<String> create(ProductRequest productRequest) {
        return Single.zip(
                productRepository.insertReturn(productMapper.toProduct(productRequest)),
                productAttributeRepository.findOrInsert(productAttributeMapper.toProductAttribute(productRequest.getAttributes())),
                (product, productAttributes) -> {
                    Map<String, Integer> mapReq = productAttributes.stream()
                            .collect(Collectors.toMap(ProductAttribute::getName, ProductAttribute::getId));
                    List<ProductAttributeOption> productAttributeOptionStream = productRequest.getAttributes().stream()
                            .flatMap(attributeRequest -> attributeRequest.getOptions().stream()
                                    .map(s -> new ProductAttributeOption()
                                            .setValue(s)
                                            .setProductAttributeId(mapReq.get(attributeRequest.getName()))
                                    )).toList();
                    List<ProductVariant> productVariantReq = productVariantMapper.toProductVariant(productRequest.getVariants()).stream()
                            .map(productVariant -> productVariant.setProductId(product.getId())).toList();
                    return Single.zip(
                            productVariantRepository.insertReturn(productVariantReq),
                            productAttributeOptionRepository.findOrInsert(productAttributeOptionStream),
                            (productVariants, productAttributeOptions) -> {
                                Map<String, Integer> mapByValue = productAttributeOptions.stream().collect(Collectors.toMap(
                                        ProductAttributeOption::getValue,
                                        ProductAttributeOption::getId
                                ));
                                List<ProductRequest.VariantsRequest> variantsRequests = productRequest.getVariants();
                                //Gia su sau khi insert xong no van giu nguyen thu tu
                                List<ProductVariantsAttributeOption> pvaos = IntStream.range(0, productAttributeOptions.size())
                                        .mapToObj(value -> {
                                            ProductVariant productVariant = productVariants.get(value);
                                            ProductRequest.VariantsRequest variantsRequest = variantsRequests.get(value);
                                            return variantsRequest.getAttributeOptions().stream()
                                                    .map(s -> new ProductVariantsAttributeOption()
                                                            .setProductAttributeOptionId(mapByValue.get(s))
                                                            .setVariantId(productVariant.getId())
                                                    ).toList();
                                        })
                                        .flatMap(Collection::stream)
                                        .toList();
                                return productVariantAttributeOptionRepository.insertReturn(pvaos);
                            }

                    );
                }
        ).flatMap(singleSingle -> singleSingle).map(listSingle -> "SUCCESS");
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
    public Single<PageResponse<Product>> findAll(PageRequest pageRequest) {
        return productRepository.findAll(pageRequest);
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
