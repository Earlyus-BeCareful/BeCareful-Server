package com.becareful.becarefulserver.domain.chat.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.MATCHING_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import com.becareful.becarefulserver.domain.matching.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractInfoListResponse;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.domain.matching.repository.ContractRepository;
import com.becareful.becarefulserver.domain.matching.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.response.ChatList;
import com.becareful.becarefulserver.global.exception.exception.MatchingException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SocialWorkerChatService {

    private final AuthUtil authUtil;
    private final ContractRepository contractRepository;
    private final MatchingRepository matchingRepository;
    private final CompletedMatchingRepository completedMatchingRepository;

    @Transactional
    public ChatList getChatList() {
        SocialWorker socialworker = authUtil.getLoggedInSocialWorker();
        NursingInstitution nursingInstitution = socialworker.getNursingInstitution();
        List<Matching> matchingList = matchingRepository.findByNursingInstitution(nursingInstitution);

        List<ChatList.ChatroomInfo> chatroomInfoList = matchingList.stream()
                .map(matching -> {
                    // Elderly와 Caregiver 정보를 가져오기
                    Caregiver caregiver = matching.getWorkApplication().getCaregiver();
                    Elderly elderly = matching.getRecruitment().getElderly();
                    String timeDifference = getTimeDifferenceString(matching);
                    Contract latestContract = contractRepository
                            .findTop1ByMatchingOrderByCreateDateDesc(matching)
                            .get();
                    String recentChat = isContractInCompletedMatching(matching) ? "최종 승인이 확정되었습니다!" : "합격 축하드립니다.";

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

    public ContractInfoListResponse getChatRoomDetailData(Long matchingId) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();

        List<Contract> contracts = contractRepository.findByMatchingIdOrderByCreateDateAsc(matchingId);
        Matching matching =
                matchingRepository.findById(matchingId).orElseThrow(() -> new MatchingException(MATCHING_NOT_EXISTS));

        matching.validateSocialWorker(socialWorker.getId());

        return ContractInfoListResponse.of(matching, contracts);
    }

    // 직전 계약서 내용 불러오기
    @Transactional(readOnly = true)
    public ContractDetailResponse getContractDetail(Long contractId) {
        Contract contract = contractRepository
                .findById(contractId)
                .orElseThrow(() -> new IllegalArgumentException("Contract not found"));

        return ContractDetailResponse.from(
                contract.getMatching().getRecruitment().getElderly(),
                contract.getWorkDays().stream().toList(),
                contract.getWorkStartTime(),
                contract.getWorkEndTime(),
                contract.getWorkSalaryAmount(),
                contract.getWorkStartDate());
    }

    @Transactional
    public void editContract(ContractEditRequest request) {
        Matching matching = matchingRepository
                .findById(request.matchingId())
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        Contract contract = Contract.edit(
                matching,
                EnumSet.copyOf(request.workDays()),
                request.workStartTime(),
                request.workEndTime(),
                request.workSalaryUnitType(),
                request.workSalaryAmount(),
                request.workStartDate(),
                EnumSet.copyOf(request.careTypes()));
        contractRepository.save(contract);
    }

    private String getTimeDifferenceString(Matching matching) {
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

    private LocalDateTime findLatestContractCreatedDate(Matching matching) {
        Contract latestContract = contractRepository
                .findTop1ByMatchingOrderByCreateDateDesc(matching)
                .orElse(null);
        return latestContract != null ? latestContract.getCreateDate() : null;
    }

    private boolean isContractInCompletedMatching(Matching matching) {
        Contract latestContract = contractRepository
                .findTop1ByMatchingOrderByCreateDateDesc(matching)
                .orElse(null);
        if (latestContract != null) {
            return completedMatchingRepository.existsCompletedMatchingByContract(latestContract);
        }
        return false;
    }
}
