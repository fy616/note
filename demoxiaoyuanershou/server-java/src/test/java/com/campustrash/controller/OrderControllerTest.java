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
class OrderControllerTest {

    @Autowired private TestRestTemplate rest;
    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private String base;
    private String userToken;
    private String userId;

    @BeforeEach
    void setUp() {
        base = "/api";
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setUsername("orderowner");
        user.setPassword(passwordEncoder.encode("test123"));
        user.setPhone("13800001111");
        user.setDormitory("测试");
        userRepository.save(user);

        ResponseEntity<Map> loginResp = rest.postForEntity(base + "/auth/login",
                Map.of("username", "orderowner", "password", "test123"), Map.class);
        userToken = (String) loginResp.getBody().get("token");
        userId = (String) ((Map<String, Object>) loginResp.getBody().get("user")).get("id");
    }

    private HttpHeaders authHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userToken);
        return headers;
    }

    @Test
    void createOrder_shouldReturnPendingReview() {
        Map<String, Object> body = Map.of(
                "description", "测试垃圾描述",
                "location", "学宿3号楼",
                "reward", 2,
                "contact", "13800001111"
        );
        RequestEntity<Map<String, Object>> req = new RequestEntity<>(body, authHeaders(), HttpMethod.POST, URI.create(base + "/orders"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        Map<String, Object> order = (Map<String, Object>) resp.getBody().get("order");
        assertEquals("pending_review", order.get("status"));
        assertEquals("测试垃圾描述", order.get("description"));
    }

    @Test
    void getOrders_shouldReturnList() {
        Order order = new Order();
        order.setUserId(userId);
        order.setUsername("orderowner");
        order.setDescription("已有订单");
        order.setLocation("位置");
        order.setStatus("approved");
        orderRepository.save(order);

        RequestEntity<Void> req = new RequestEntity<>(authHeaders(), HttpMethod.GET, URI.create(base + "/orders"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        var orders = (java.util.List<?>) resp.getBody().get("orders");
        assertTrue(orders.size() >= 1);
    }

    @Test
    void deleteOwnOrder_shouldSucceed() {
        Order order = new Order();
        order.setUserId(userId);
        order.setUsername("orderowner");
        order.setDescription("待删除");
        order.setLocation("位置");
        order.setStatus("approved");
        orderRepository.save(order);

        RequestEntity<Void> req = new RequestEntity<>(authHeaders(), HttpMethod.DELETE, URI.create(base + "/orders/" + order.getId()));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(200, resp.getStatusCode().value());
        assertFalse(orderRepository.findById(order.getId()).isPresent());
    }

    @Test
    void deleteOthersOrder_shouldReturn400() {
        Order order = new Order();
        order.setUserId("other_user_id");
        order.setUsername("other");
        order.setDescription("别人的订单");
        order.setLocation("位置");
        order.setStatus("approved");
        orderRepository.save(order);

        RequestEntity<Void> req = new RequestEntity<>(authHeaders(), HttpMethod.DELETE, URI.create(base + "/orders/" + order.getId()));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(400, resp.getStatusCode().value());
    }

    @Test
    void createOrder_frozenUser_shouldReturn400() {
        User frozen = userRepository.findById(userId).orElseThrow();
        frozen.setIsFrozen(1);
        userRepository.save(frozen);

        Map<String, Object> body = Map.of("description", "test", "location", "test");
        RequestEntity<Map<String, Object>> req = new RequestEntity<>(body, authHeaders(), HttpMethod.POST, URI.create(base + "/orders"));
        ResponseEntity<Map> resp = rest.exchange(req, Map.class);

        assertEquals(400, resp.getStatusCode().value());
    }
}
