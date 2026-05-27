package com.campustrash.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "task_categories")
public class TaskCategory {
    @Id
    private String id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 10)
    private String icon;

    @Column(name = "need_review")
    private Boolean needReview = true;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "is_custom")
    private Boolean isCustom = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        id = String.valueOf(System.currentTimeMillis());
        createdAt = LocalDateTime.now();
    }
}
