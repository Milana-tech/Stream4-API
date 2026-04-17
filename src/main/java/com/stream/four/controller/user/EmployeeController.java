package com.stream.four.controller.user;

import com.stream.four.dto.response.subscription.SubscriptionOverviewResponse;
import com.stream.four.dto.response.user.EmployeeBasicInfoResponse;
import com.stream.four.dto.response.user.ProfileStatusResponse;
import com.stream.four.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/employees",
        produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE, "text/csv"})
@Tag(name = "employees", description = "Employee-specific operations (supports JSON, XML, CSV)")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/basic-info")
    @PreAuthorize("@employeeSecurity.canViewBasicInfo(authentication.name)")
    @Operation(summary = "Get basic info", description = "Get employee basic information. Supports JSON, XML, CSV.")
    public ResponseEntity<EmployeeBasicInfoResponse> getBasicInfo(Authentication authentication) {
        return ResponseEntity.ok(employeeService.getBasicInfo(authentication.getName()));
    }

    @PutMapping("/profile/{id}/activate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.name)")
    @Operation(summary = "Activate profile", description = "Activate a user profile. Supports JSON, XML, CSV.")
    public ResponseEntity<ProfileStatusResponse> activateProfile(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.activateProfile(id));
    }

    @PutMapping("/profile/{id}/deactivate")
    @PreAuthorize("@employeeSecurity.canModifyProfiles(authentication.name)")
    @Operation(summary = "Deactivate profile", description = "Deactivate a user profile. Supports JSON, XML, CSV.")
    public ResponseEntity<ProfileStatusResponse> deactivateProfile(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.deactivateProfile(id));
    }

    @GetMapping("/subscriptions")
    @PreAuthorize("@employeeSecurity.canViewFinancialData(authentication.name)")
    @Operation(summary = "Get subscription overview", description = "Get subscription and payment overview. Supports JSON, XML, CSV.")
    public ResponseEntity<SubscriptionOverviewResponse> getSubscriptionOverview() {
        return ResponseEntity.ok(employeeService.getSubscriptionOverview());
    }
}
