package com.taskmesh.projectmanagement.controller;

import com.taskmesh.projectmanagement.dto.CreateProject;
import com.taskmesh.projectmanagement.entity.Project;
import com.taskmesh.projectmanagement.service.ProjectService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Project> createProject(
            @RequestBody CreateProject request,
            @RequestHeader("Authorization") String token,
            Authentication auth) {

        Long ownerId = Long.parseLong(auth.getName());

        return ResponseEntity.ok(
                projectService.createProject(request, ownerId, token)
        );
    }

}
