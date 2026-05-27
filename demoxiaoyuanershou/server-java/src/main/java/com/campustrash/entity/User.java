package com.campustrash.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private String phone;
    private String dormitory;

    @Column(nullable = false)
    private Integer points = 0;

    @Column(nullable = false)
    private String role = "user";

    private Integer isFrozen = 0;

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (id == null) {
            id = String.valueOf(System.currentTimeMillis());
        }
        if (points == null) {
            points = 0;
        }
        if (role == null) {
            role = "user";
        }
        if (isFrozen == null) {
            isFrozen = 0;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDormitory() { return dormitory; }
    public void setDormitory(String dormitory) { this.dormitory = dormitory; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getIsFrozen() { return isFrozen == null ? 0 : isFrozen; }
    public void setIsFrozen(Integer isFrozen) { this.isFrozen = isFrozen; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
