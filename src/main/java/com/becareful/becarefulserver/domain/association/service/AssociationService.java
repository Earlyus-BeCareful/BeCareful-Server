package com.becareful.becarefulserver.domain.association.service;

import static com.becareful.becarefulserver.domain.community.domain.BoardType.*;
import static com.becareful.becarefulserver.domain.community.domain.BoardType.PARTICIPATION_APPLICATION;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMembershipRequest;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationCreateRequest;
import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationProfileImageUploadResponse;
import com.becareful.becarefulserver.domain.association.repository.AssociationMembershipRequestRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.association.vo.AssociationJoinRequestStatus;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.community.repository.PostBoardRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.AssociationException;
import com.becareful.becarefulserver.global.exception.exception.ElderlyException;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.FileUtil;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class AssociationService {

    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final SocialWorkerRepository socialWorkerRepository;
    private final AssociationRepository associationRepository;
    private final AssociationMembershipRequestRepository associationMembershipRequestRepository;
    private final PostBoardRepository postBoardRepository;

    @Transactional
    public void joinAssociation(AssociationJoinRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = associationRepository
                .findById(request.associationId())
                .orElseThrow(() -> new AssociationException(ASSOCIATION_NOT_EXISTS));

        AssociationMembershipRequest newMembershipRequest = AssociationMembershipRequest.create(
                association, currentSocialWorker, request.associationRank(), AssociationJoinRequestStatus.PENDING);
        associationMembershipRequestRepository.save(newMembershipRequest);
    }

    // 협회 가입 신청 승인
    @Transactional
    public void accpetJoinAssociation(Long associationMembershipRequestId) {
        AssociationMembershipRequest membershipRequest = associationMembershipRequestRepository
                .findById(associationMembershipRequestId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        membershipRequest.setStatus(AssociationJoinRequestStatus.APPROVED);

        SocialWorker socialWorker = membershipRequest.getSocialWorker();
        socialWorker.joinAssociation(membershipRequest.getAssociation(), membershipRequest.getAssociationRank());
    }

    // 협회 가입 신청 반려(신청자가 반려사실을 확인하면 요청 레코드 삭제)
    @Transactional
    public void rejectJoinAssociation(Long associationMembershipRequestId) {
        AssociationMembershipRequest membershipRequest = associationMembershipRequestRepository
                .findById(associationMembershipRequestId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        membershipRequest.setStatus(AssociationJoinRequestStatus.REJECTED);
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

    // TODO(회원 목록 반환)

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
}
