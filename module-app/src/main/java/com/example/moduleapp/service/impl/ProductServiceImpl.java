package com.example.moduleapp.service.impl;

import com.example.cloudinary.service.IUploadFileService;
import com.example.common.config.constant.ErrorCodeBase;
import com.example.common.data.request.PageRequest;
import com.example.common.data.response.PageResponse;
import com.example.common.exception.AppException;
import com.example.moduleapp.data.ProductMapper;
import com.example.moduleapp.data.request.ProductRequest;
import com.example.moduleapp.model.tables.pojos.Product;
import com.example.moduleapp.model.tables.pojos.ProductAttribute;
import com.example.moduleapp.repository.IRxCartRepository;
import com.example.moduleapp.repository.IRxProductRepository;
import com.example.moduleapp.repository.impl.ProductAttributeRepository;
import com.example.moduleapp.service.ProductService;
import com.example.security.config.service.UserDetailImpl;
import com.example.security.service.AuthService;
import io.reactivex.rxjava3.core.Single;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final IRxProductRepository productRepository;
    private final IRxCartRepository cartRepository;
    private final IUploadFileService uploadFileService;
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final ProductAttributeRepository productAttributeRepository;

    @Override
    public Single<Product> create(ProductRequest productRequest) {
        ProductAttribute productAttribute=new ProductAttribute();
        productAttribute.setName("mau sac");
        ProductAttribute productAttribute1=new ProductAttribute();
        productAttribute1.setName("kich co");
        List<ProductAttribute> productAttributeList=List.of(productAttribute1,productAttribute);
        productAttributeRepository.findOrInsert(productAttributeList).blockingGet();
//        if (productRequest.getImage() != null) {
//            return uploadFileService.rxUpload(productRequest.getImage())
//                    .flatMap(image -> {
//                        Product product = productMapper.toProduct(productRequest);
//                        product.setImage(image);
//                        return productRepository.insertReturn(product);
//                    });
//        }
//        return productRepository.insertReturn(productMapper.toProduct(productRequest));
        return null;
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
                    UserDetailImpl userDetail= (UserDetailImpl) authService.getCurrentUser();
                    return null;
                });
    }
}
