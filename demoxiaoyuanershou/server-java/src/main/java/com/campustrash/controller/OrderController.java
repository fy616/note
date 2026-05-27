package com.campustrash.controller;

import com.campustrash.dto.OrderRequest;
import com.campustrash.security.JwtUser;
import com.campustrash.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getOrders(
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.getOrders(status, user.getId()));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrder(
            @RequestBody OrderRequest req,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.createOrder(req, user.getId(), user.getUsername()));
    }

    @PutMapping("/{id}/resubmit")
    public ResponseEntity<Map<String, Object>> resubmitOrder(
            @PathVariable String id,
            @RequestBody OrderRequest req,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.resubmitOrder(id, req, user.getId()));
    }

    @PutMapping("/{id}/take")
    public ResponseEntity<Map<String, Object>> takeOrder(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.takeOrder(id, user.getId(), user.getUsername()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeOrder(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.completeOrder(id, user.getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteOrder(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        return ResponseEntity.ok(orderService.deleteOrder(id, user.getId()));
    }
}
