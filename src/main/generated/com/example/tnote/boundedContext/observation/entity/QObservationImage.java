package com.example.tnote.boundedContext.observation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QObservationImage is a Querydsl query type for ObservationImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QObservationImage extends EntityPathBase<ObservationImage> {

    private static final long serialVersionUID = 634282837L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QObservationImage observationImage = new QObservationImage("observationImage");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QObservation observation;

    public final StringPath observationImageUrl = createString("observationImageUrl");

    public QObservationImage(String variable) {
        this(ObservationImage.class, forVariable(variable), INITS);
    }

    public QObservationImage(Path<? extends ObservationImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QObservationImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QObservationImage(PathMetadata metadata, PathInits inits) {
        this(ObservationImage.class, metadata, inits);
    }

    public QObservationImage(Class<? extends ObservationImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.observation = inits.isInitialized("observation") ? new QObservation(forProperty("observation"), inits.get("observation")) : null;
    }

}

