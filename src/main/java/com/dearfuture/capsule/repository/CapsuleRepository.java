package com.dearfuture.capsule.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dearfuture.capsule.entity.Capsule;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    List<Capsule> findAllByUserId(Long userId);
    List<Capsule> findAllByIsOpenedFalseAndOpenAtBefore(LocalDateTime now);
    List<Capsule> findAllByUserIdAndIsOpenedTrue(Long userId);
    List<Capsule> findAllByUserIdAndIsOpenedFalse(Long userId);
}