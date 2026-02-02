package com.taskmesh.projectmanagement.controller;

import com.taskmesh.projectmanagement.dto.AddMember;
import com.taskmesh.projectmanagement.dto.CreateProject;
import com.taskmesh.projectmanagement.dto.ProjectDto;
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

    @PostMapping("/{projectId}/members")
    public ResponseEntity<?> addMember(
            @PathVariable Long projectId,
            @RequestBody AddMember request,
            @RequestHeader("Authorization") String token,
            Authentication auth) {

        Long ownerId = Long.parseLong(auth.getName());
        projectService.addMember(projectId, ownerId, request, token);
        return ResponseEntity.ok("User added");
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ProjectDto> getProject(
            @PathVariable Long projectId,
            Authentication auth) {

        Long userId = Long.parseLong(auth.getName());
        return ResponseEntity.ok(
                projectService.getProject(projectId, userId)
        );
    }

    @GetMapping("/{projectId}/verify-manager")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> verifyManager(
            @PathVariable Long projectId,
            Authentication auth) {

        Long userId = Long.parseLong(auth.getName());

        projectService.verifyManager(projectId, userId);

        return ResponseEntity.ok().build();
    }
    @GetMapping("/{projectId}/members/{userId}")
    public ResponseEntity<Void> verifyMember(
            @PathVariable Long projectId,
            @PathVariable Long userId,
            Authentication auth) {

        Long callerId = Long.parseLong(auth.getName());
        projectService.verifyMember(projectId, userId, callerId);
        return ResponseEntity.ok().build();
    }
}
