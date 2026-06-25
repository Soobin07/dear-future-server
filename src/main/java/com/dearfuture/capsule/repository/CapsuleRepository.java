package com.dearfuture.capsule.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dearfuture.capsule.entity.Capsule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

	Page<Capsule> findAllByUserId(Long userId, Pageable pageable);

	List<Capsule> findAllByIsOpenedFalseAndOpenAtBefore(LocalDateTime now);

	Page<Capsule> findAllByUserIdAndIsOpenedTrue(Long userId, Pageable pageable);

	Page<Capsule> findAllByUserIdAndIsOpenedFalse(Long userId, Pageable pageable);
}