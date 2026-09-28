package com.goulette.workhub.project;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public ProjectResponse create(CreateProjectRequest request) {
        Project saved = projectRepository.save(new Project(
                request.name(),
                request.description(),
                request.budget(),
                request.plannedStartDate(),
                request.plannedEndDate()
        ));
        return ProjectResponse.from(saved);
    }

    public List<ProjectResponse> findAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public ProjectResponse findById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(id));
        return ProjectResponse.from(project);
    }
}
