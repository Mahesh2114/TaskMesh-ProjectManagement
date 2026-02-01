package com.taskmesh.projectmanagement.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class UserService {

    private final RestTemplate restTemplate;

    public UserService (RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateActiveUser(Long userId, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response =
                restTemplate.exchange(
                        "http://localhost:8080/users/" + userId,
                        HttpMethod.GET,
                        entity,
                        Map.class
                );

        if (!"ACTIVE".equals(response.getBody().get("status"))) {
            throw new RuntimeException("User not active");
        }
    }
}
