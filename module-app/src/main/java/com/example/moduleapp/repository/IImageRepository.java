package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.repository.IBlockingRepository;

public interface IImageRepository extends IBlockingRepository<Image, Integer> {
}
