package com.stream.four.controller;

import com.stream.four.controller.user.EmployeeController;
import com.stream.four.dto.response.subscription.SubscriptionOverviewResponse;
import com.stream.four.dto.response.user.EmployeeBasicInfoResponse;
import com.stream.four.dto.response.user.ProfileStatusResponse;
import com.stream.four.exception.GlobalExceptionHandler;
import com.stream.four.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EmployeeControllerTest {

    private MockMvc mockMvc;
    private final EmployeeService employeeService = mock(EmployeeService.class);

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new EmployeeController(employeeService))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private UsernamePasswordAuthenticationToken auth() {
        return new UsernamePasswordAuthenticationToken("emp-001", null, List.of());
    }

    @Test
    void getBasicInfo_returns200() throws Exception {
        var info = new EmployeeBasicInfoResponse("emp-001", "Jan Junior", "junior@stream4.com", "JUNIOR_EMPLOYEE");
        when(employeeService.getBasicInfo("emp-001")).thenReturn(info);

        mockMvc.perform(get("/employees/basic-info")
                        .principal(auth())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jan Junior"));
    }

    @Test
    void activateProfile_returns200() throws Exception {
        var status = new ProfileStatusResponse("p1", "ACTIVE");
        when(employeeService.activateProfile("p1")).thenReturn(status);

        mockMvc.perform(put("/employees/profile/p1/activate")
                        .principal(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void deactivateProfile_returns200() throws Exception {
        var status = new ProfileStatusResponse("p1", "INACTIVE");
        when(employeeService.deactivateProfile("p1")).thenReturn(status);

        mockMvc.perform(put("/employees/profile/p1/deactivate")
                        .principal(auth()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void getSubscriptionOverview_returns200() throws Exception {
        when(employeeService.getSubscriptionOverview()).thenReturn(new SubscriptionOverviewResponse());

        mockMvc.perform(get("/employees/subscriptions")
                        .principal(auth())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
