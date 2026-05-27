package com.campustrash.dto;

import java.time.LocalDateTime;

public class OrderRequest {

    private String description;
    private String location;
    private Integer reward;
    private String contact;
    private String category;
    private LocalDateTime deadline;

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public Integer getReward() { return reward; }
    public void setReward(Integer reward) { this.reward = reward; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
}
