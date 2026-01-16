package com.stream.four.repository;

import java.util.Optional;

import com.stream.four.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    Optional<Subscription> findByUserAndActiveTrue(User user);
}
