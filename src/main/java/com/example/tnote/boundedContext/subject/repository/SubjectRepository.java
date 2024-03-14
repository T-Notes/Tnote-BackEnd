package com.example.tnote.boundedContext.subject.repository;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subjects, Long> {
    void deleteAllByUserId(Long userId);
}
