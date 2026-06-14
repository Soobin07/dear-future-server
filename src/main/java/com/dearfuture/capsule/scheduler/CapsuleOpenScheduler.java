package com.dearfuture.capsule.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.dearfuture.capsule.entity.Capsule;
import com.dearfuture.capsule.repository.CapsuleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CapsuleOpenScheduler {

    private final CapsuleRepository capsuleRepository;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void openCapsules() {
        LocalDateTime now = LocalDateTime.now();

        List<Capsule> capsules =
                capsuleRepository.findAllByIsOpenedFalseAndOpenAtBefore(now);

        for (Capsule capsule : capsules) {
            capsule.open();
        }
    }
}