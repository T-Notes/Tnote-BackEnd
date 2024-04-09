package com.example.tnote.boundedContext.recentLog.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecentLog is a Querydsl query type for RecentLog
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecentLog extends EntityPathBase<RecentLog> {

    private static final long serialVersionUID = -1498049914L;

    public static final QRecentLog recentLog = new QRecentLog("recentLog");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Long> logId = createNumber("logId", Long.class);

    public final StringPath logType = createString("logType");

    public final DateTimePath<java.time.Instant> timestamp = createDateTime("timestamp", java.time.Instant.class);

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QRecentLog(String variable) {
        super(RecentLog.class, forVariable(variable));
    }

    public QRecentLog(Path<? extends RecentLog> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecentLog(PathMetadata metadata) {
        super(RecentLog.class, metadata);
    }

}

