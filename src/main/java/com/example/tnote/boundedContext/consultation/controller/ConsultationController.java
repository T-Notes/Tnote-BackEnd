package com.example.tnote.boundedContext.consultation.controller;

import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/consultation")
@RequiredArgsConstructor
public class ConsultationController {
    @GetMapping("/fields")
    public ResponseEntity<List<CounselingField>> getCounselingFields() {
        return ResponseEntity.ok(Arrays.asList(CounselingField.values()));
    }

    // CounselingType enum 값들을 반환하는 API
    @GetMapping("/types")
    public ResponseEntity<List<CounselingType>> getCounselingTypes() {
        return ResponseEntity.ok(Arrays.asList(CounselingType.values()));
    }


}
