package com.example.tnote.boundedContext.consultation.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QConsultationImage is a Querydsl query type for ConsultationImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QConsultationImage extends EntityPathBase<ConsultationImage> {

    private static final long serialVersionUID = -1820464343L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QConsultationImage consultationImage = new QConsultationImage("consultationImage");

    public final QConsultation consultation;

    public final StringPath consultationImageUrl = createString("consultationImageUrl");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public QConsultationImage(String variable) {
        this(ConsultationImage.class, forVariable(variable), INITS);
    }

    public QConsultationImage(Path<? extends ConsultationImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QConsultationImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QConsultationImage(PathMetadata metadata, PathInits inits) {
        this(ConsultationImage.class, metadata, inits);
    }

    public QConsultationImage(Class<? extends ConsultationImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.consultation = inits.isInitialized("consultation") ? new QConsultation(forProperty("consultation"), inits.get("consultation")) : null;
    }

}

