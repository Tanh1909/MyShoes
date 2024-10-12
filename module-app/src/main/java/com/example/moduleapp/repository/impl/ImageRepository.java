package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.repository.IRxImageRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.IMAGE;

@Repository
@RequiredArgsConstructor
public class ImageRepository extends JooqRepository<Image, Integer> implements IRxImageRepository {
    private final DSLContext dslContext;

    @Override
    protected DSLContext getDSLContext() {
        return dslContext;
    }

    @Override
    protected Table getTable() {
        return IMAGE;
    }

    @Override
    public Single<Image> findByTargetIdAndType(Integer targetId, String type) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.TARGET_ID.eq(targetId).and(IMAGE.TYPE.eq(type)))
                .fetchOneInto(Image.class)
        );
    }

    @Override
    public Single<List<Image>> findAllByIdIn(Collection<Integer> ids) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.ID.in(ids))
                .fetchInto(pojoClass)
        );
    }

    @Override
    public Single<String> updateAll(Collection<Image> images) {
        return Single.just(images.stream()
                .map(image -> update(image.getId(), image).blockingGet())
                .toList()
        ).map(p -> "SUCCESS");
    }
}
