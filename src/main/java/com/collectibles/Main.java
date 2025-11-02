package com.collectibles;

import static spark.Spark.*;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sprint 2: Adds exception handling, views, and form for item offers.
 */
public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Gson gson = new Gson();

    private static final Map<Integer, Item> items = new HashMap<>();
    private static final Map<String, User> users = new HashMap<>();

    public static void main(String[] args) {
        port(4567);
        staticFiles.location("/public");
        logger.info("🚀 Starting Collectibles Store Web App (Sprint 2)");

        // Set up routes and exception handling
        configureExceptionHandling();
        configureRoutes();

        // Load some initial demo data
        users.put("1", new User(1, "Luis Mendoza", "luis@example.com"));
        items.put(1, new Item(1, "Iron Man Figure", 49.99, "6-inch collectible"));
        items.put(2, new Item(2, "Spider-Man Poster", 19.99, "Limited edition"));
    }

    private static void configureExceptionHandling() {
        exception(ApiException.class, (ex, req, res) -> {
            res.status(((ApiException) ex).getStatusCode());
            res.type("application/json");
            res.body(gson.toJson(Map.of("error", ex.getMessage())));
            logger.error("❌ API Exception: " + ex.getMessage());
        });

        notFound((req, res) -> {
            res.type("application/json");
            return gson.toJson(Map.of("error", "Route not found"));
        });
    }

    private static void configureRoutes() {
        // Home route - renders index.html
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Collectibles Store");
            model.put("items", items.values());
            return new ModelAndView(model, "index.mustache");
        }, new MustacheTemplateEngine());

        // Display offer form
        get("/offer", (req, res) -> new ModelAndView(new HashMap<>(), "offer_form.mustache"),
            new MustacheTemplateEngine());

        // Handle form submission
        post("/offer", (req, res) -> {
            String name = req.queryParams("name");
            double price = Double.parseDouble(req.queryParams("price"));
            String description = req.queryParams("description");

            int id = items.size() + 1;
            Item newItem = new Item(id, name, price, description);
            items.put(id, newItem);

            res.redirect("/");
            return null;
        });
    }
}
