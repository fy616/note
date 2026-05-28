package com.campustrash.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    private String id;

    @Column(name = "userId")
    private String userId;
    private String username;
    private String description;
    private String location;
    private Integer reward = 0;
    private String contact;
    private String status;

    @Column(name = "takerId")
    private String takerId;
    @Column(name = "takerName")
    private String takerName;

    @Column(name = "reviewMessage", columnDefinition = "TEXT")
    private String reviewMessage;

    @Column(length = 50)
    private String category;

    private LocalDateTime deadline;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (id == null) {
            id = String.valueOf(System.currentTimeMillis());
        }
        if (reward == null) {
            reward = 0;
        }
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getReward() { return reward; }
    public void setReward(Integer reward) { this.reward = reward; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getTakerId() { return takerId; }
    public void setTakerId(String takerId) { this.takerId = takerId; }
    public String getTakerName() { return takerName; }
    public void setTakerName(String takerName) { this.takerName = takerName; }
    public String getReviewMessage() { return reviewMessage; }
    public void setReviewMessage(String reviewMessage) { this.reviewMessage = reviewMessage; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
