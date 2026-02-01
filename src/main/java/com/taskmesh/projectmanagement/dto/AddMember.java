package com.taskmesh.projectmanagement.dto;

import com.taskmesh.projectmanagement.entity.ProjectRole;

public class AddMember {
    private Long userId;
    private ProjectRole role;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProjectRole getRole() {
        return role;
    }

    public void setRole(ProjectRole role) {
        this.role = role;
    }
}
