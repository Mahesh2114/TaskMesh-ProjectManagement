package com.taskmesh.projectmanagement.service;

import com.taskmesh.projectmanagement.client.UserService;
import com.taskmesh.projectmanagement.dto.AddMember;
import com.taskmesh.projectmanagement.dto.CreateProject;
import com.taskmesh.projectmanagement.dto.ProjectDto;
import com.taskmesh.projectmanagement.entity.Project;
import com.taskmesh.projectmanagement.entity.ProjectMember;
import com.taskmesh.projectmanagement.entity.ProjectRole;
import com.taskmesh.projectmanagement.entity.ProjectStatus;
import com.taskmesh.projectmanagement.kafka.ProjectEventProducer;
import com.taskmesh.projectmanagement.repo.ProjectMemberRepo;
import com.taskmesh.projectmanagement.repo.ProjectRepo;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepo projectRepo;
    private final ProjectMemberRepo memberRepo;
    private final UserService userClient;
    private final ProjectEventProducer producer;

    public ProjectService(ProjectRepo projectRepo,
                          ProjectMemberRepo memberRepo,
                          UserService userClient,
                          ProjectEventProducer producer) {
        this.projectRepo = projectRepo;
        this.memberRepo = memberRepo;
        this.userClient = userClient;
        this.producer = producer;
    }

    public Project createProject(CreateProject request,
                                 Long ownerId,
                                 String token) {

        userClient.validateActiveUser(ownerId, token);

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwnerId(ownerId);
        project.setStatus(ProjectStatus.ACTIVE);
        project.setCreatedAt(LocalDateTime.now());

        projectRepo.save(project);

        ProjectMember owner = new ProjectMember();
        owner.setProjectId(project.getId());
        owner.setUserId(ownerId);
        owner.setRole(ProjectRole.MANAGER);
        memberRepo.save(owner);

        producer.projectCreated(project.getId());

        return project;
    }

    public void addMember(Long projectId,
                          Long ownerId,
                          AddMember request,
                          String token) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(ownerId))
            throw new RuntimeException("Only owner can add members");

            userClient.validateActiveUser(request.getUserId(), token);

        ProjectMember member = new ProjectMember();
        member.setProjectId(projectId);
        member.setUserId(request.getUserId());
        member.setRole(request.getRole());

        memberRepo.save(member);

        producer.userAssigned(projectId, request.getUserId());
    }

    @Cacheable(value = "projects", key = "#projectId")
    public ProjectDto getProject(Long projectId, Long userId) {

        if (!memberRepo.existsByProjectIdAndUserId(projectId, userId))
            throw new RuntimeException("Access denied");

        Project project = projectRepo.findById(projectId)
                .orElseThrow();

        List<ProjectMember> members =
                memberRepo.findByProjectId(projectId);

        ProjectDto response = new ProjectDto();
        response.setProject(project);
        response.setMembers(members);

        return response;
    }

    public void verifyManager(Long projectId, Long userId) {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        if (!project.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("Not project manager");
        }
    }

    public void verifyMember(Long projectId, Long userId, Long callerId) {

        // Project must exist
        projectRepo.findById(projectId)
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Assigned user must be member
        if (!memberRepo.existsByProjectIdAndUserId(projectId, userId)) {
            throw new AccessDeniedException("Assigned user is not a project member");
        }

        // Caller must be owner or member
        boolean callerAllowed =
                projectRepo.findById(projectId)
                        .get()
                        .getOwnerId().equals(callerId)
                        ||
                        memberRepo.existsByProjectIdAndUserId(projectId, callerId);

        if (!callerAllowed) {
            throw new AccessDeniedException("Caller not authorized");
        }
    }
}
