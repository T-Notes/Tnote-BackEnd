package com.example.tnote.boundedContext.proceeding.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProceeding is a Querydsl query type for Proceeding
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProceeding extends EntityPathBase<Proceeding> {

    private static final long serialVersionUID = -312206488L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProceeding proceeding = new QProceeding("proceeding");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath location = createString("location");

    public final ListPath<ProceedingImage, QProceedingImage> proceedingImage = this.<ProceedingImage, QProceedingImage>createList("proceedingImage", ProceedingImage.class, QProceedingImage.class, PathInits.DIRECT2);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public final StringPath workContents = createString("workContents");

    public QProceeding(String variable) {
        this(Proceeding.class, forVariable(variable), INITS);
    }

    public QProceeding(Path<? extends Proceeding> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProceeding(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProceeding(PathMetadata metadata, PathInits inits) {
        this(Proceeding.class, metadata, inits);
    }

    public QProceeding(Class<? extends Proceeding> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

