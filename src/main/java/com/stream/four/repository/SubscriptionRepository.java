package com.stream.four.repository;

import com.stream.four.model.subscription.Subscription;
import com.stream.four.model.enums.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUser_UserIdAndStatus(String userId, SubscriptionStatus status);

    List<Subscription> findByUser_UserId(String userId); 

    boolean existsByUser_UserIdAndStatus(String userId, SubscriptionStatus status);
}
