package com.campustrash.controller;

import com.campustrash.entity.User;
import com.campustrash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String base;

    @BeforeEach
    void setUp() {
        base = "/api";
        userRepository.deleteAll();
    }

    @Test
    void register_shouldReturnTokenAndUser() {
        Map<String, Object> body = Map.of(
                "username", "testuser",
                "password", "test123456",
                "phone", "13800001111",
                "dormitory", "测试宿舍"
        );
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/register", body, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody().get("token"));
        Map<String, Object> user = (Map<String, Object>) resp.getBody().get("user");
        assertEquals("testuser", user.get("username"));
        assertEquals("user", user.get("role"));
    }

    @Test
    void register_duplicateUsername_shouldReturn400() {
        User user = new User();
        user.setUsername("dupuser");
        user.setPassword(passwordEncoder.encode("test123456"));
        user.setPhone("13800001111");
        user.setDormitory("测试");
        userRepository.save(user);

        Map<String, Object> body = Map.of(
                "username", "dupuser",
                "password", "test123456",
                "phone", "13800002222",
                "dormitory", "测试"
        );
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/register", body, Map.class);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void register_missingFields_shouldReturn400() {
        Map<String, Object> body = Map.of("username", "incomplete");
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/register", body, Map.class);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void login_shouldReturnToken() {
        User user = new User();
        user.setUsername("logintest");
        user.setPassword(passwordEncoder.encode("mypassword"));
        user.setPhone("13800001111");
        user.setDormitory("测试");
        userRepository.save(user);

        Map<String, Object> body = Map.of("username", "logintest", "password", "mypassword");
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/login", body, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        assertNotNull(resp.getBody().get("token"));
        assertEquals("logintest", ((Map<String, Object>) resp.getBody().get("user")).get("username"));
    }

    @Test
    void login_wrongPassword_shouldReturn400() {
        User user = new User();
        user.setUsername("wpass");
        user.setPassword(passwordEncoder.encode("correct"));
        user.setPhone("13800001111");
        user.setDormitory("测试");
        userRepository.save(user);

        Map<String, Object> body = Map.of("username", "wpass", "password", "wrong");
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/login", body, Map.class);
        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void login_frozenUser_shouldReturn403() {
        User user = new User();
        user.setUsername("frozenuser");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setPhone("13800001111");
        user.setDormitory("测试");
        user.setIsFrozen(1);
        userRepository.save(user);

        Map<String, Object> body = Map.of("username", "frozenuser", "password", "test123");
        ResponseEntity<Map> resp = rest.postForEntity(base + "/auth/login", body, Map.class);
        assertEquals(400, resp.getStatusCode().value());
    }
}
