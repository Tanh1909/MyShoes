package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;

public interface IRxImageRepository extends IRxJooqRepository<Image, Integer> {
    Single<Image> findByTargetIdAndType(Integer targetId, String type);

    Single<List<Image>> findAllByIdIn(Collection<Integer> ids);

    Single<String> updateAll(Collection<Image> images);
}
