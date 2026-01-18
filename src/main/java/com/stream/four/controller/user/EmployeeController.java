package com.stream.four.controller.user;

import com.stream.four.dto.response.subscription.SubscriptionOverviewResponse;
import com.stream.four.dto.response.user.EmployeeBasicInfoResponse;
import com.stream.four.dto.response.user.ProfileStatusResponse;
import com.stream.four.model.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/employee",
        produces = {MediaType.APPLICATION_JSON_VALUE,
                MediaType.APPLICATION_XML_VALUE,
                "text/csv"})  // ← Added CSV support
@Tag(name = "Employee", description = "Employee-specific operations (supports JSON, XML, CSV)")
public class EmployeeController {

    @GetMapping("/basic-info")
    @PreAuthorize("@employeeSecurity.canViewBasicInfo(authentication.principal)")
    @Operation(summary = "Get basic info", description = "Get employee basic information. Supports JSON, XML, CSV.")
    public ResponseEntity<EmployeeBasicInfoResponse> getBasicInfo(@AuthenticationPrincipal User user) {
        EmployeeBasicInfoResponse response = new EmployeeBasicInfoResponse(
                user.getUserId(), user.getName(), user.getEmail(), user.getRole().name()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{id}/activate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.principal)")
    @Operation(summary = "Activate profile", description = "Activate a user profile. Supports JSON, XML, CSV.")
    public ResponseEntity<ProfileStatusResponse> activateProfile(@PathVariable String id) {
        ProfileStatusResponse response = new ProfileStatusResponse(id, "ACTIVATED");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile/{id}/deactivate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.principal)")
    @Operation(summary = "Deactivate profile", description = "Deactivate a user profile. Supports JSON, XML, CSV.")
    public ResponseEntity<ProfileStatusResponse> deactivateProfile(@PathVariable String id) {
        ProfileStatusResponse response = new ProfileStatusResponse(id, "DEACTIVATED");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("@employeeSecurity.canViewFinancialData(authentication.principal)")
    @Operation(summary = "Get subscription overview", description = "Get subscription and payment overview. Supports JSON, XML, CSV.")
    public ResponseEntity<SubscriptionOverviewResponse> getSubscriptionOverview() {
        SubscriptionOverviewResponse response = new SubscriptionOverviewResponse(
                "Sample Subscription Overview",
                "Sample Payment History"
        );
        return ResponseEntity.ok(response);
    }
}