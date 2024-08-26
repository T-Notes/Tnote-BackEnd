package com.example.tnote.boundedContext.schedule.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QSchedule is a Querydsl query type for Schedule
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSchedule extends EntityPathBase<Schedule> {

    private static final long serialVersionUID = -1033688210L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QSchedule schedule = new QSchedule("schedule");

    public final com.example.tnote.base.entity.QBaseTimeEntity _super = new com.example.tnote.base.entity.QBaseTimeEntity(this);

    public final ListPath<com.example.tnote.boundedContext.classLog.entity.ClassLog, com.example.tnote.boundedContext.classLog.entity.QClassLog> classLogList = this.<com.example.tnote.boundedContext.classLog.entity.ClassLog, com.example.tnote.boundedContext.classLog.entity.QClassLog>createList("classLogList", com.example.tnote.boundedContext.classLog.entity.ClassLog.class, com.example.tnote.boundedContext.classLog.entity.QClassLog.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.consultation.entity.Consultation, com.example.tnote.boundedContext.consultation.entity.QConsultation> consultationList = this.<com.example.tnote.boundedContext.consultation.entity.Consultation, com.example.tnote.boundedContext.consultation.entity.QConsultation>createList("consultationList", com.example.tnote.boundedContext.consultation.entity.Consultation.class, com.example.tnote.boundedContext.consultation.entity.QConsultation.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastClass = createString("lastClass");

    public final ListPath<com.example.tnote.boundedContext.observation.entity.Observation, com.example.tnote.boundedContext.observation.entity.QObservation> observationList = this.<com.example.tnote.boundedContext.observation.entity.Observation, com.example.tnote.boundedContext.observation.entity.QObservation>createList("observationList", com.example.tnote.boundedContext.observation.entity.Observation.class, com.example.tnote.boundedContext.observation.entity.QObservation.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.plan.entity.Plan, com.example.tnote.boundedContext.plan.entity.QPlan> planList = this.<com.example.tnote.boundedContext.plan.entity.Plan, com.example.tnote.boundedContext.plan.entity.QPlan>createList("planList", com.example.tnote.boundedContext.plan.entity.Plan.class, com.example.tnote.boundedContext.plan.entity.QPlan.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.proceeding.entity.Proceeding, com.example.tnote.boundedContext.proceeding.entity.QProceeding> proceedingList = this.<com.example.tnote.boundedContext.proceeding.entity.Proceeding, com.example.tnote.boundedContext.proceeding.entity.QProceeding>createList("proceedingList", com.example.tnote.boundedContext.proceeding.entity.Proceeding.class, com.example.tnote.boundedContext.proceeding.entity.QProceeding.class, PathInits.DIRECT2);

    public final StringPath semesterName = createString("semesterName");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public final ListPath<com.example.tnote.boundedContext.subject.entity.Subjects, com.example.tnote.boundedContext.subject.entity.QSubjects> subjectsList = this.<com.example.tnote.boundedContext.subject.entity.Subjects, com.example.tnote.boundedContext.subject.entity.QSubjects>createList("subjectsList", com.example.tnote.boundedContext.subject.entity.Subjects.class, com.example.tnote.boundedContext.subject.entity.QSubjects.class, PathInits.DIRECT2);

    public final ListPath<com.example.tnote.boundedContext.todo.entity.Todo, com.example.tnote.boundedContext.todo.entity.QTodo> todoList = this.<com.example.tnote.boundedContext.todo.entity.Todo, com.example.tnote.boundedContext.todo.entity.QTodo>createList("todoList", com.example.tnote.boundedContext.todo.entity.Todo.class, com.example.tnote.boundedContext.todo.entity.QTodo.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.example.tnote.boundedContext.user.entity.QUser user;

    public QSchedule(String variable) {
        this(Schedule.class, forVariable(variable), INITS);
    }

    public QSchedule(Path<? extends Schedule> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QSchedule(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QSchedule(PathMetadata metadata, PathInits inits) {
        this(Schedule.class, metadata, inits);
    }

    public QSchedule(Class<? extends Schedule> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.user = inits.isInitialized("user") ? new com.example.tnote.boundedContext.user.entity.QUser(forProperty("user")) : null;
    }

}

