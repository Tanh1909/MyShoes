package com.example.moduleapp.repository;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.repository.IRxJooqRepository;
import io.reactivex.rxjava3.core.Single;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IRxImageRepository extends IRxJooqRepository<Image, Integer> {
    Single<List<Image>> findByTargetIdAndType(Integer targetId, String type);

    Single<List<Image>> findByTargetIdInAndType(Collection<Integer> targetIds, String type);

    Single<Optional<Image>> findPrimaryByTargetIdAndType(Integer targetId, String type);

    Single<List<Image>> findPrimaryByTargetIdInAndType(Collection<Integer> targetIds, String type);

}
