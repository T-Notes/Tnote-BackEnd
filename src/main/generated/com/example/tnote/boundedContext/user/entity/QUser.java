package com.example.tnote.boundedContext.user.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -851436330L;

    public static final QUser user = new QUser("user");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final BooleanPath alarm = createBoolean("alarm");

    public final NumberPath<Integer> career = createNumber("career", Integer.class);

    public final ListPath<com.example.tnote.boundedContext.classLog.entity.ClassLog, com.example.tnote.boundedContext.classLog.entity.QClassLog> classLogs = this.<com.example.tnote.boundedContext.classLog.entity.ClassLog, com.example.tnote.boundedContext.classLog.entity.QClassLog>createList("classLogs", com.example.tnote.boundedContext.classLog.entity.ClassLog.class, com.example.tnote.boundedContext.classLog.entity.QClassLog.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.consultation.entity.Consultation, com.example.tnote.boundedContext.consultation.entity.QConsultation> consultations = this.<com.example.tnote.boundedContext.consultation.entity.Consultation, com.example.tnote.boundedContext.consultation.entity.QConsultation>createList("consultations", com.example.tnote.boundedContext.consultation.entity.Consultation.class, com.example.tnote.boundedContext.consultation.entity.QConsultation.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final NumberPath<Integer> lastScheduleId = createNumber("lastScheduleId", Integer.class);

    public final StringPath lastScheduleName = createString("lastScheduleName");

    public final ListPath<com.example.tnote.boundedContext.observation.entity.Observation, com.example.tnote.boundedContext.observation.entity.QObservation> observations = this.<com.example.tnote.boundedContext.observation.entity.Observation, com.example.tnote.boundedContext.observation.entity.QObservation>createList("observations", com.example.tnote.boundedContext.observation.entity.Observation.class, com.example.tnote.boundedContext.observation.entity.QObservation.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.proceeding.entity.Proceeding, com.example.tnote.boundedContext.proceeding.entity.QProceeding> proceedings = this.<com.example.tnote.boundedContext.proceeding.entity.Proceeding, com.example.tnote.boundedContext.proceeding.entity.QProceeding>createList("proceedings", com.example.tnote.boundedContext.proceeding.entity.Proceeding.class, com.example.tnote.boundedContext.proceeding.entity.QProceeding.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.schedule.entity.Schedule, com.example.tnote.boundedContext.schedule.entity.QSchedule> schedules = this.<com.example.tnote.boundedContext.schedule.entity.Schedule, com.example.tnote.boundedContext.schedule.entity.QSchedule>createList("schedules", com.example.tnote.boundedContext.schedule.entity.Schedule.class, com.example.tnote.boundedContext.schedule.entity.QSchedule.class, PathInits.DIRECT2);

    public final StringPath school = createString("school");

    public final StringPath subject = createString("subject");

    public final ListPath<com.example.tnote.boundedContext.todo.entity.Todo, com.example.tnote.boundedContext.todo.entity.QTodo> todos = this.<com.example.tnote.boundedContext.todo.entity.Todo, com.example.tnote.boundedContext.todo.entity.QTodo>createList("todos", com.example.tnote.boundedContext.todo.entity.Todo.class, com.example.tnote.boundedContext.todo.entity.QTodo.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath username = createString("username");

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

