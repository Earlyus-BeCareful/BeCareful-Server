package com.becareful.becarefulserver.domain.association.service;

import static com.becareful.becarefulserver.domain.community.domain.BoardType.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.association.domain.vo.*;
import com.becareful.becarefulserver.domain.association.dto.*;
import com.becareful.becarefulserver.domain.association.dto.request.*;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.repository.*;
import com.becareful.becarefulserver.domain.community.domain.*;
import com.becareful.becarefulserver.domain.community.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.properties.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.io.*;
import java.util.*;
import lombok.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.springframework.web.multipart.*;

@Service
@RequiredArgsConstructor
public class AssociationService {

    private final FileUtil fileUtil;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtProperties jwtProperties;
    private final SocialWorkerRepository socialWorkerRepository;
    private final AssociationRepository associationRepository;
    private final PostBoardRepository postBoardRepository;
    private final AssociationJoinApplicationRepository associationJoinApplicationRepository;
    private final AssociationMemberRepository associationMemberRepository;

    @Transactional
    public void joinAssociation(AssociationJoinRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association = associationRepository
                .findById(request.associationId())
                .orElseThrow(() -> new AssociationException(ASSOCIATION_NOT_EXISTS));

        if (!request.isAgreedToTerms() || !request.isAgreedToCollectPersonalInfo()) {
            throw new DomainException(COMMUNITY_REQUIRED_AGREEMENT_NOT_AGREED);
        }

        AssociationJoinApplication newMembershipRequest = AssociationJoinApplication.create(
                currentSocialWorker, association, request.associationRank(), request.isAgreedToReceiveMarketingInfo());
        associationJoinApplicationRepository.save(newMembershipRequest);
    }

    // 협회 가입 신청 승인
    @Transactional
    public void acceptJoinAssociation(Long associationJoinApplicationId) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        AssociationJoinApplication joinApplication = associationJoinApplicationRepository
                .findById(associationJoinApplicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        currentMember.validateAssociation(joinApplication.getAssociation());

        joinApplication.approve();

        SocialWorker socialWorker = joinApplication.getSocialWorker();

        AssociationMember member = AssociationMember.createMember(
                joinApplication.getSocialWorker(),
                joinApplication.getAssociation(),
                joinApplication.getAssociationRank(),
                joinApplication.isAgreedToTerms(),
                joinApplication.isAgreedToCollectPersonalInfo(),
                joinApplication.isAgreedToReceiveMarketingInfo());
        socialWorker.joinAssociation(member);
    }

