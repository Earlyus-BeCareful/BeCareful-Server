package com.becareful.becarefulserver.global.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.repository.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.nursing_institution.repository.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.properties.*;
import com.becareful.becarefulserver.global.util.*;
import java.time.*;
import java.util.*;
import lombok.*;
import org.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.*;
import software.amazon.awssdk.services.s3.presigner.model.*;

@Service
@AllArgsConstructor
public class S3Service {

    private final ElderlyRepository elderlyRepository;
    private final CaregiverRepository caregiverRepository;
    private final NursingInstitutionRepository nursingInstitutionRepository;
    private final AssociationRepository associationRepository;
    private final S3Util s3Util;
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final S3Properties s3Properties;
    private final Logger log = LoggerFactory.getLogger(S3Service.class);

    private static final String[] PROFILE_TYPES = {
        "association-profile-image",
        "nursing-institution-profile-image",
        "elderly-profile-image",
        "caregiver-profile-image",
        "social-worker-profile-image"
    };

    /**
     * Presigned URL 생성 (temp 폴더에 저장)
     */
    public PresignedUrlResponse createPresignedUrl(String directory, String fileName, String contentType) {
        String tempKey = s3Util.getTempKey(directory, fileName);

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key(tempKey)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(3))
                .putObjectRequest(putRequest)
                .build();

        PresignedPutObjectRequest presignedPut = s3Presigner.presignPutObject(presignRequest);

        return new PresignedUrlResponse(tempKey, presignedPut.url().toString());
    }

    /**
     * temp → permanent 폴더로 이동
     */
    @Transactional
    public String moveTempFileToPermanent(String tempKey) {
        String newKey = s3Util.getPermanentKeyFromTempKey(tempKey);

        CopyObjectRequest copyReq = CopyObjectRequest.builder()
                .sourceBucket(s3Properties.getBucket())
                .sourceKey(tempKey)
                .destinationBucket(s3Properties.getBucket())
                .destinationKey(newKey)
                .build();

        try {
            s3Client.copyObject(copyReq); // 실패 시 예외 발생 → DB 롤백 유도
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            throw new com.becareful.becarefulserver.global.exception.exception.S3Exception(S3_MOVE_FILE_FAILED);
        }

        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucket())
                    .key(tempKey)
                    .build());
        } catch (S3Exception e) {
            log.warn("Temp file delete failed, will clean later: {}", tempKey);
        }

        return s3Util.getUrlFromKey(newKey);
    }

    @Transactional
    public void cleanAllTempFolders() {
        for (String type : PROFILE_TYPES) {
            cleanTempFolder(type);
        }
    }

    /**
     * temp 폴더 정리 (12시간 이상된 파일)
     */
    public void cleanTempFolder(String directory) {
        String prefix = directory + "/temp/";

        ListObjectsV2Request listReq = ListObjectsV2Request.builder()
                .bucket(s3Properties.getBucket())
                .prefix(prefix)
                .build();

        ListObjectsV2Response listRes = s3Client.listObjectsV2(listReq);

        Instant now = Instant.now();

        for (S3Object obj : listRes.contents()) {
            boolean isOld = obj.lastModified().isBefore(now.minus(Duration.ofHours(12)));

            if (isOld) {
                try {
                    s3Client.deleteObject(DeleteObjectRequest.builder()
                            .bucket(s3Properties.getBucket())
                            .key(obj.key())
                            .build());
                    log.info("Deleted temp file: {}", obj.key());
                } catch (S3Exception e) {
                    log.warn("Failed to delete temp file: {}", obj.key());
                }
            }
        }
    }

    /**
     * permanent 폴더에 있으나, DB에 없는 파일 삭제
     */
    @Transactional(readOnly = true)
    public void deleteOrphanPermanentFiles() {
        Set<String> registeredUrls = getAllRegisteredProfileImageUrls();

        for (String type : PROFILE_TYPES) {
            String prefix = type + "/permanent/";
            List<String> s3Keys = s3Util.listFiles(prefix);

            for (String key : s3Keys) {
                String fullUrl = s3Util.getUrlFromKey(key);

                if (!registeredUrls.contains(fullUrl)) {
                    s3Util.deleteFile(key);
                }
            }
        }
    }

    /**
     * 모든 테이블의 profileImageUrl 수집
     */
    private Set<String> getAllRegisteredProfileImageUrls() {
        Set<String> urls = new HashSet<>();

        urls.addAll(elderlyRepository.findAllProfileImageUrls());
        urls.addAll(caregiverRepository.findAllProfileImageUrls());
        urls.addAll(nursingInstitutionRepository.findAllProfileImageUrls());
        urls.addAll(associationRepository.findAllProfileImageUrls());

        return urls;
    }
}
