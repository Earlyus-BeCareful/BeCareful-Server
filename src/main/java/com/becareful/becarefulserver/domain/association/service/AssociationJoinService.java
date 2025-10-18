package com.becareful.becarefulserver.domain.association.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationJoinApplicationStatus;
import com.becareful.becarefulserver.domain.association.dto.JoinApplicationSimpleDto;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationJoinApplicationListResponse;
import com.becareful.becarefulserver.domain.association.repository.AssociationJoinApplicationRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.AssociationException;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssociationJoinService {

    private final AuthUtil authUtil;
    private final AssociationRepository associationRepository;
    private final AssociationJoinApplicationRepository associationJoinApplicationRepository;
    private final AssociationMemberRepository associationMemberRepository;

    /**
     * 협회 가입 신청 - 협회 가입 전이므로, 현재 로그인 정보는 Social Worker 로 조회
     * @param request 협회 가입 신청 DTO
     */
    @Transactional
    public void applyJoinAssociation(AssociationJoinRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = associationRepository
                .findById(request.associationId())
                .orElseThrow(() -> new AssociationException(ASSOCIATION_NOT_EXISTS));

        AssociationJoinApplication newMembershipRequest = AssociationJoinApplication.create(
                loggedInSocialWorker,
                association,
                request.associationRank(),
                request.isAgreedToTerms(),
                request.isAgreedToCollectPersonalInfo(),
                request.isAgreedToReceiveMarketingInfo());
        associationJoinApplicationRepository.save(newMembershipRequest);
    }

    /**
     * 협회 가입 신청 취소 - 협회 가입 전이므로, 현재 로그인 정보는 Social Worker 로 조회
     */
    @Transactional
    public void cancelMyJoinApplication() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        AssociationJoinApplication application = associationJoinApplicationRepository
                .findBySocialWorker(loggedInSocialWorker)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_JOIN_APPLICATION_NOT_EXISTS));

        if (application.getStatus().equals(AssociationJoinApplicationStatus.APPROVED)) {
            throw new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_ACCEPTED);
        }
        if (application.getStatus().equals(AssociationJoinApplicationStatus.REJECTED)) {
            throw new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_REJECTED);
        }

        associationJoinApplicationRepository.delete(application);
    }

    /**
     * 협회 가입 신청서 리스트 조회 - 협회 관리자가 호출하므로, 로그인 정보는 Association Member 로 조회
     * @return AssociationJoinApplicationListResponse
     */
    @Transactional(readOnly = true)
    public AssociationJoinApplicationListResponse getAssociationJoinApplicationList() {
        AssociationMember currentSocialWorker = authUtil.getLoggedInAssociationMember();

        Association association = currentSocialWorker.getAssociation();
        int joinApplicationCount = associationJoinApplicationRepository.countByAssociationAndStatus(
                association, AssociationJoinApplicationStatus.PENDING);

        List<AssociationJoinApplication> applications =
                associationJoinApplicationRepository.findAllByAssociationAndStatus(
                        association, AssociationJoinApplicationStatus.PENDING);
        List<JoinApplicationSimpleDto> applicationDtos =
                applications.stream().map(JoinApplicationSimpleDto::of).toList();
        return new AssociationJoinApplicationListResponse(joinApplicationCount, applicationDtos);
    }

    /**
     * 협회 가입 신청 승인 - 협회 관리자가 호출하므로, 로그인 정보는 Association Member 로 조회
     * @param applicationId 신청서 ID
     */
    @Transactional
    public void acceptJoinAssociation(Long applicationId) {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        AssociationJoinApplication application = associationJoinApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_JOIN_APPLICATION_NOT_EXISTS));

        Association association = application.getAssociation();

        // TODO : 검증 로직 분리
        if (!loggedInAssociationMember.getAssociation().equals(association)) {
            throw new DomainException(ASSOCIATION_JOIN_APPLICATION_NOT_ACCEPTABLE_DIFFERENT_ASSOCIATION);
        }

        application.approve();

        AssociationMember associationMember = AssociationMember.create(
                application.getSocialWorker(),
                association,
                application.getAssociationRank(),
                application.isAgreedToTerms(),
                application.isAgreedToCollectPersonalInfo(),
                application.isAgreedToReceiveMarketingInfo());

        associationMemberRepository.save(associationMember);
    }

    /**
     * 협회 가입 신청 반려 - 협회 관리자가 호출하므로, 로그인 정보는 Association Member 로 조회
     * (신청자가 반려사실을 확인하면 요청 레코드 삭제)
     * @param applicationId 신청서 ID
     */
    @Transactional
    public void rejectJoinAssociation(Long applicationId) {
        AssociationJoinApplication joinApplication = associationJoinApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_JOIN_APPLICATION_NOT_EXISTS));

        joinApplication.reject();
    }
}
