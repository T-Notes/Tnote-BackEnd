package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassLogImageRepository extends JpaRepository<ClassLogImage, Long> {
    List<ClassLogImage> findClassLogImagesByClassLog_Id(Long classLogId);
}
