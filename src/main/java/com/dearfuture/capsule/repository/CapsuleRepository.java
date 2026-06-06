package com.dearfuture.capsule.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dearfuture.capsule.entity.Capsule;

public interface CapsuleRepository extends JpaRepository<Capsule, Long> {

    List<Capsule> findAllByUserId(Long userId);
}