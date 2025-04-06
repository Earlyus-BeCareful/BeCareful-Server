package com.becareful.becarefulserver.domain.socialworker.service;

import com.becareful.becarefulserver.domain.recruitment.domain.Matching;
import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.domain.Recruitment;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.recruitment.domain.Contract;
import com.becareful.becarefulserver.domain.recruitment.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.repository.ContractRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SimpleElderlyResponse;
import com.becareful.becarefulserver.domain.socialworker.dto.response.SocialWorkerHomeResponse;
import com.becareful.becarefulserver.domain.socialworker.repository.ElderlyRepository;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import com.becareful.becarefulserver.domain.socialworker.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialworkerRepository;
import com.becareful.becarefulserver.global.exception.exception.NursingInstitutionException;
import com.becareful.becarefulserver.global.exception.exception.SocialworkerException;
import com.becareful.becarefulserver.global.util.AuthUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialworkerService {
    private final SocialworkerRepository socialworkerRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;

    private final MatchingRepository matchingRepository;
    private final ContractRepository contractRepository;
    private final CompletedMatchingRepository completedMatchingRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthUtil authUtil;
    private final ElderlyRepository elderlyRepository;

    public SocialWorkerHomeResponse getHomeData() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        Integer elderlyCount = elderlyRepository.findByNursingInstitution(loggedInSocialWorker.getNursingInstitution()).size();
        Integer socialWorkerCount = socialworkerRepository.countByNursingInstitution(loggedInSocialWorker.getNursingInstitution());

        List<Long> elderlyIds = elderlyRepository.findByNursingInstitution(loggedInSocialWorker.getNursingInstitution()).stream()
                .map(Elderly::getId)
                .toList();

        List<Matching> matchingList = matchingRepository.findAllMatchingByElderlyIds(elderlyIds);

        Long processingMatchingCount = matchingList.stream()
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.지원))
                .count();

        Long recentlyMatchedCount = matchingList.stream()
                .filter(matching -> matching.getUpdateDate().isAfter(LocalDateTime.now().minusDays(7)))
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.합격))
                .count();

        Integer totalMatchedCount = matchingList.size();

        Integer appliedCaregiverCount = matchingList.stream()
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.지원))
                .map(matching -> matching.getWorkApplication().getId())
                .collect(Collectors.toSet())
                .size();

        long completedRecruitmentCount = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(recruitment -> !recruitment.isRecruiting())
                .count();

        long wholeApplierCountForCompletedRecruitment = matchingList.stream()
                .filter(matching -> !matching.getRecruitment().isRecruiting())
                .filter(matching -> matching.getMatchingStatus().equals(MatchingStatus.합격) || matching.getMatchingStatus().equals(MatchingStatus.불합격))
                .count();

        long wholeCompletedMatchingCount = matchingList.stream()
                .filter(matching -> !matching.getRecruitment().isRecruiting())
                .count();

        List<SimpleElderlyResponse> elderlyList = matchingList.stream()
                .map(Matching::getRecruitment)
                .filter(Recruitment::isRecruiting)
                .map(Recruitment::getElderly)
                .map(SimpleElderlyResponse::from)
                .toList();

        return SocialWorkerHomeResponse.of(loggedInSocialWorker,
                elderlyCount,
                socialWorkerCount,
                processingMatchingCount,
                recentlyMatchedCount,
                totalMatchedCount,
                appliedCaregiverCount,
                ((double) wholeApplierCountForCompletedRecruitment / completedRecruitmentCount) * 100,
                ((double) wholeApplierCountForCompletedRecruitment / wholeCompletedMatchingCount) * 100,
                elderlyList);
    }


    @Transactional
    public Long saveSocialworker(SocialworkerCreateRequest request) {

        validateEssentialAgreement(request.isAgreedToTerms(), request.isAgreedToCollectPersonalInfo());

        NursingInstitution institution = nursingInstitutionRepository.findById(request.institutionId())
                .orElseThrow(() -> new NursingInstitutionException(NURSING_INSTITUTION_NOT_FOUND));

        if (socialworkerRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new SocialworkerException(SOCIALWORKER_ALREADY_EXISTS);
        }

        // Socialworker 엔티티 생성
        SocialWorker socialworker = SocialWorker.create(
                request.name(), request.gender(), request.phoneNumber(),
                getEncodedPassword(request.password()),
                institution,
                request.rank(),
                request.isAgreedToReceiveMarketingInfo()
        );

        socialworkerRepository.save(socialworker);

        return socialworker.getId();
    }

    @Transactional
    public ChatList getChatList(){
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        NursingInstitution nursingInstitution = socialworker.getNursingInstitution();
        List<Matching> matchingList = matchingRepository.findByNursingInstitution(nursingInstitution);

        List<ChatList.ChatroomInfo> chatroomInfoList = matchingList.stream()
                .map(matching -> {
                    // Elderly와 Caregiver 정보를 가져오기
                    Caregiver caregiver = matching.getWorkApplication().getCaregiver();
                    Elderly elderly = matching.getRecruitment().getElderly();
                    String timeDifference = getTimeDifferenceString(matching);
                    Contract latestContract = contractRepository.findLatestContractByMatching(matching).get(0);
                    String recentChat = isContractInCompletedMatching(matching)
                            ? "최종 승인이 확정되었습니다!"
                            : "합격 축하드립니다.";

                    // ChatroomInfo 생성
                    return new ChatList.ChatroomInfo(
                            matching.getId(),
                            caregiver.getProfileImageUrl(), // 어르신 프로필 이미지 URL
                            caregiver.getName(), // 요양보호자 이름
                            recentChat, // 최근 채팅
                            timeDifference,
                            elderly.getName(), // 어르신 이름
                            elderly.getAge(), // 어르신 나이
                            elderly.getGender() // 어르신 성별
                    );
                })
                .collect(Collectors.toList());

        // ChatList 반환
        return new ChatList(chatroomInfoList);

    }

    public LocalDateTime findLatestContractCreatedDate(Matching matching) {
        List<Contract> contracts = contractRepository.findLatestContractByMatching(matching);
        Contract latestContract = contracts.isEmpty() ? null : contracts.get(0);
        return latestContract != null ? latestContract.getCreateDate() : null;
    }

    public boolean isContractInCompletedMatching(Matching matching) {
        List<Contract> contracts = contractRepository.findLatestContractByMatching(matching);
        Contract latestContract = contracts.isEmpty() ? null : contracts.get(0);
        if (latestContract != null) {
            return completedMatchingRepository.existsInCompletedMatching(latestContract.getId());
        }
        return false;
    }

    public String getTimeDifferenceString(Matching matching) {
        // 현재 시간
        LocalDateTime currentTime = LocalDateTime.now();

        // 가장 최신 Contract의 생성 시간
        LocalDateTime contractCreatedTime = findLatestContractCreatedDate(matching);

        // Duration을 사용하여 차이 계산
        Duration duration = Duration.between(contractCreatedTime, currentTime);

        // 차이에 따라 다른 시간 단위로 변환
        if (duration.toHours() < 1) {
            // 1시간 이내이면 분 단위로 반환
            long minutes = duration.toMinutes();
            return minutes + "분 전";
        } else if (duration.toDays() < 1) {
            // 1일 이내이면 시간 단위로 반환
            long hours = duration.toHours();
            return hours + "시간 전";
        } else {
            // 1일 이상이면 일 단위로 반환
            long days = duration.toDays();
            return days + "일 전";
        }
    }

    private String getEncodedPassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    private void validateEssentialAgreement(boolean isAgreedToTerms,
                                            boolean isAgreedToCollectPersonalInfo) {
        if (isAgreedToTerms && isAgreedToCollectPersonalInfo) {
            return;
        }

        throw new SocialworkerException(SOCIALWORKER_REQUIRED_AGREEMENT);
    }
}