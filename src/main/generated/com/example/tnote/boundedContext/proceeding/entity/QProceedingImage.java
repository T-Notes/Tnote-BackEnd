package com.example.tnote.boundedContext.proceeding.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QProceedingImage is a Querydsl query type for ProceedingImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QProceedingImage extends EntityPathBase<ProceedingImage> {

    private static final long serialVersionUID = -1717272877L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QProceedingImage proceedingImage = new QProceedingImage("proceedingImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QProceeding proceeding;

    public final StringPath proceedingImageUrl = createString("proceedingImageUrl");

    public QProceedingImage(String variable) {
        this(ProceedingImage.class, forVariable(variable), INITS);
    }

    public QProceedingImage(Path<? extends ProceedingImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QProceedingImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QProceedingImage(PathMetadata metadata, PathInits inits) {
        this(ProceedingImage.class, metadata, inits);
    }

    public QProceedingImage(Class<? extends ProceedingImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.proceeding = inits.isInitialized("proceeding") ? new QProceeding(forProperty("proceeding"), inits.get("proceeding")) : null;
    }

}

