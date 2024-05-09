package com.example.tnote.boundedContext.home.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLastSchedule is a Querydsl query type for LastSchedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLastSchedule extends EntityPathBase<LastSchedule> {

    private static final long serialVersionUID = 933380204L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLastSchedule lastSchedule = new QLastSchedule("lastSchedule");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final com.example.tnote.boundedContext.schedule.entity.QSchedule schedule;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public QLastSchedule(String variable) {
        this(LastSchedule.class, forVariable(variable), INITS);
    }

    public QLastSchedule(Path<? extends LastSchedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLastSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLastSchedule(PathMetadata metadata, PathInits inits) {
        this(LastSchedule.class, metadata, inits);
    }

    public QLastSchedule(Class<? extends LastSchedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new com.example.tnote.boundedContext.schedule.entity.QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

