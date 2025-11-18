package com.becareful.becarefulserver.domain.matching.service;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.matching.dto.request.EditCompletedMatchingNoteRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.CompletedMatchingInfoResponse;
import com.becareful.becarefulserver.domain.matching.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompletedMatchingService {

    private final CompletedMatchingRepository completedMatchingRepository;
    private final AuthUtil authUtil;

    public List<CompletedMatchingInfoResponse> getCompletedMatchings() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<CompletedMatching> completedMatchings = completedMatchingRepository.findByCaregiver(caregiver);
        return completedMatchings.stream()
                .map(cm -> CompletedMatchingInfoResponse.from(cm, cm.getContract()))
                .toList();
    }

    public void editNote(Long completedMatchingId, EditCompletedMatchingNoteRequest request) {
        CompletedMatching completedMatching = completedMatchingRepository
                .findById(completedMatchingId)
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        completedMatching.updateNote(request.note());
    }
}
