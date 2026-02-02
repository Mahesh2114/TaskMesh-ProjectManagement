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

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void validateActiveUser(Long userId, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token); // 🔥 FORWARD JWT

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        restTemplate.exchange(
                "http://localhost:8080/users/" + userId,
                HttpMethod.GET,
                entity,
                Void.class
        );
    }
}
