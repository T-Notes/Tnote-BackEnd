package com.example.tnote.boundedContext.plan.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.boundedContext.plan.dto.PlanDeleteResponse;
import com.example.tnote.boundedContext.plan.dto.PlanResponses;
import com.example.tnote.boundedContext.plan.dto.PlanSaveRequest;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.dto.PlanUpdateRequest;
import com.example.tnote.boundedContext.plan.entity.Plan;
import com.example.tnote.boundedContext.plan.entity.PlanImage;
import com.example.tnote.boundedContext.plan.exception.PlanErrorCode;
import com.example.tnote.boundedContext.plan.exception.PlanException;
import com.example.tnote.boundedContext.plan.repository.PlanImageRepository;
import com.example.tnote.boundedContext.plan.repository.PlanRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
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
    private final RecentLogService recentLogService;

    public PlanService(final PlanRepository planRepository, final PlanImageRepository planImageRepository,
                       final UserRepository userRepository, final ScheduleRepository scheduleRepository,
                       final AwsS3Uploader awsS3Uploader,final RecentLogService recentLogService) {
        this.planRepository = planRepository;
        this.planImageRepository = planImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.awsS3Uploader = awsS3Uploader;
        this.recentLogService = recentLogService;
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
        recentLogService.save(userId, plan.getId(), scheduleId,"PLAN");
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
        recentLogService.delete(planId,"PLAN");

        return new PlanDeleteResponse(planId);
    }

    @Transactional
    public PlanResponse find(final Long userId, final Long planId) {
        Plan plan = findByIdAndUserId(planId, userId);
        recentLogService.save(userId,planId,plan.getSchedule().getId(),"PLAN");
        return PlanResponse.from(plan);
    }

    @Transactional
    public PlanResponse updatePlan(final Long userId, final Long planId, final PlanUpdateRequest updateRequest,
                                   final List<MultipartFile> planImages) {
        Plan plan = findByIdAndUserId(planId, userId);

        plan.updateFields(
                updateRequest.getTitle(),
                updateRequest.getStartDate(),
                updateRequest.getEndDate(),
                updateRequest.getLocation(),
                updateRequest.getContents(),
                updateRequest.getParticipants()
        );
        updateImage(plan, planImages);

        return PlanResponse.from(plan);
    }

    private void updateImage(Plan plan, List<MultipartFile> planImages) {
        if (planImages == null || planImages.isEmpty()) {
            deleteExistedImages(plan);
        }
        if (planImages != null && !planImages.isEmpty()) {
            List<PlanImage> updatedImages = deleteExistedImagesAndUploadNewImages(plan, planImages);
            plan.updateImages(updatedImages);
        }
    }

    private List<PlanImage> uploadPlanImages(final Plan plan, final List<MultipartFile> planImages) {
        return planImages.stream()
                .map(file -> awsS3Uploader.upload(file, "plan"))
                .map(pair -> createPlanImage(plan, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private PlanImage createPlanImage(final Plan plan, final String imageUrl, final String originalFileName) {
        plan.clearImages();

        return planImageRepository.save(new PlanImage(imageUrl, originalFileName, plan));
    }

    private void deleteExistedImage(final Plan plan) {
        System.out.println("Deleting existing images for plan ID: " + plan.getId());
        deleteS3Images(plan);
    }

    private void deleteExistedImages(final Plan plan) {
        System.out.println("Deleting existing images for plan ID: " + plan.getId());
        deleteS3Images(plan);
        planImageRepository.deleteByPlanId(plan.getId());
    }

    private List<PlanImage> deleteExistedImagesAndUploadNewImages(final Plan plan,
                                                                  final List<MultipartFile> planImages) {
        deleteExistedImages(plan);
        return uploadImages(plan, planImages);
    }

    private void deleteS3Images(final Plan plan) {
        System.out.println("Starting to delete images from S3 for plan ID: " + plan.getId());
        List<PlanImage> planImages = plan.getPlanImages();
        for (PlanImage planImage : planImages) {
            String imageKey = planImage.getPlanImageUrl().substring(49);
            System.out.println("Deleting image from S3: " + imageKey);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private List<PlanImage> uploadImages(final Plan plan, final List<MultipartFile> planImages) {
        return planImages.stream()
                .map(file -> awsS3Uploader.upload(file, "plan"))
                .map(pair -> createPlanImage(plan, pair.getFirst(), pair.getSecond()))
                .toList();
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
