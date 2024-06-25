package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ClassLogImageRepository extends JpaRepository<ClassLogImage, Long> {
    List<ClassLogImage> findClassLogImagesByClassLogId(Long classLogId);
    @Modifying
    @Query("DELETE FROM ClassLogImage cli WHERE cli.classLog.id = :classLogId")
    void deleteByClassLogId(@Param("classLogId") Long classLogId);
}

