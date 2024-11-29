package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.repository.IBlockingRepository;

import java.util.Collection;
import java.util.List;

public interface IImageRepository extends IBlockingRepository<Image, Integer> {

    Integer deleteByTargetIdAndTypeBlocking(Integer targetId, String type);

    List<Image> findByIdsAndTargetIdNullableAndTypeBlocking(Collection<Integer> ids, Integer targetId, String type);
}
