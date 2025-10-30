package com.collectibles;

import static spark.Spark.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Main {

    // Logger instance for debugging and info logs
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Gson instance for JSON handling
    private static final Gson gson = new Gson();

    // In-memory user storage (used instead of a database for simplicity)
    private static final Map<Integer, User> users = new HashMap<>();

    public static void main(String[] args) {

        // Start the Spark server on port 4567
        port(4567);

        // Log server startup
        logger.info("🚀 Starting Collectibles Store API on http://localhost:4567");

        // Add some sample users for testing
        users.put(1, new User(1, "Luis Mendoza", "luis@example.com"));
        users.put(2, new User(2, "Emilio Flores", "emilio@example.com"));
        users.put(3, new User(3, "Ana Torres", "ana@example.com"));

        // Root route to check if the server is running
        get("/", (req, res) -> {
            res.type("application/json");
            return gson.toJson(Map.of(
                "message", "Welcome to the Collectibles Store API 👋",
                "available_routes", List.of("/users", "/users/:id")
            ));
        });

        // Retrieve all users
        get("/users", (req, res) -> {
            res.type("application/json");
            logger.info("GET /users - Retrieving all users");
            return gson.toJson(users.values());
        });

        // Retrieve user by ID
        get("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User user = users.get(id);
            if (user == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }
            return gson.toJson(user);
        });

        // Add a new user
        post("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User newUser = gson.fromJson(req.body(), User.class);
            users.put(id, newUser);
            logger.info("POST /users/:id - Added new user with ID {}", id);
            res.status(201);
            return gson.toJson(Map.of("message", "User added successfully"));
        });

        // Update an existing user
        put("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User updatedUser = gson.fromJson(req.body(), User.class);
            if (!users.containsKey(id)) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }
            users.put(id, updatedUser);
            logger.info("PUT /users/:id - Updated user with ID {}", id);
            return gson.toJson(Map.of("message", "User updated successfully"));
        });

        // Check if user exists
        options("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            boolean exists = users.containsKey(id);
            return gson.toJson(Map.of("exists", exists));
        });

        // Delete a user
        delete("/users/:id", (req, res) -> {
            res.type("application/json");
            int id = Integer.parseInt(req.params(":id"));
            User removed = users.remove(id);
            if (removed == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }
            logger.info("DELETE /users/:id - Removed user with ID {}", id);
            return gson.toJson(Map.of("message", "User deleted successfully"));
        });

        // Exception handler for unexpected errors
        exception(Exception.class, (e, req, res) -> {
            logger.error("Unexpected error occurred", e);
            res.type("application/json");
            res.status(500);
            res.body(gson.toJson(Map.of("error", "Internal server error")));
        });

        // After filter to set JSON response type by default
        after((req, res) -> res.type("application/json"));
    }
}