package com.stream.four.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUserIdAndActiveTrue(String userId);
}
