package com.stream.four.security;

import com.stream.four.model.enums.Role;
import com.stream.four.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("employeeSecurity")
@RequiredArgsConstructor
public class EmployeeSecurity
{
    private final UserRepository userRepository;

    public boolean canViewBasicInfo(String userId)
    {
        return userRepository.findById(userId)
                .map(u -> u.getRole() == Role.JUNIOR_EMPLOYEE
                        || u.getRole() == Role.MID_EMPLOYEE
                        || u.getRole() == Role.SENIOR_EMPLOYEE)
                .orElse(false);
    }

    public boolean canModifyProfiles(String userId)
    {
        return userRepository.findById(userId)
                .map(u -> u.getRole() == Role.MID_EMPLOYEE || u.getRole() == Role.SENIOR_EMPLOYEE)
                .orElse(false);
    }

    public boolean canViewFinancialData(String userId)
    {
        return userRepository.findById(userId)
                .map(u -> u.getRole() == Role.SENIOR_EMPLOYEE)
                .orElse(false);
    }
}
