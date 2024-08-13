package com.example.tnote.boundedContext.plan.service;

import com.example.tnote.base.utils.AwsS3Uploader;
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

    public PlanService(PlanRepository planRepository, PlanImageRepository planImageRepository,
                       UserRepository userRepository, ScheduleRepository scheduleRepository,
                       AwsS3Uploader awsS3Uploader) {
        this.planRepository = planRepository;
        this.planImageRepository = planImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public PlanResponse save(final Long userId, final Long scheduleId, final PlanSaveRequest registerRequest,
                             List<MultipartFile> planImages) {
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

    private List<PlanImage> uploadPlanImages(Plan plan, List<MultipartFile> planImages) {
        return planImages.stream()
                .map(file -> awsS3Uploader.upload(file, "classLog"))
                .map(pair -> createPlanImage(plan, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private PlanImage createPlanImage(Plan plan, String imageUrl, String originalFileName) {
        plan.clearImages();

        return planImageRepository.save(new PlanImage(imageUrl, originalFileName, plan));
    }

    private List<PlanResponse> convertToPlanResponseList(final Slice<Plan> planSlice) {
        return planSlice.getContent().stream()
                .map(PlanResponse::from)
                .toList();
    }
}
