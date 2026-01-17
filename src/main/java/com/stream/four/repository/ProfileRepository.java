package com.stream.four.repository;

import com.stream.four.model.user.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileRepository extends JpaRepository<Profile, String> {
    
    List<Profile> findByUserIdAndDeletedFalse(String userId);

    boolean existsByIdAndUserId(String id, String userId);
}
