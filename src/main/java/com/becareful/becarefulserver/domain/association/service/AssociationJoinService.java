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

    @Transactional
    public void applyJoinAssociation(AssociationJoinRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = associationRepository
                .findById(request.associationId())
                .orElseThrow(() -> new AssociationException(ASSOCIATION_NOT_EXISTS));

        AssociationJoinApplication newMembershipRequest = AssociationJoinApplication.create(
                currentSocialWorker,
                association,
                request.associationRank(),
                request.isAgreedToTerms(),
                request.isAgreedToCollectPersonalInfo(),
                request.isAgreedToReceiveMarketingInfo());
        associationJoinApplicationRepository.save(newMembershipRequest);
    }

    @Transactional
    public void cancelMyJoinApplication() {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();
        AssociationJoinApplication application = associationJoinApplicationRepository
                .findBySocialWorker(loggedInSocialWorker)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        if (application.getStatus().equals(AssociationJoinApplicationStatus.APPROVED)) {
            throw new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_ACCEPTED);
        }
        if (application.getStatus().equals(AssociationJoinApplicationStatus.REJECTED)) {
            throw new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_REJECTED);
        }

        associationJoinApplicationRepository.delete(application);
    }

    // 협회 가입 요청 목록 반환
    @Transactional(readOnly = true)
    public AssociationJoinApplicationListResponse getAssociationJoinApplicationList() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

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

    // 협회 가입 신청 승인
    @Transactional
    public void acceptJoinAssociation(Long applicationId) {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        AssociationJoinApplication application = associationJoinApplicationRepository
                .findById(applicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        Association association = application.getAssociation();

        // TODO : 검증 로직 분리
        if (!loggedInAssociationMember.getAssociation().equals(association)) {
            throw new DomainException(ASSOCIATION_JOIN_APPLICATION_NOT_ACCEPTABLE_DIFFERENT_ASSOCIATION);
        }

        application.approve();

        SocialWorker socialWorker = application.getSocialWorker();
        AssociationMember associationMember = AssociationMember.create(
                socialWorker,
                association,
                application.getAssociationRank(),
                application.isAgreedToTerms(),
                application.isAgreedToCollectPersonalInfo(),
                application.isAgreedToReceiveMarketingInfo());

        associationMemberRepository.save(associationMember);
    }

    // 협회 가입 신청 반려(신청자가 반려사실을 확인하면 요청 레코드 삭제)
    @Transactional
    public void rejectJoinAssociation(Long associationJoinApplicationId) {
        AssociationJoinApplication joinApplication = associationJoinApplicationRepository
                .findById(associationJoinApplicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        joinApplication.reject();
    }
}
