package com.example.moduleapp.repository.impl;

import com.example.moduleapp.model.tables.pojos.Image;
import com.example.moduleapp.repository.IImageRepository;
import com.example.moduleapp.repository.IRxImageRepository;
import com.example.repository.JooqRepository;
import io.reactivex.rxjava3.core.Single;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.common.template.RxTemplate.rxSchedulerIo;
import static com.example.moduleapp.model.Tables.IMAGE;
import static java.util.Optional.ofNullable;

@Repository
@RequiredArgsConstructor
public class ImageRepository extends JooqRepository<Image, Integer>
        implements IRxImageRepository, IImageRepository {
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
    public Condition filterActive() {
        return super.filterActive().and(IMAGE.TARGET_ID.isNull());
    }

    @Override
    public Single<List<Image>> findByTargetIdAndType(Integer targetId, String type) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.TARGET_ID.eq(targetId).and(IMAGE.TYPE.eq(type)))
                .fetchInto(Image.class)
        );
    }


    @Override
    public Single<List<Image>> findByTargetIdInAndType(Collection<Integer> targetIds, String type) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.TARGET_ID.in(targetIds).and(IMAGE.TYPE.eq(type)))
                .fetchInto(Image.class)
        );
    }

    @Override
    public Single<Optional<Image>> findPrimaryByTargetIdAndType(Integer targetId, String type) {
        return rxSchedulerIo(() -> ofNullable(getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.TARGET_ID.eq(targetId).and(IMAGE.TYPE.eq(type)).and(IMAGE.IS_PRIMARY.isTrue()))
                .fetchOneInto(Image.class))
        );
    }

    @Override
    public Single<List<Image>> findPrimaryByTargetIdInAndType(Collection<Integer> targetIds, String type) {
        return rxSchedulerIo(() -> getDSLContext()
                .select()
                .from(getTable())
                .where(IMAGE.TARGET_ID.in(targetIds).and(IMAGE.TYPE.eq(type)).and(IMAGE.IS_PRIMARY.isTrue()))
                .fetchInto(Image.class)
        );
    }

    @Override
    public Integer deleteByTargetIdAndTypeBlocking(Integer targetId, String type) {
        return getDSLContext()
                .deleteFrom(getTable())
                .where(IMAGE.TARGET_ID.eq(targetId).and(IMAGE.TYPE.eq(type)))
                .execute();
    }
}
