package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Product;
import com.example.repository.IBlockingRepository;

public interface IProductRepository extends IBlockingRepository<Product,Integer> {
}
