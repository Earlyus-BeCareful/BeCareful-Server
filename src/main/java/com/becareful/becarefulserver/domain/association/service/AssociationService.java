package com.becareful.becarefulserver.domain.association.service;

import static com.becareful.becarefulserver.domain.community.domain.BoardType.*;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.association.domain.vo.*;
import com.becareful.becarefulserver.domain.association.dto.request.*;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.repository.*;
import com.becareful.becarefulserver.domain.community.domain.*;
import com.becareful.becarefulserver.domain.community.repository.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.properties.*;
import com.becareful.becarefulserver.global.util.*;
import jakarta.servlet.http.*;
import jakarta.validation.*;
import java.io.*;
import java.time.*;
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
    public Long createAssociation(AssociationCreateRequest request) {
        SocialWorker loggedInSocialWorker = authUtil.getLoggedInSocialWorker();

        Association association =
                Association.create(request.name(), request.profileImageUrl(), request.establishedYear());
        associationRepository.save(association);

        List<PostBoard> postBoards = createDefaultPostBoards(association);
        postBoardRepository.saveAll(postBoards);

        AssociationMember associationMember = AssociationMember.create(
                loggedInSocialWorker,
                association,
                AssociationRank.CHAIRMAN,
                request.isAgreedToTerms(),
                request.isAgreedToCollectPersonalInfo(),
                request.isAgreedToReceiveMarketingInfo());
        associationMemberRepository.save(associationMember);

        return association.getId();
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
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        Association association = loggedInAssociationMember.getAssociation();
        int associationMemberCount = associationMemberRepository.countByAssociation(association);
        int joinApplicationCount = associationJoinApplicationRepository.countByAssociationAndStatus(
                association, AssociationJoinApplicationStatus.PENDING);

        return new AssociationMemberOverviewResponse(associationMemberCount, joinApplicationCount);
    }

    // 협회 회원 목록 반환
    @Transactional(readOnly = true)
    public AssociationMemberListResponse getAssociationMemberList() {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();

        Association association = loggedInAssociationMember.getAssociation();
        List<AssociationMemberResponse> associationMemberResponses =
                associationMemberRepository.findAllByAssociation(association).stream()
                        .map(AssociationMemberResponse::from)
                        .toList();

        return AssociationMemberListResponse.from(associationMemberResponses);
    }

    // 협회 회원 상세정보 반환
    @Transactional(readOnly = true)
    public AssociationMemberDetailInfoResponse getAssociationMemberDetailInfo(Long memberId) {
        AssociationMember member = associationMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new SocialWorkerException(ASSOCIATION_MEMBER_NOT_EXISTS));

        Association association = member.getAssociation();
        NursingInstitution institution = member.getNursingInstitution();
        Integer age = Period.between(member.getBirthday(), LocalDate.now()).getYears(); // 만나이 구하기

        return AssociationMemberDetailInfoResponse.of(member, age, institution, association);
    }

    @Transactional
    public void leaveAssociation(HttpServletResponse response) {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        Association association = loggedInAssociationMember.getAssociation();
        AssociationRank currentRank = loggedInAssociationMember.getAssociationRank();

        if (loggedInAssociationMember.getAssociationRank() == AssociationRank.CHAIRMAN) {
            throw new DomainException("협회장은 탈퇴할 수 없습니다.");
        }
        if (currentRank.equals(AssociationRank.EXECUTIVE)) {
            int executiveCount = associationMemberRepository.countByAssociationAndAssociationRank(
                    association, AssociationRank.EXECUTIVE);
            if (executiveCount <= 1) {
                throw new DomainException("최소 한 명의 임원진이 유지되어야 합니다.");
            }
        }

        loggedInAssociationMember.leaveAssociation();

        updateJwtAndSecurityContext(
                response,
                loggedInAssociationMember.getPhoneNumber(),
                loggedInAssociationMember.getInstitutionRank(),
                loggedInAssociationMember.getAssociationRank());
    }

    // 회원을 협회에서 탈퇴 시키는 메서드. 회원정보를 삭제하는게 아님
    @Transactional
    public void expelMember(Long memberId) {
        // TODO : 권한 및 같은 협회 검증
        AssociationMember member = associationMemberRepository
                .findById(memberId)
                .orElseThrow(() -> new SocialWorkerException(ASSOCIATION_MEMBER_NOT_EXISTS));

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
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        Association association = loggedInAssociationMember.getAssociation();
        AssociationMember chairman = associationMemberRepository
                .findByAssociationAndAssociationRank(association, AssociationRank.CHAIRMAN)
                .orElseThrow(() -> new AssociationException(ASSOCIATION_CHAIRMAN_NOT_EXISTS));
        int memberCount = associationMemberRepository.countByAssociation(association);

        return AssociationInfoResponse.of(association, memberCount, chairman);
    }

    @Transactional
    public void updateAssociationInfo(@Valid UpdateAssociationInfoRequest request) {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();
        Association association = loggedInAssociationMember.getAssociation();

        association.updateAssociationInfo(request);
    }

    @Transactional
    public void updateAssociationRank(@Valid UpdateAssociationRankRequest request) {
        AssociationMember member = associationMemberRepository
                .findById(request.memberId())
                .orElseThrow(() -> new SocialWorkerException(ASSOCIATION_MEMBER_NOT_EXISTS));

        Association association = member.getAssociation();

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
    public void updateAssociationChairman(AssociationChairmanUpdateRequest request, HttpServletResponse response) {
        AssociationMember currentChairman = authUtil.getLoggedInAssociationMember();
        AssociationMember newChairman = associationMemberRepository
                .findById(request.newChairmanId())
                .orElseThrow(() -> new DomainException(ASSOCIATION_MEMBER_NOT_EXISTS));

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
}
