package com.stream.four.controller.user;

import com.stream.four.dto.response.subscription.SubscriptionOverviewResponse;
import com.stream.four.dto.response.user.EmployeeBasicInfoResponse;
import com.stream.four.dto.response.user.ProfileStatusResponse;
import com.stream.four.model.user.User;
import org.apache.coyote.Response;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/employee", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class EmployeeController
{
    @GetMapping("/basic-info")
    @PreAuthorize("@employeeSecurity.canViewBasicInfo(authentication.principal)")
    public ResponseEntity<EmployeeBasicInfoResponse> getBackInfo(@AuthenticationPrincipal User user)
    {
        EmployeeBasicInfoResponse response = new EmployeeBasicInfoResponse(
                user.getUserId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{id}/activate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.principal)")
    public ResponseEntity<ProfileStatusResponse> activateProfile(@PathVariable String id)
    {
        ProfileStatusResponse response = new ProfileStatusResponse(id, "ACTIVATED");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{id}/deactivate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.principal)")
    public ResponseEntity<ProfileStatusResponse> deactivateProfile(@PathVariable String id)
    {
        ProfileStatusResponse response = new ProfileStatusResponse(id, "DEACTIVATED");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("@employeeSecurity.canViewFinancialData(authentication.principle)")
    public ResponseEntity<SubscriptionOverviewResponse> getSubscriptionOverview()
    {
        SubscriptionOverviewResponse response = new SubscriptionOverviewResponse(
                "Sample Subscription Overview",
                "Sample Payment History"
        );
        return ResponseEntity.ok(response);
    }
}