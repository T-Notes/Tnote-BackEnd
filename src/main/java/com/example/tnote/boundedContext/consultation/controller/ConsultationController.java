package com.example.tnote.boundedContext.consultation.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDeleteResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationDetailResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationRequestDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationUpdateRequestDto;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/consultation")
@RequiredArgsConstructor
public class ConsultationController {
    private final ConsultationService consultationService;

    @GetMapping("/fields")
    public ResponseEntity<Result> getCounselingFields() {
        List<CounselingField> response = Arrays.asList(CounselingField.values());
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/types")
    public ResponseEntity<Result> getCounselingTypes() {
        List<CounselingType> response = Arrays.asList(CounselingType.values());
        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping(value = "/consultations", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> createConsultation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestPart ConsultationRequestDto requestDto,
                                                     @RequestPart(name = "consultationImages", required = false) List<MultipartFile> consultationImages) {
        ConsultationResponseDto consultationResponseDto = consultationService.save(principalDetails.getId(),
                requestDto, consultationImages);
        return ResponseEntity.ok(Result.of(consultationResponseDto));
    }

    @GetMapping("/consultations")
    public ResponseEntity<Result> getAllConsultations(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        List<ConsultationResponseDto> consultationResponseDtos = consultationService.readAllConsultation(
                principalDetails.getId());

        return ResponseEntity.ok(Result.of(consultationResponseDtos));
    }

    @DeleteMapping("/{consultationId}")
    public ResponseEntity<Result> deleteConsultation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @PathVariable Long consultationId) {
        ConsultationDeleteResponseDto deleteResponseDto = consultationService.deleteClassLog(principalDetails.getId(),
                consultationId);
        return ResponseEntity.ok(Result.of(deleteResponseDto));
    }

    @GetMapping("/{consultationId}")
    public ResponseEntity<Result> getClassLogDetail(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                    @PathVariable Long consultationId) {
        //Todo 나중에 아카이브 컨트롤러로 빼야할수도 있습니다 회의가 필요합니다.
        ConsultationDetailResponseDto detailResponseDto = consultationService.getConsultationDetail(
                principalDetails.getId(),
                consultationId);
        return ResponseEntity.ok(Result.of(detailResponseDto));
    }

    @PatchMapping("/{consultationId}")
    public ResponseEntity<Result> updateConsultation(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @PathVariable Long consultationId,
                                                     @RequestPart ConsultationUpdateRequestDto requestDto,
                                                     @RequestPart(name = "consultationImages", required = false) List<MultipartFile> consultationImages) {
        ConsultationResponseDto responseDto = consultationService.updateConsultation(principalDetails.getId(),
                consultationId, requestDto, consultationImages);
        return ResponseEntity.ok(Result.of(responseDto));
    }
}
