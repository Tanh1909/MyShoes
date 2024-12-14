package com.example.moduleapp.service.impl;

import com.example.common.utils.ValidateUtils;
import com.example.moduleapp.data.dto.ProductVariantAttribute;
import com.example.moduleapp.data.dto.ProductVariantDetail;
import com.example.moduleapp.data.mapper.ProductVariantMapper;
import com.example.moduleapp.model.tables.pojos.ProductVariant;
import com.example.moduleapp.repository.impl.ProductVariantRepository;
import com.example.moduleapp.service.ProductVariantService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Log4j2
public class ProductVariantServiceImpl implements ProductVariantService {
    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;

    @Override
    public Single<ProductVariantDetail> findDetailById(Integer id) {
        return productVariantRepository.findById(id)
                .flatMap(productVariantOptional -> {
                    ProductVariant productVariant = ValidateUtils.getOptionalValue(productVariantOptional, ProductVariant.class);
                    return productVariantRepository.findAttributeById(productVariant.getId())
                            .map(productVariantAttributes -> {
                                Map<String, String> mapAttr = productVariantAttributes.stream()
                                        .collect(Collectors.toMap(ProductVariantAttribute::getName, ProductVariantAttribute::getValue));
                                ProductVariantDetail productVariantDetail = productVariantMapper.toProductVariantDetail(productVariant);
                                productVariantDetail.setAttributes(mapAttr);
                                return productVariantDetail;
                            });
                });
    }

    @Override
    public Single<Map<String, String>> findMapAttrById(Integer id) {
        return productVariantRepository.findById(id)
                .flatMap(productVariantOptional -> {
                    ProductVariant productVariant = ValidateUtils.getOptionalValue(productVariantOptional, ProductVariant.class);
                    return productVariantRepository.findAttributeById(productVariant.getId())
                            .map(productVariantAttributes -> productVariantAttributes.stream()
                                    .collect(Collectors.toMap(ProductVariantAttribute::getName, ProductVariantAttribute::getValue))
                            );
                });
    }

    @Override
    public Single<List<ProductVariantDetail>> findDetailsByIdIn(Collection<Integer> ids) {
        return productVariantRepository.findByIds(ids)
                .flatMap(productVariants -> getProductVariantDetails(productVariants, ids));
    }

    @Override
    public Single<List<ProductVariantDetail>> findDetailsByIdInIgnoreFilter(Collection<Integer> ids) {
        return productVariantRepository.findByIdsIgnoreFilter(ids)
                .flatMap(productVariants -> getProductVariantDetails(productVariants, ids));
    }

    @Override
    public Single<List<ProductVariantDetail>> findDetailsByProductId(Integer productId) {
        return productVariantRepository.findByProductId(productId)
                .flatMap(productVariants -> {
                    List<Integer> ids = productVariants.stream().map(ProductVariant::getId).toList();
                    return getProductVariantDetails(productVariants, ids);
                });
    }

    @Override
    public Single<List<ProductVariantDetail>> findDetailsByProductIdIn(Collection<Integer> productIds) {
        return productVariantRepository.findByProductIdIn(productIds)
                .flatMap(productVariants -> {
                    List<Integer> ids = productVariants.stream().map(ProductVariant::getId).toList();
                    return getProductVariantDetails(productVariants, ids);
                });
    }

    private Single<List<ProductVariantDetail>> getProductVariantDetails(List<ProductVariant> productVariants, Collection<Integer> ids) {
        return productVariantRepository.findAttributeByIdIn(ids)
                .map(productVariantAttributes -> {
                    Map<Integer, Map<String, String>> groupByVariantId = productVariantAttributes.stream()
                            .collect(Collectors.groupingBy(
                                    ProductVariantAttribute::getVariantId,
                                    Collectors.toMap(ProductVariantAttribute::getName, ProductVariantAttribute::getValue)
                            ));
                    return productVariants.stream()
                            .map(productVariant -> {
                                ProductVariantDetail productVariantDetail = productVariantMapper.toProductVariantDetail(productVariant);
                                productVariantDetail.setAttributes(groupByVariantId.get(productVariant.getId()));
                                return productVariantDetail;
                            })
                            .toList();
                });
    }
}
