package com.example.tnote.boundedContext.subject.repository;

import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.subject.exception.SubjectErrorCode;
import com.example.tnote.boundedContext.subject.exception.SubjectException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubjectRepository extends JpaRepository<Subjects, Long> {

    default Subjects findSubjectsById(Long id) {
        return findById(id)
                .orElseThrow(() -> new SubjectException(SubjectErrorCode.SUBJECT_NOT_FOUND));
    }
}
