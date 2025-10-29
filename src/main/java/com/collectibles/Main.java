package com.collectibles;

import static spark.Spark.*;
import com.google.gson.Gson;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class for the Collectibles Store API.
 * Implements CRUD routes for users using Spark Java.
 */
public class Main {

    // Logger instance for console output
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Gson instance for converting between JSON and Java objects
    private static final Gson gson = new Gson();

    // Simple in-memory user storage (Map simulating a database)
    private static final Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        // Set the port where Spark will listen (default: 4567)
        port(4567);

        logger.info("🚀 Starting Collectibles Store API on http://localhost:4567 ...");

        // Define routes for the REST API
        defineRoutes();

        // Graceful shutdown message
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("🛑 Server stopped.");
        }));
    }

    /**
     * Defines all API routes for user operations.
     */
    private static void defineRoutes() {

        // GET /users — Retrieve all users
        get("/users", (req, res) -> {
            res.type("application/json");
            return gson.toJson(users.values());
        });

        // GET /users/:id — Retrieve user by ID
        get("/users/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");

            User user = users.get(id);
            if (user == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }
            return gson.toJson(user);
        });

        // POST /users/:id — Add new user
        post("/users/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");

            if (users.containsKey(id)) {
                res.status(409);
                return gson.toJson(Map.of("error", "User already exists"));
            }

            User user = gson.fromJson(req.body(), User.class);
            users.put(id, user);
            res.status(201);
            return gson.toJson(Map.of("message", "User added successfully", "user", user));
        });

        // PUT /users/:id — Edit an existing user
        put("/users/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");

            User existing = users.get(id);
            if (existing == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }

            User updated = gson.fromJson(req.body(), User.class);
            users.put(id, updated);
            return gson.toJson(Map.of("message", "User updated successfully", "user", updated));
        });

        // OPTIONS /users/:id — Check if a user exists
        options("/users/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");

            boolean exists = users.containsKey(id);
            return gson.toJson(Map.of("exists", exists));
        });

        // DELETE /users/:id — Delete a user
        delete("/users/:id", (req, res) -> {
            res.type("application/json");
            String id = req.params(":id");

            User removed = users.remove(id);
            if (removed == null) {
                res.status(404);
                return gson.toJson(Map.of("error", "User not found"));
            }

            return gson.toJson(Map.of("message", "User deleted successfully", "user", removed));
        });

        // Log confirmation
        logger.info("✅ All routes successfully configured.");
    }
}