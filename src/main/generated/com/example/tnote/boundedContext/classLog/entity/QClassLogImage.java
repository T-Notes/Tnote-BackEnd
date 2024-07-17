package com.example.tnote.boundedContext.classLog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClassLogImage is a Querydsl query type for ClassLogImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassLogImage extends EntityPathBase<ClassLogImage> {

    private static final long serialVersionUID = 1927798435L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClassLogImage classLogImage = new QClassLogImage("classLogImage");

    public final QClassLog classLog;

    public final StringPath classLogImageUrl = createString("classLogImageUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final NumberPath<Long> version = createNumber("version", Long.class);

    public QClassLogImage(String variable) {
        this(ClassLogImage.class, forVariable(variable), INITS);
    }

    public QClassLogImage(Path<? extends ClassLogImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClassLogImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClassLogImage(PathMetadata metadata, PathInits inits) {
        this(ClassLogImage.class, metadata, inits);
    }

    public QClassLogImage(Class<? extends ClassLogImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.classLog = inits.isInitialized("classLog") ? new QClassLog(forProperty("classLog"), inits.get("classLog")) : null;
    }

}

