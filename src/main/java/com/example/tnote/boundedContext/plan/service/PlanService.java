package com.example.tnote.boundedContext.plan.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.boundedContext.plan.dto.PlanDeleteResponse;
import com.example.tnote.boundedContext.plan.dto.PlanResponses;
import com.example.tnote.boundedContext.plan.dto.PlanSaveRequest;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.entity.PlanImage;
import com.example.tnote.boundedContext.plan.exception.PlanErrorCode;
import com.example.tnote.boundedContext.plan.exception.PlanException;
import com.example.tnote.boundedContext.plan.repository.PlanImageRepository;
import com.example.tnote.boundedContext.plan.repository.PlanRepository;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
public class PlanService {
    private final PlanRepository planRepository;
    private final PlanImageRepository planImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final AwsS3Uploader awsS3Uploader;

    public PlanService(final PlanRepository planRepository, final PlanImageRepository planImageRepository,
                       final UserRepository userRepository, final ScheduleRepository scheduleRepository,
                       final AwsS3Uploader awsS3Uploader) {
        this.planRepository = planRepository;
        this.planImageRepository = planImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public PlanResponse save(final Long userId, final Long scheduleId, final PlanSaveRequest registerRequest,
                             final List<MultipartFile> planImages) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);
        Plan plan = registerRequest.toEntity(user, schedule);

        if (plan.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || plan.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new PlanException(PlanErrorCode.INVALID_PLAN_DATE);
        }

        if (planImages != null && !planImages.isEmpty()) {
            List<PlanImage> uploadedImages = uploadPlanImages(plan, planImages);
            plan.getPlanImages().addAll(uploadedImages);
        }

        return PlanResponse.from(planRepository.save(plan));
    }

    public PlanResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<Plan> plans = planRepository.findALLByUserIdAndScheduleId(userId, scheduleId);
        Slice<Plan> planSlice = planRepository.findALLByUserIdAndScheduleId(userId, scheduleId, pageable);

        List<PlanResponse> responses = convertToPlanResponseList(planSlice);
        return PlanResponses.of(responses, plans, planSlice);
    }

    @Transactional
    public PlanDeleteResponse delete(final Long planId, final Long userId) {
        Plan plan = findByIdAndUserId(planId, userId);
        deleteExistedImage(plan);
        planRepository.delete(plan);

        return new PlanDeleteResponse(plan);
    }

    public PlanResponse find(final Long userId, final Long planId) {
        Plan plan = findByIdAndUserId(planId, userId);
        return PlanResponse.from(plan);
    }

    private List<PlanImage> uploadPlanImages(Plan plan, List<MultipartFile> planImages) {
        return planImages.stream()
                .map(file -> awsS3Uploader.upload(file, "plan"))
                .map(pair -> createPlanImage(plan, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private PlanImage createPlanImage(Plan plan, String imageUrl, String originalFileName) {
        plan.clearImages();

        return planImageRepository.save(new PlanImage(imageUrl, originalFileName, plan));
    }

    private void deleteExistedImage(Plan plan) {
        System.out.println("Deleting existing images for plan ID: " + plan.getId());
        deleteS3Images(plan);
    }

    private void deleteS3Images(Plan plan) {
        System.out.println("Starting to delete images from S3 for plan ID: " + plan.getId());
        List<PlanImage> planImages = plan.getPlanImages();
        for (PlanImage planImage : planImages) {
            String imageKey = planImage.getPlanImageUrl().substring(49);
            System.out.println("Deleting image from S3: " + imageKey);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private List<PlanResponse> convertToPlanResponseList(final Slice<Plan> planSlice) {
        return planSlice.getContent().stream()
                .map(PlanResponse::from)
                .toList();
    }

    private Plan findByIdAndUserId(final Long planId, final Long userId) {
        return planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new PlanException(PlanErrorCode.NOT_FOUND));
    }
}
