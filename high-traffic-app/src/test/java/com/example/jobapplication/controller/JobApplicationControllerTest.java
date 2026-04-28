package com.example.jobapplication.controller;

import com.example.jobapplication.entity.JobApplication;
import com.example.jobapplication.service.JobApplicationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JobApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JobApplicationService service;

    @Autowired
    private ObjectMapper objectMapper;

    private JobApplication testJobApplication;

    @BeforeEach
    void setUp() {
        testJobApplication = new JobApplication();
        testJobApplication.setId(1L);
        testJobApplication.setName("John Doe");
        testJobApplication.setEmail("john@example.com");
        testJobApplication.setJobId(1L);
        testJobApplication.setResumeUrl("https://example.com/resume.pdf");
    }

    @Test
    void testCreateJobApplication() throws Exception {
        when(service.createJobApplication(any(JobApplication.class))).thenReturn(testJobApplication);

        mockMvc.perform(post("/api/job-applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testJobApplication)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(service, times(1)).createJobApplication(any(JobApplication.class));
    }

    @Test
    void testGetAllJobApplications() throws Exception {
        List<JobApplication> applications = Arrays.asList(testJobApplication);
        when(service.getAllJobApplications()).thenReturn(applications);

        mockMvc.perform(get("/api/job-applications")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("John Doe"));

        verify(service, times(1)).getAllJobApplications();
    }

    @Test
    void testGetJobApplicationById() throws Exception {
        when(service.getJobApplicationById(1L)).thenReturn(Optional.of(testJobApplication));

        mockMvc.perform(get("/api/job-applications/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(service, times(1)).getJobApplicationById(1L);
    }

    @Test
    void testUpdateJobApplication() throws Exception {
        when(service.updateJobApplication(eq(1L), any(JobApplication.class))).thenReturn(testJobApplication);

        mockMvc.perform(put("/api/job-applications/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testJobApplication)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(service, times(1)).updateJobApplication(eq(1L), any(JobApplication.class));
    }

    @Test
    void testDeleteJobApplication() throws Exception {
        doNothing().when(service).deleteJobApplication(1L);

        mockMvc.perform(delete("/api/job-applications/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).deleteJobApplication(1L);
    }
}

