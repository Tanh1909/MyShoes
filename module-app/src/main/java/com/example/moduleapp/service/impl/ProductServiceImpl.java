package com.example.moduleapp.service.impl;

import com.example.cloudinary.service.IUploadFileService;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.moduleapp.data.ProductMapper;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.moduleapp.service.ProductService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final IRxProductRepository productRepository;
    private final IUploadFileService uploadFileService;
    private final ProductMapper productMapper;

    @Override
    public Single<Product> create(ProductRequest productRequest) {
        if (productRequest.getImage() != null) {
            return uploadFileService.rxUpload(productRequest.getImage())
                    .flatMap(image -> {
                        Product product = productMapper.toProduct(productRequest);
                        product.setImage(image);
                        return productRepository.insertReturn(product);
                    });
        }
        return productRepository.insertReturn(productMapper.toProduct(productRequest));
    }

    @Override
    public Single<Product> update(Long id, ProductRequest productRequest) {
        return productRepository.existsById(id).flatMap(isExist ->
                {
                    if (!isExist) throw new AppException(ErrorCodeBase.NOT_FOUND, "PRODUCT");
                    return productRepository.updateReturn(id, productMapper.toProduct(productRequest));
                }
        );
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
}
