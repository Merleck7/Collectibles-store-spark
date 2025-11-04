package com.collectibles;

import static spark.Spark.*;
import com.google.gson.Gson;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

/**
 * Main entry point for the Collectibles Store web app.
 * Sprint 3: Implements item filters and real-time price updates using WebSockets.
 */
public class Main {

    // Logger for application events
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    // Gson instance for JSON serialization
    private static final Gson gson = new Gson();

    // In-memory data storage (mock database)
    private static final Map<Integer, Item> items = new HashMap<>();
    private static final Map<String, User> users = new HashMap<>();

    // WebSocket sessions for real-time updates
    private static final Set<Session> webSocketSessions = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        // 1️⃣ Server setup and WebSocket registration
        port(4567); // Default Spark port
        webSocket("/price-updates", PriceWebSocketHandler.class); // WebSocket endpoint
        staticFiles.location("/public"); // Serve static files from src/main/resources/public

        logger.info("🚀 Starting Collectibles Store Web App (Sprint 3)");

        // 2️⃣ Initialize sample data (temporary mock database)
        users.put("1", new User(1, "Luis Mendoza", "luis@example.com"));
        users.put("2", new User(2, "Ana Torres", "ana@example.com"));

        items.put(1, new Item(1, "Iron Man Figure", 49.99, "6-inch collectible"));
        items.put(2, new Item(2, "Spider-Man Poster", 19.99, "Limited edition"));
        items.put(3, new Item(3, "Batman Statue", 79.99, "Exclusive edition"));

        // 3️⃣ Configure routes and error handling
        configureExceptionHandling();
        configureRoutes();

        // 4️⃣ Initialize Spark server
        init();
    }

    /**
     * Handles exceptions and missing routes globally.
     */
    private static void configureExceptionHandling() {
        // Handle custom API exceptions
        exception(ApiException.class, (ex, req, res) -> {
            res.status(((ApiException) ex).getStatusCode());
            res.type("application/json");
            res.body(gson.toJson(Map.of("error", ex.getMessage())));
            logger.error("❌ API Exception: " + ex.getMessage());
        });

        // Handle 404 (route not found)
        notFound((req, res) -> {
            res.type("application/json");
            return gson.toJson(Map.of("error", "Route not found"));
        });
    }

    /**
     * Defines all routes and endpoints.
     */
    private static void configureRoutes() {

        // ✅ Test route - confirms Spark is responding
        get("/test", (req, res) -> {
            res.type("text/html; charset=UTF-8"); // ✅ Force UTF-8 encoding
            return "<h2>✅ Server is running properly!</h2>";
        });

        // 🏠 Root route - serves the main page
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("title", "Collectibles Store");
            model.put("items", items.values());
            return new ModelAndView(model, "index.mustache");
        }, new MustacheTemplateEngine());

        // 👥 List all users (JSON)
        get("/users", (req, res) -> {
            res.type("application/json");
            return gson.toJson(users.values());
        });

        // 📄 Offer submission form
        get("/offer", (req, res) ->
                new ModelAndView(new HashMap<>(), "offer_form.mustache"),
                new MustacheTemplateEngine()
        );

        // 📨 Handle offer form submission
        post("/offer", (req, res) -> {
            String name = req.queryParams("name");
            String description = req.queryParams("description");
            double price = Double.parseDouble(req.queryParams("price"));

            int id = items.size() + 1;
            Item newItem = new Item(id, name, price, description);
            items.put(id, newItem);

            // Notify WebSocket clients of new items/prices
            broadcastPriceUpdate();

            res.redirect("/");
            return null;
        });

        // 🔍 Filter items by name or price range
        get("/filter", (req, res) -> {
            String nameFilter = req.queryParams("name");
            String minPriceParam = req.queryParams("min");
            String maxPriceParam = req.queryParams("max");

            double minPrice = minPriceParam != null ? Double.parseDouble(minPriceParam) : 0.0;
            double maxPrice = maxPriceParam != null ? Double.parseDouble(maxPriceParam) : Double.MAX_VALUE;

            List<Item> filteredItems = items.values().stream()
                    .filter(i -> (nameFilter == null || i.getName().toLowerCase().contains(nameFilter.toLowerCase())) &&
                            i.getPrice() >= minPrice && i.getPrice() <= maxPrice)
                    .toList();

            res.type("application/json");
            return gson.toJson(filteredItems);
        });
    }

    /**
     * Sends real-time item updates to all connected WebSocket clients.
     */
    public static void broadcastPriceUpdate() {
        String message = gson.toJson(Map.of("type", "update", "items", items.values()));
        webSocketSessions.forEach(session -> {
            try {
                session.getRemote().sendString(message);
            } catch (IOException e) {
                logger.error("⚠️ WebSocket send error: " + e.getMessage());
            }
        });
    }

    /**
     * WebSocket handler to manage connections and messages.
     */
    @WebSocket
    public static class PriceWebSocketHandler {
        @OnWebSocketConnect
        public void onConnect(Session session) {
            webSocketSessions.add(session);
            logger.info("🟢 WebSocket connected: " + session.getRemoteAddress());
        }

        @OnWebSocketClose
        public void onClose(Session session, int statusCode, String reason) {
            webSocketSessions.remove(session);
            logger.info("🔴 WebSocket disconnected: " + reason);
        }

        @OnWebSocketMessage
        public void onMessage(Session session, String message) {
            logger.info("📩 WebSocket message received: " + message);
        }

        @OnWebSocketError
        public void onError(Session session, Throwable error) {
            logger.error("⚠️ WebSocket error: " + error.getMessage());
        }
    }
}
