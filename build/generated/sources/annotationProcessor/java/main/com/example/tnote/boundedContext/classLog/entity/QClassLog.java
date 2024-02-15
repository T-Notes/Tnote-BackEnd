package com.example.tnote.boundedContext.classLog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QClassLog is a Querydsl query type for ClassLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QClassLog extends EntityPathBase<ClassLog> {

    private static final long serialVersionUID = -2066212456L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QClassLog classLog = new QClassLog("classLog");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final StringPath classContents = createString("classContents");

    public final ListPath<ClassLogImage, QClassLogImage> classLogImage = this.<ClassLogImage, QClassLogImage>createList("classLogImage", ClassLogImage.class, QClassLogImage.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath magnitude = createString("magnitude");

    public final StringPath plan = createString("plan");

    public final com.example.tnote.boundedContext.schedule.entity.QSchedule schedule;

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath submission = createString("submission");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public QClassLog(String variable) {
        this(ClassLog.class, forVariable(variable), INITS);
    }

    public QClassLog(Path<? extends ClassLog> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QClassLog(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QClassLog(PathMetadata metadata, PathInits inits) {
        this(ClassLog.class, metadata, inits);
    }

    public QClassLog(Class<? extends ClassLog> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new com.example.tnote.boundedContext.schedule.entity.QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

