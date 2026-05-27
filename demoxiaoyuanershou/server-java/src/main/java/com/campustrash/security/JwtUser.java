package com.campustrash.security;

public class JwtUser {

    private final String id;
    private final String username;

    public JwtUser(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() { return id; }
    public String getUsername() { return username; }
}
