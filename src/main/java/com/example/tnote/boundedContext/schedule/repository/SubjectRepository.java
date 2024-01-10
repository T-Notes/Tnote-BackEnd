package com.example.tnote.boundedContext.schedule.repository;

import com.example.tnote.boundedContext.schedule.entity.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subjects, Long> {
}