    // 협회 가입 신청 반려(신청자가 반려사실을 확인하면 요청 레코드 삭제)
    @Transactional
    public void rejectJoinAssociation(Long associationJoinApplicationId) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        AssociationJoinApplication joinApplication = associationJoinApplicationRepository
                .findById(associationJoinApplicationId)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS));

        currentMember.validateAssociation(joinApplication.getAssociation());

        joinApplication.reject();
    }

    @Transactional
    public long createAssociation(AssociationCreateRequest request) {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        Association newAssociation =
                Association.create(request.name(), request.profileImageUrl(), request.establishedYear());
        associationRepository.save(newAssociation);

        if (!request.isAgreedToTerms() || !request.isAgreedToCollectPersonalInfo()) {
            throw new DomainException(COMMUNITY_REQUIRED_AGREEMENT_NOT_AGREED);
        }

        AssociationMember newMember = AssociationMember.createChairman(
                currentSocialWorker, newAssociation, true, true, request.isAgreedToReceiveMarketingInfo());

        currentSocialWorker.joinAssociation(newMember);

        List<PostBoard> postBoards = createDefaultPostBoards(newAssociation);
        postBoardRepository.saveAll(postBoards);

        return newAssociation.getId();
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
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        int associationMemberCount = associationMemberRepository.countByAssociation(association);
        int joinApplicationCount = associationJoinApplicationRepository.countByAssociationAndStatus(
                association, AssociationJoinApplicationStatus.PENDING);

        return new AssociationMemberOverviewResponse(associationMemberCount, joinApplicationCount);
    }

    // 협회 회원 목록 반환
    @Transactional(readOnly = true)
    public AssociationMemberListResponse getAssociationMemberList() {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        int associationMemberCount = associationMemberRepository.countByAssociation(association);

        List<AssociationMember> members = associationMemberRepository.findAllByAssociation(association);
        List<AssociationMemberSimpleDto> associationMemberSimpleDtos =
                members.stream().map(AssociationMemberSimpleDto::from).toList();

        return new AssociationMemberListResponse(associationMemberCount, associationMemberSimpleDtos);
    }

    // 협회 가입 요청 목록 반환
    @Transactional(readOnly = true)
    public AssociationJoinApplicationListResponse getAssociationJoinApplicationList() {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        int joinApplicationCount = associationJoinApplicationRepository.countByAssociationAndStatus(
                association, AssociationJoinApplicationStatus.PENDING);

        List<AssociationJoinApplication> applications =
                associationJoinApplicationRepository.findAllByAssociationAndStatus(
                        association, AssociationJoinApplicationStatus.PENDING);
        List<JoinApplicationSimpleDto> applicationDtos =
                applications.stream().map(JoinApplicationSimpleDto::from).toList();
        return new AssociationJoinApplicationListResponse(joinApplicationCount, applicationDtos);
    }

    // 협회 회원 상세정보 반환
    @Transactional(readOnly = true)
    public AssociationMemberDetailInfoResponse getAssociationMemberDetailInfo(Long memberId) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        AssociationMember findMember = associationMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new DomainException(ASSOCIATION_MEMBER_NOT_EXISTS));

        currentMember.validateAssociation(findMember.getAssociation());

        return AssociationMemberDetailInfoResponse.from(findMember);
    }

    @Transactional
    public void leaveAssociation(HttpServletResponse response) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();
        Association association = currentMember.getAssociation();
        AssociationRank currentRank = currentMember.getAssociationRank();

        if (currentMember.getAssociationRank() == AssociationRank.CHAIRMAN) {
            throw new DomainException("협회장은 탈퇴할 수 없습니다.");
        }
        if (currentRank.equals(AssociationRank.EXECUTIVE)) {
            int executiveCount = associationMemberRepository.countByAssociationAndAssociationRank(
                    association, AssociationRank.EXECUTIVE);
            if (executiveCount <= 1) {
                throw new DomainException("최소 한 명의 임원진이 유지되어야 합니다.");
            }
        }

        currentSocialWorker.leaveAssociation();

        updateJwtAndSecurityContext(
                response,
                currentMember.getPhoneNumber(),
                currentMember.getInstitutionRank(),
                currentMember.getAssociationRank());
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

    @Transactional(readOnly = true)
    public AssociationSearchListResponse searchAssociationByName(String associationName) {
        List<Association> associationList = associationName == null
                ? associationRepository.findAll()
                : associationRepository.findByNameContains(associationName);
        List<AssociationResponse> associationSimpleInfoList = associationList.stream()
                .map(association -> {
                    int memberCount = associationMemberRepository.countByAssociation(association);
                    return AssociationResponse.of(association, memberCount);
                })
                .toList();
        return new AssociationSearchListResponse(associationList.size(), associationSimpleInfoList);
    }

    @Transactional(readOnly = true)
    public AssociationSearchListResponse getAssociationList() {
        List<AssociationResponse> associationResponseList = associationRepository.findAll().stream()
                .map(association -> {
                    int memberCount = associationMemberRepository.countByAssociation(association);
                    return AssociationResponse.of(association, memberCount);
                })
                .toList();
        return AssociationSearchListResponse.from(associationResponseList);
    }

    @Transactional(readOnly = true)
    public AssociationInfoResponse getAssociationInfo() {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();
        AssociationMember chairman = associationMemberRepository
                .findByAssociationAndAssociationRank(association, AssociationRank.CHAIRMAN)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_CHAIRMAN_NOT_EXISTS));
        int memberCount = associationMemberRepository.countByAssociation(association);

        return AssociationInfoResponse.of(association, memberCount, chairman);
    }

    @Transactional
    public void updateAssociationInfo(@Valid UpdateAssociationInfoRequest request) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        association.updateAssociationInfo(request);
    }

    @Transactional
    public void updateAssociationRank(@Valid UpdateAssociationRankRequest request) {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();
        Association association = currentMember.getAssociation();

        AssociationMember member = associationMemberRepository
                .findById(request.memberId())
                .orElseThrow(() -> new DomainException(ASSOCIATION_MEMBER_NOT_EXISTS));

        currentMember.validateAssociation(member.getAssociation());

        AssociationRank currentRank = member.getAssociationRank();
        AssociationRank targetRank = request.associationRank();

        if (currentRank.equals(AssociationRank.CHAIRMAN)) {
            throw new DomainException("협회장의 회원 유형은 협회장만 수정할 수 있습니다. 협회장인 경우 다른 페이지에서 수정해주시기 바랍니다.");
        }

        if (currentRank.equals(AssociationRank.EXECUTIVE) && targetRank.equals(AssociationRank.MEMBER)) {
            int executiveCount = associationMemberRepository.countByAssociationAndAssociationRank(
                    association, AssociationRank.EXECUTIVE);
            if (executiveCount <= 1) {
                throw new DomainException("최소 한 명의 임원진이 유지되어야 합니다.");
            }
        }

        member.updateAssociationRank(request.associationRank());
    }

    @Transactional
    public void updateAssociationChairman(
            @Valid UpdateAssociationChairmanRequest request, HttpServletResponse response) {
        AssociationMember currentChairman = authUtil.getLoggedInAssociationMember();
        AssociationMember newChairman = associationMemberRepository
                .findByIdAndName(request.newChairmanId(), request.newChairmanName())
                .orElseThrow(() -> new NotFoundException("회원 정보를 잘못 입력하였습니다."));

        currentChairman.updateAssociationRank(request.nextRankOfCurrentChairman());
        newChairman.updateAssociationRank(AssociationRank.CHAIRMAN);

        updateJwtAndSecurityContext(
                response,
                currentChairman.getPhoneNumber(),
                currentChairman.getInstitutionRank(),
                request.nextRankOfCurrentChairman());
    }

    private void updateJwtAndSecurityContext(
            HttpServletResponse response,
            String phoneNumber,
            InstitutionRank institutionRankParam,
            AssociationRank associationRankParam) {
        String institutionRank = institutionRankParam.toString();
        String associationRank = associationRankParam.toString();
        String accessToken = jwtUtil.createAccessToken(phoneNumber, institutionRank, associationRank);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(cookieUtil.createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry()));
        response.addCookie(
                cookieUtil.createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry()));

        List<GrantedAuthority> authorities =
                List.of((GrantedAuthority) () -> institutionRank, (GrantedAuthority) () -> associationRank);

        Authentication auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Transactional
    public void cancelMyJoinRequest() {
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
}
