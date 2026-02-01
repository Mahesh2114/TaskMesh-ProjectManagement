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


}
