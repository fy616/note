package com.campustrash.controller;

import com.campustrash.dto.CategoryRequest;
import com.campustrash.entity.TaskCategory;
import com.campustrash.security.JwtUser;
import com.campustrash.service.CategoryService;
import com.campustrash.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final AdminService adminService;

    @GetMapping
    public ResponseEntity<List<TaskCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PostMapping
    public ResponseEntity<TaskCategory> createCategory(
            @AuthenticationPrincipal JwtUser user,
            @Valid @RequestBody CategoryRequest request) {
        adminService.checkAdmin(user.getId());
        return ResponseEntity.ok(categoryService.createCategory(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskCategory> updateCategory(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String id,
            @Valid @RequestBody CategoryRequest request) {
        adminService.checkAdmin(user.getId());
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @AuthenticationPrincipal JwtUser user,
            @PathVariable String id) {
        adminService.checkAdmin(user.getId());
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
