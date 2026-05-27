package com.campustrash.dto;

import jakarta.validation.constraints.NotBlank;

public class RejectRequest {

    @NotBlank(message = "请填写驳回原因")
    private String message;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
