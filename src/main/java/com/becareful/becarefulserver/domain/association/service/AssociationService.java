package com.becareful.becarefulserver.domain.association.service;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.association.dto.AssociationSimpleDto;
import com.becareful.becarefulserver.domain.association.dto.JoinApplicationSimpleDto;
import com.becareful.becarefulserver.domain.association.dto.MemberSimpleDto;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationCreateRequest;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.repository.AssociationJoinApplicationRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.association.vo.AssociationJoinApplicationStatus;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.AssociationException;
import com.becareful.becarefulserver.global.exception.exception.ElderlyException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

import static com.becareful.becarefulserver.domain.community.domain.BoardType.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Service
@RequiredArgsConstructor
public class AssociationService {

    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final SocialWorkerRepository socialWorkerRepository;
    private final AssociationRepository associationRepository;
    private final AssociationJoinApplicationRepository associationMembershipRequestRepository;
    private final PostBoardRepository postBoardRepository;
    private final AssociationJoinApplicationRepository associationJoinApplicationRepository;

    @Transactional
    public void joinAssociation(AssociationJoinRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = associationRepository
                .findById(request.associationId())
                .orElseThrow(() -> new AssociationException(ASSOCIATION_NOT_EXISTS));

        AssociationJoinApplication newMembershipRequest = AssociationJoinApplication.create(
                association, currentSocialWorker, request.associationRank(), AssociationJoinApplicationStatus.PENDING);
        associationMembershipRequestRepository.save(newMembershipRequest);
    }

    // 협회 가입 신청 승인
    @Transactional
    public void acceptJoinAssociation(Long associationMembershipRequestId) {
        AssociationJoinApplication membershipRequest = associationMembershipRequestRepository
                .findById(associationMembershipRequestId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        membershipRequest.setStatus(AssociationJoinApplicationStatus.APPROVED);

        SocialWorker socialWorker = membershipRequest.getSocialWorker();
        socialWorker.joinAssociation(membershipRequest.getAssociation(), membershipRequest.getAssociationRank());
    }

    // 협회 가입 신청 반려(신청자가 반려사실을 확인하면 요청 레코드 삭제)
    @Transactional
    public void rejectJoinAssociation(Long associationMembershipRequestId) {
        AssociationJoinApplication membershipRequest = associationMembershipRequestRepository
                .findById(associationMembershipRequestId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        membershipRequest.setStatus(AssociationJoinApplicationStatus.REJECTED);
    }

    @Transactional
    public long saveAssociation(AssociationCreateRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association newAssociation =
                Association.create(request.name(), request.profileImageUrl(), request.establishedYear());
        associationRepository.save(newAssociation);

        currentSocialWorker.setAssociation(newAssociation);

        List<PostBoard> postBoards = createDefaultPostBoards(newAssociation);
        postBoardRepository.saveAll(postBoards);

        return newAssociation.getId();
    }

    @Transactional(readOnly = true)
    public AssociationMyResponse getMyAssociation() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();
        Association association = currentSocialWorker.getAssociation();
        int associationMemberCount = socialWorkerRepository.countByAssociation(association);

        return AssociationMyResponse.from(association, associationMemberCount);
    }

    public AssociationProfileImageUploadResponse uploadProfileImage(MultipartFile file) {
        try {
            String fileName = generateProfileImageFileName();
            String profileImageUrl = fileUtil.upload(file, "association-image", fileName);

            return new AssociationProfileImageUploadResponse(profileImageUrl);
        } catch (IOException e) {
            throw new ElderlyException(ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE);
        }
    }

    // 협회 회원 목록 overview
    @Transactional(readOnly = true)
    public AssociationMemberOverviewResponse getAssociationMemberOverview() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();
        Association association = currentSocialWorker.getAssociation();
        int associationMemberCount = socialWorkerRepository.countByAssociation(association);
        int joinApplicationCount = associationJoinApplicationRepository.countByAssociationAndStatus(
                association, AssociationJoinApplicationStatus.PENDING);

        return new AssociationMemberOverviewResponse(associationMemberCount, joinApplicationCount);
    }

    // 협회 회원 목록 반환
    @Transactional(readOnly = true)
    public AssociationMemberListResponse getAssociationMemberList() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = currentSocialWorker.getAssociation();
        int associationMemberCount = socialWorkerRepository.countByAssociation(association);

        List<SocialWorker> members = socialWorkerRepository.findAllByAssociation(association);
        List<MemberSimpleDto> memberSimpleDtos =
                members.stream().map(MemberSimpleDto::of).toList();

        return new AssociationMemberListResponse(associationMemberCount, memberSimpleDtos);
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

    // 협회 회원 상세정보 반환
    @Transactional(readOnly = true)
    public AssociationMemberDetailInfoResponse getAssociationMemberDetailInfo(Long memberId) {
        SocialWorker member = socialWorkerRepository
                .findById(memberId)
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));

        Association association = member.getAssociation();
        NursingInstitution institution = member.getNursingInstitution();
        Integer age = Period.between(member.getBirthday(), LocalDate.now()).getYears(); // 만나이 구하기

        return AssociationMemberDetailInfoResponse.of(member, age, institution, association);
    }

    // 회원을 협회에서 탈퇴 시키는 메서드. 회원정보를 삭제하는게 아님
    @Transactional
    public void expelMember(Long memberId) {
        SocialWorker member = socialWorkerRepository
                .findById(memberId)
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));

        member.leaveAssociation();
    }

    // TODO(파일이름 생성 로직 수정)
    private String generateProfileImageFileName() {
        return UUID.randomUUID().toString();
    }

    private List<PostBoard> createDefaultPostBoards(Association association) {
        return List.of(
                PostBoard.create(ASSOCIATION_NOTICE, AssociationRank.MEMBER, AssociationRank.MEMBER, association),
                PostBoard.create(SERVICE_NOTICE, AssociationRank.MEMBER, AssociationRank.MEMBER, association),
                PostBoard.create(INFORMATION_SHARING, AssociationRank.MEMBER, AssociationRank.MEMBER, association),
                PostBoard.create(
                        PARTICIPATION_APPLICATION, AssociationRank.MEMBER, AssociationRank.MEMBER, association));
    }

    public AssociationSearchListResponse searchAssociationByName(String associationName) {
        List<Association> associationList = associationName == null
                ? associationRepository.findAll()
                : associationRepository.findByNameContains(associationName);
        List<AssociationSimpleDto> associationSimpleInfoList = associationList.stream()
                .map(association -> {
                    int memberCount = socialWorkerRepository.countByAssociation(association);
                    return AssociationSimpleDto.of(association, memberCount);
                })
                .toList();
        return new AssociationSearchListResponse(associationList.size(), associationSimpleInfoList);
    }

    public AssociationSearchListResponse getAssociationList() {
        List<AssociationSimpleDto> associationSimpleDtoList = associationRepository.findAll().stream()
                .map(association -> {
                    int memberCount = socialWorkerRepository.countByAssociation(association);
                    return AssociationSimpleDto.of(association, memberCount);
                })
                .toList();
        return AssociationSearchListResponse.from(associationSimpleDtoList);
    }
}
