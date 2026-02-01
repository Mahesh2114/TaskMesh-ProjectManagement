package com.taskmesh.projectmanagement.dto;

import com.taskmesh.projectmanagement.entity.Project;
import com.taskmesh.projectmanagement.entity.ProjectMember;

import java.util.List;

public class ProjectDto {
    private Project project;
    private List<ProjectMember> members;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public List<ProjectMember> getMembers() {
        return members;
    }

    public void setMembers(List<ProjectMember> members) {
        this.members = members;
    }
}
