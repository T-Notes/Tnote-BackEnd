package com.example.tnote.boundedContext.observation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QObservation is a Querydsl query type for Observation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QObservation extends EntityPathBase<Observation> {

    private static final long serialVersionUID = 635039142L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QObservation observation = new QObservation("observation");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final StringPath color = createString("color");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final StringPath guidance = createString("guidance");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath observationContents = createString("observationContents");

    public final ListPath<ObservationImage, QObservationImage> observationImage = this.<ObservationImage, QObservationImage>createList("observationImage", ObservationImage.class, QObservationImage.class, PathInits.DIRECT2);

    public final com.example.tnote.boundedContext.schedule.entity.QSchedule schedule;

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public QObservation(String variable) {
        this(Observation.class, forVariable(variable), INITS);
    }

    public QObservation(Path<? extends Observation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QObservation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QObservation(PathMetadata metadata, PathInits inits) {
        this(Observation.class, metadata, inits);
    }

    public QObservation(Class<? extends Observation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new com.example.tnote.boundedContext.schedule.entity.QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

