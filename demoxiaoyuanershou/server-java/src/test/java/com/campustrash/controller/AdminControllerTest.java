package com.campustrash.controller;

import com.campustrash.entity.Order;
import com.campustrash.entity.User;
import com.campustrash.repository.OrderRepository;
import com.campustrash.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired private TestRestTemplate rest;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String base;
    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        base = "/api";
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setPhone("13800000000");
        admin.setDormitory("行政楼");
        admin.setRole("admin");
        userRepository.save(admin);

        User normal = new User();
        normal.setUsername("normaluser");
        normal.setPassword(passwordEncoder.encode("user123"));
        normal.setPhone("13800001111");
        normal.setDormitory("测试");
        userRepository.save(normal);

        ResponseEntity<Map> adminResp = rest.postForEntity(base + "/auth/login",
                Map.of("username", "admin", "password", "admin123"), Map.class);
        adminToken = (String) adminResp.getBody().get("token");

        ResponseEntity<Map> userResp = rest.postForEntity(base + "/auth/login",
                Map.of("username", "normaluser", "password", "user123"), Map.class);
        userToken = (String) userResp.getBody().get("token");
    }

    private HttpHeaders adminAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);
        return headers;
    }

    private HttpHeaders userAuth() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return headers;
    }

    @Test
    void adminGetUsers_shouldReturnList() {
        RequestEntity<Void> req = new RequestEntity<>(adminAuth(), HttpMethod.GET, URI.create(base + "/admin/users"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        var users = (java.util.List<?>) resp.getBody().get("users");
        assertEquals(2, users.size());
    }

    @Test
    void nonAdminGetUsers_shouldReturn400() {
        RequestEntity<Void> req = new RequestEntity<>(userAuth(), HttpMethod.GET, URI.create(base + "/admin/users"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void approveOrder_shouldWork() {
        Order order = new Order();
        order.setUserId("test");
        order.setUsername("normaluser");
        order.setDescription("待审核订单");
        order.setLocation("位置");
        order.setStatus("pending_review");
        orderRepository.save(order);

        RequestEntity<Void> req = new RequestEntity<>(adminAuth(), HttpMethod.PUT, URI.create(base + "/admin/review/" + order.getId() + "/approve"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        assertEquals("approved", ((Map<String, Object>) resp.getBody().get("order")).get("status"));
    }

    @Test
    void rejectOrder_shouldWork() {
        Order order = new Order();
        order.setUserId("test");
        order.setUsername("normaluser");
        order.setDescription("待驳回订单");
        order.setLocation("位置");
        order.setStatus("pending_review");
        orderRepository.save(order);

        Map<String, String> body = Map.of("message", "描述不够清晰");
        RequestEntity<Map<String, String>> req = new RequestEntity<>(body, adminAuth(), HttpMethod.PUT, URI.create(base + "/admin/review/" + order.getId() + "/reject"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        Map<String, Object> result = (Map<String, Object>) resp.getBody().get("order");
        assertEquals("rejected", result.get("status"));
        assertEquals("描述不够清晰", result.get("reviewMessage"));
    }

    @Test
    void freezeAndUnfreezeUser_shouldWork() {
        User normal = userRepository.findByUsername("normaluser").orElseThrow();

        RequestEntity<Void> freezeReq = new RequestEntity<>(adminAuth(), HttpMethod.PUT, URI.create(base + "/admin/users/" + normal.getId() + "/freeze"));
        ResponseEntity<Map> freezeResp = rest.exchange(freezeReq, Map.class);
        assertEquals(200, freezeResp.getStatusCode().value());

        User frozen = userRepository.findById(normal.getId()).orElseThrow();
        assertEquals(1, frozen.getIsFrozen());

        RequestEntity<Void> unfreezeReq = new RequestEntity<>(adminAuth(), HttpMethod.PUT, URI.create(base + "/admin/users/" + normal.getId() + "/unfreeze"));
        ResponseEntity<Map> unfreezeResp = rest.exchange(unfreezeReq, Map.class);
        assertEquals(200, unfreezeResp.getStatusCode().value());

        User unfrozen = userRepository.findById(normal.getId()).orElseThrow();
        assertEquals(0, unfrozen.getIsFrozen());
    }
}
