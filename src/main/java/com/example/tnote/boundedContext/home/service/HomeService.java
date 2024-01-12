package com.example.tnote.boundedContext.home.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.home.repository.ConsultationQueryRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final ConsultationQueryRepository consultationQueryRepository;
    // private final ObservationQueryRepository observationQueryRepository;
    private final UserRepository userRepository;

    public List<ConsultationResponseDto> findAllOfConsultation(String studentName, PrincipalDetails user) {

        if (!checkCurrentUser(user.getId())) {
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        List<Consultation> consultations = consultationQueryRepository.findAll(studentName);

        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    // TODO : observation 다 만들어지면 이거 열면 된다
//    public List<ObservationResponseDto> findAllOfObservation(String studentName, PrincipalDetails user) {

//        if (!checkCurrentUser(user.getId())) {
//            throw new UserException(UserErrorResult.USER_NOT_FOUND);
//        }
//        List<Observation> observations = observationQueryRepository.findAll(studentName);
//
//        return observations.stream()
//                .map(ObservationResponseDto::of)
//                .toList();
//    }

    private Boolean checkCurrentUser(Long id) {
        Optional<User> currentUser = userRepository.findById(id);

        return currentUser.isPresent();
    }

}
