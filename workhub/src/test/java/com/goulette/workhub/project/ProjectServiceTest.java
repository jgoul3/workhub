package com.goulette.workhub.project;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectService(projectRepository);
    }

    @Test
    void createSavesProjectAndReturnsResponse() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Website Redesign", "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15)
        );

        when(projectRepository.save(any(Project.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse response = projectService.create(request);

        assertThat(response.name()).isEqualTo("Website Redesign");
        assertThat(response.status()).isEqualTo(ProjectStatus.PLANNING);
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    void createRejectsInvalidDates() {
        CreateProjectRequest request = new CreateProjectRequest(
                "Website Redesign", "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 9, 15)
        );

        assertThatThrownBy(() -> projectService.create(request))
                .isInstanceOf(InvalidProjectDatesException.class);
        verify(projectRepository, never()).save(any());
    }

    @Test
    void findByIdReturnsExistingProject() {
        Project project = new Project(
            "Website Redesign", "Refresh the site",
            new BigDecimal("25000.00"),
            LocalDate.of(2026, 10, 1),
            LocalDate.of(2026, 12, 15)
        );

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponse response = projectService.findById(1L);

        assertThat(response.name()).isEqualTo("Website Redesign");
        assertThat(response.budget()).isEqualByComparingTo(new BigDecimal("25000.00"));
    }

    @Test
    void findByIdThrowsExceptionWhenNotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> projectService.findById(1L))
                .isInstanceOf(ProjectNotFoundException.class)
                .hasMessageContaining("1");
    }

    @Test
    void findAllReturnsEveryExistingProject() {
        Project project1 = new Project(
                "Website Redesign 1", "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15)
        );
        Project project2 = new Project(
                "Website Redesign 2", "Refresh the site",
                new BigDecimal("25000.00"),
                LocalDate.of(2026, 10, 1),
                LocalDate.of(2026, 12, 15)
        );
        when(projectRepository.findAll()).thenReturn(List.of(project1, project2));

        List<ProjectResponse> responses = projectService.findAll();

        assertThat(responses)
                .extracting(ProjectResponse::name)
                .containsExactly("Website Redesign 1", "Website Redesign 2");
    }

    @Test
    void findAllReturnsEmptyWhenNoProjects() {
        when(projectRepository.findAll()).thenReturn(List.of());
        assertThat(projectService.findAll()).isEmpty();
    }
}