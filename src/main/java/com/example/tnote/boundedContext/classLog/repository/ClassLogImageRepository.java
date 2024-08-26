package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassLogImageRepository extends JpaRepository<ClassLogImage, Long> {
    @Modifying
    @Query("DELETE FROM ClassLogImage cli WHERE cli.classLog.id = :classLogId")
    void deleteByClassLogId(@Param("classLogId") Long classLogId);
}

