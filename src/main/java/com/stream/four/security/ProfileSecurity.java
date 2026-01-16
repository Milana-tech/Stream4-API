package com.stream.four.security;

import com.stream.four.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;

@Component("profileSecurity")
@RequiredArgsConstructor
public class ProfileSecurity
{

    private final ProfileRepository profileRepository;

    public boolean canAccessProfile(String profileId, String userId)
    {
        return profileRepository.existsByIdAndUserId(profileId, userId);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}
