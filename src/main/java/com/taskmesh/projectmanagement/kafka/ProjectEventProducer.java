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
        try {
            kafkaTemplate.send("PROJECT_CREATED", projectId.toString());
        } catch (Exception e) {
            System.out.println("Kafka down, skipping PROJECT_CREATED event");
        }
    }

    public void userAssigned(Long projectId, Long userId) {
        try {
            kafkaTemplate.send(
                    "USER_ASSIGNED_TO_PROJECT",
                    projectId + ":" + userId
            );
        } catch (Exception e) {
            System.out.println("Kafka down, skipping USER_ASSIGNED_TO_PROJECT event");
        }
    }
}
