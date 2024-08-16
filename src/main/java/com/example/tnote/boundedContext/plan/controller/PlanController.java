package com.example.tnote.boundedContext.plan.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.plan.dto.PlanSaveRequest;
import com.example.tnote.boundedContext.plan.service.PlanService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/tnote/v1/plan")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Result> save(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @RequestParam final Long scheduleId,
                                       @RequestPart final PlanSaveRequest planSaveRequest,
                                       @RequestPart(name = "planImages", required = false) final List<MultipartFile> planImages) {
        return ResponseEntity.ok(
                Result.of(planService.save(principalDetails.getId(), scheduleId, planSaveRequest, planImages)));
    }

    @GetMapping
    public ResponseEntity<Result> find(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                       @RequestParam final Long planId) {

        return ResponseEntity.ok(Result.of(planService.find(principalDetails.getId(), planId)));
    }

    @DeleteMapping
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @RequestParam final Long planId) {
        return ResponseEntity.ok(Result.of(planService.delete(principalDetails.getId(), planId)));
    }

    @GetMapping(value = "/all")
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                          @RequestParam final Long scheduleId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "4") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(Result.of(planService.findAll(principalDetails.getId(), scheduleId, pageRequest)));
    }
}