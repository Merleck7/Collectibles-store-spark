package com.collectibles;

/**
 * Simple User class for demo purposes.
 */
public class User {
    private String name;
    private String email;

    public User() {
        // Empty constructor required for Gson
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Getters
    public String getName() { return name; }
    public String getEmail() { return email; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}

