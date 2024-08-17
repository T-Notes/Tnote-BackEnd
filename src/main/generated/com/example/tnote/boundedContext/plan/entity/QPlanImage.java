package com.example.tnote.boundedContext.plan.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlanImage is a Querydsl query type for PlanImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlanImage extends EntityPathBase<PlanImage> {

    private static final long serialVersionUID = 1066452457L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlanImage planImage = new QPlanImage("planImage");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath name = createString("name");

    public final QPlan plan;

    public final StringPath planImageUrl = createString("planImageUrl");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QPlanImage(String variable) {
        this(PlanImage.class, forVariable(variable), INITS);
    }

    public QPlanImage(Path<? extends PlanImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlanImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlanImage(PathMetadata metadata, PathInits inits) {
        this(PlanImage.class, metadata, inits);
    }

    public QPlanImage(Class<? extends PlanImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.plan = inits.isInitialized("plan") ? new QPlan(forProperty("plan"), inits.get("plan")) : null;
    }

}

