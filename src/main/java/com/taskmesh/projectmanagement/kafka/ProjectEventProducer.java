package com.taskmesh.projectmanagement.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProjectEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProjectEventProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void projectCreated(Long projectId) {
        kafkaTemplate.send("PROJECT_CREATED", projectId.toString());
    }

    public void userAssigned(Long projectId, Long userId) {
        kafkaTemplate.send("USER_ASSIGNED_TO_PROJECT",
                projectId + ":" + userId);
    }
}
