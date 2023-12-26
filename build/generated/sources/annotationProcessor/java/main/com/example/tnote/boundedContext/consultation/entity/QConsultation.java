package com.example.tnote.boundedContext.consultation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConsultation is a Querydsl query type for Consultation
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConsultation extends EntityPathBase<Consultation> {

    private static final long serialVersionUID = 1310839122L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConsultation consultation = new QConsultation("consultation");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final StringPath consultationContents = createString("consultationContents");

    public final StringPath consultationResult = createString("consultationResult");

    public final EnumPath<CounselingField> counselingField = createEnum("counselingField", CounselingField.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> endDate = createDateTime("endDate", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final DateTimePath<java.time.LocalDateTime> startDate = createDateTime("startDate", java.time.LocalDateTime.class);

    public final StringPath studentName = createString("studentName");

    public final StringPath targetType = createString("targetType");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public QConsultation(String variable) {
        this(Consultation.class, forVariable(variable), INITS);
    }

    public QConsultation(Path<? extends Consultation> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConsultation(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConsultation(PathMetadata metadata, PathInits inits) {
        this(Consultation.class, metadata, inits);
    }

    public QConsultation(Class<? extends Consultation> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

