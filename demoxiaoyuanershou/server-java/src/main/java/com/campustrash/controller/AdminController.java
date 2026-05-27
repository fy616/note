package com.campustrash.controller;

import com.campustrash.dto.RejectRequest;
import com.campustrash.repository.UserRepository;
import com.campustrash.security.JwtUser;
import com.campustrash.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserRepository userRepository;

    public AdminController(AdminService adminService, UserRepository userRepository) {
        this.adminService = adminService;
        this.userRepository = userRepository;
    }

    private void checkAdmin(String userId) {
        String role = userRepository.findRoleById(userId);
        if (role == null || !"admin".equals(role)) {
            throw new RuntimeException("需要管理员权限");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(@AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.getUsers());
    }

    @PutMapping("/users/{id}/freeze")
    public ResponseEntity<Map<String, String>> freezeUser(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.freezeUser(id));
    }

    @PutMapping("/users/{id}/unfreeze")
    public ResponseEntity<Map<String, String>> unfreezeUser(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.unfreezeUser(id));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.deleteUser(id));
    }

    @GetMapping("/review")
    public ResponseEntity<Map<String, Object>> getReviewOrders(@AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.getReviewOrders());
    }

    @PutMapping("/review/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveOrder(
            @PathVariable String id,
            @AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.approveOrder(id));
    }

    @PutMapping("/review/{id}/reject")
    public ResponseEntity<Map<String, Object>> rejectOrder(
            @PathVariable String id,
            @Valid @RequestBody RejectRequest req,
            @AuthenticationPrincipal JwtUser user) {
        checkAdmin(user.getId());
        return ResponseEntity.ok(adminService.rejectOrder(id, req));
    }
}
