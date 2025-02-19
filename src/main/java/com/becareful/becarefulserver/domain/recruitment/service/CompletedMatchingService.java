package com.becareful.becarefulserver.domain.recruitment.service;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.recruitment.domain.CompletedMatching;
import com.becareful.becarefulserver.domain.recruitment.dto.request.EditCompletedMatchingNoteRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.CompletedMatchingInfoResponse;
import com.becareful.becarefulserver.domain.recruitment.repository.CompletedMatchingRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompletedMatchingService {
    private final CompletedMatchingRepository completedMatchingRepository;
    private final AuthUtil authUtil;

    public List<CompletedMatchingInfoResponse> getCompletedMatchings() {
        Caregiver caregiver = authUtil.getLoggedInCaregiver();
        List<CompletedMatching> completedMatchings = completedMatchingRepository.findByCaregiver(caregiver);
        return completedMatchings.stream()
                .map(matching -> CompletedMatchingInfoResponse.form(matching, matching.getElderly()))
                .collect(Collectors.toList());
    }

    public void editNote(Long completedMatchingId, EditCompletedMatchingNoteRequest request){
        CompletedMatching completedMatching = completedMatchingRepository.findById(completedMatchingId)
                .orElseThrow(() -> new IllegalArgumentException("Matching not found"));

        completedMatching.updateNote(request.note());
    }
}
