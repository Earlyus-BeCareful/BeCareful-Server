package com.becareful.becarefulserver.global.scheduler;

import com.becareful.becarefulserver.global.service.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3CleanUpScheduler {
    private final S3Service s3Service;

    @Scheduled(cron = "0 0 0 * * *") // 메 00:00
    public void cleanupTempFiles() {
        log.info("[S3CleanupScheduler] temp 폴더 정리 시작");
        s3Service.cleanAllTempFolders();
        log.info("[S3CleanupScheduler] temp 폴더 정리 완료");
    }

    @Scheduled(cron = "0 0 0 * * *") // 매 00:00
    public void cleanupPermanentFiles() {
        log.info("[S3CleanupScheduler] permanent 폴더 정리 시작");
        s3Service.deleteOrphanPermanentFiles();
        log.info("[S3CleanupScheduler] permanent 폴더 정리 완료");
    }
}
