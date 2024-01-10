package com.example.tnote.boundedContext.schedule.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSubjects is a Querydsl query type for Subjects
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSubjects extends EntityPathBase<Subjects> {

    private static final long serialVersionUID = 1889331614L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSubjects subjects = new QSubjects("subjects");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final EnumPath<ClassDay> classDay = createEnum("classDay", ClassDay.class);

    public final StringPath classLocation = createString("classLocation");

    public final StringPath classTime = createString("classTime");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath memo = createString("memo");

    public final QSchedule schedule;

    public final StringPath subjectName = createString("subjectName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QSubjects(String variable) {
        this(Subjects.class, forVariable(variable), INITS);
    }

    public QSubjects(Path<? extends Subjects> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSubjects(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSubjects(PathMetadata metadata, PathInits inits) {
        this(Subjects.class, metadata, inits);
    }

    public QSubjects(Class<? extends Subjects> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.schedule = inits.isInitialized("schedule") ? new QSchedule(forProperty("schedule"), inits.get("schedule")) : null;
    }

}

