package com.goulette.workhub.project;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProjectService projectService;

    @Test
    void createReturns201WithLocationHeader() throws Exception {

        ProjectResponse response = new ProjectResponse(
                5L,
                "Website Redesign",
                "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15),
                null,
                null,
                ProjectStatus.PLANNING,
                Instant.parse("2026-10-02T12:00:00Z")
        );

        when(projectService.create(any(CreateProjectRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Website Redesign",
                          "description": "Refresh the site",
                          "budget": 25000.00,
                          "plannedStartDate": "2026-10-01",
                          "plannedEndDate": "2026-12-15"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/projects/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.status").value("PLANNING"));
    }

    @Test
    void findByIdReturns200WithProject() throws Exception {
        ProjectResponse response = new ProjectResponse(
                5L,
                "Website Redesign",
                "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15),
                null,
                null,
                ProjectStatus.PLANNING,
                Instant.parse("2026-10-02T12:00:00Z")
        );

        when(projectService.findById(5L)).thenReturn(response);

        mockMvc.perform(get("/api/projects/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Website Redesign"))
                .andExpect(jsonPath("$.plannedStartDate").value("2026-10-01"));
    }

    @Test
    void findByIdReturns404WhenProjectNotFound() throws Exception {

        when(projectService.findById(999L)).thenThrow(new ProjectNotFoundException(999L));

        mockMvc.perform(get("/api/projects/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createReturns404WhenNameIsBlank() throws Exception {

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "",
                          "description": "Refresh the site",
                          "budget": 25000.00,
                          "plannedStartDate": "2026-10-01",
                          "plannedEndDate": "2026-12-15"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).create(any());
    }

    @Test
    void createReturns400WhenBudgetIsNegative() throws Exception {

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "name": "Website Redesign",
                          "description": "Refresh the site",
                          "budget": -25000.00,
                          "plannedStartDate": "2026-10-01",
                          "plannedEndDate": "2026-12-15"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verify(projectService, never()).create(any());
    }

    @Test
    void createReturns400WhenServiceRejectsDates() throws Exception {

        when(projectService.create(any(CreateProjectRequest.class)))
                .thenThrow(new InvalidProjectDatesException(LocalDate.of(2026, 9, 1),
                                                            LocalDate.of(2026, 8, 15)));
        mockMvc.perform(post("/api/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "name": "Website Redesign",
                          "description": "Refresh the site",
                          "budget": 25000.00,
                          "plannedStartDate": "2026-09-01",
                          "plannedEndDate": "2026-08-15"
                        }
                        """))
                .andExpect(status().isBadRequest());

        verify(projectService).create(any(CreateProjectRequest.class));

    }

    @Test
    void findAllReturns200WithAllProjects() throws Exception {
        ProjectResponse response1 = new ProjectResponse(
                5L,
                "Website Redesign 1",
                "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15),
                null,
                null,
                ProjectStatus.PLANNING,
                Instant.parse("2026-10-02T12:00:00Z")
        );
        ProjectResponse response2 = new ProjectResponse(
                6L,
                "Website Redesign 2",
                "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15),
                null,
                null,
                ProjectStatus.PLANNING,
                Instant.parse("2026-10-02T12:00:00Z")
        );
        List<ProjectResponse> list = List.of(response1, response2);

        when(projectService.findAll()).thenReturn(list);

        mockMvc.perform(get("/api/projects"))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(status().isOk());
    }
}