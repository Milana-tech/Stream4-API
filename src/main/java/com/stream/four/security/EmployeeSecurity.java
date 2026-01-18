package com.stream.four.security;

import com.stream.four.model.enums.Role;
import com.stream.four.model.user.User;
import org.springframework.stereotype.Component;

@Component("employeeSecurity")
public class EmployeeSecurity
{
    public boolean canViewBasicInfo(User user)
    {
      return user != null && (
              user.getRole() == Role.JUNIOR_EMPLOYEE || user.getRole() == Role.MID_EMPLOYEE ||
                      user.getRole() == Role.SENIOR_EMPLOYEE
              );
    }

    public boolean canModifyProfiles(User user)
    {
        return user != null && (
                user.getRole() == Role.MID_EMPLOYEE || user.getRole() == Role.SENIOR_EMPLOYEE
                );
    }

    public boolean canViewFinancialData(User user)
    {
        return user != null &&  user.getRole() == Role.SENIOR_EMPLOYEE;
    }
}
