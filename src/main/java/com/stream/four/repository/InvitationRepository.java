package com.stream.four.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stream.four.model.Invitation;

public class InvitationRepository extends JpaRepository<Invitation, String> {

    public Optional<Invitation> findByToken(String token) {
        return null;
    }

}
