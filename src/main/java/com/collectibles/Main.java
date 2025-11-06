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

    // WebSocket sessions for real-time updates
    private static final Set<Session> webSocketSessions = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        // 1️⃣ Server setup and WebSocket registration
        port(4567); // Default Spark port
        webSocket("/price-updates", PriceWebSocketHandler.class); // WebSocket endpoint
        staticFiles.location("/public"); // Serve static files from src/main/resources/public

        logger.info("🚀 Starting Collectibles Store Web App (Sprint 3)");

        // 🧩 Collectible items list (extended)
        items.put(1, new Item(1, "Holographic Pikachu Card", 249.99,
                "A rare 25th Anniversary Edition holographic Pikachu card, perfectly preserved in a crystal acrylic case. A must-have for Pokémon fans seeking both nostalgia and long-term value."));
        items.put(2, new Item(2, "Iron Man Nano Gauntlet Replica", 499.00,
                "Crafted from high-grade aluminum and LED-lit gemstones, this 1:1 replica of Iron Man’s Nano Gauntlet features authentic sounds and light-up Infinity Stones. The perfect centerpiece for Marvel collectors."));
        items.put(3, new Item(3, "Baby Yoda (Grogu) Plush Limited Edition", 89.99,
                "Soft, detailed, and expressive — this official Lucasfilm collectible captures the gentle curiosity of Grogu. Comes in eco-friendly packaging and includes an adoption certificate."));
        items.put(4, new Item(4, "Cyberpunk Samurai Katana", 329.50,
                "Inspired by Cyberpunk 2077, this steel katana replica glows with an LED neon edge effect and features an engraved blade with the Arasaka insignia. A fusion of tradition and futuristic style."));
        items.put(5, new Item(5, "Legend of Zelda: Master Sword Replica", 279.00,
                "Faithfully recreated with a steel blade and wooden hilt, this full-scale Master Sword includes a decorative pedestal base. Ideal for fans seeking to bring Hyrule’s magic into their home."));
        items.put(6, new Item(6, "Funko Pop! Spider-Man (Miles Morales Edition)", 34.99,
                "A vibrant collectible from the Spider-Verse series, showcasing Miles Morales in his signature electric suit. Comes in a special-edition box with foil detailing and a numbered certificate."));
        items.put(7, new Item(7, "Retro Game Boy Color – Transparent Purple", 189.00,
                "Original refurbished Game Boy Color in its iconic translucent shell. Fully functional and includes a protective display stand — a nostalgic gem for any retro gamer’s shelf."));
        items.put(8, new Item(8, "Star Wars Lightsaber – Darth Vader Edition", 429.99,
                "An official high-fidelity replica from the Black Series collection. Features a metal hilt, realistic sound effects, and a removable red blade for display or dueling practice."));

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

        // 🔍 Filter items by name or price range (safe conversion)
        get("/filter", (req, res) -> {
            String nameFilter = req.queryParams("name");
            String minPriceParam = req.queryParams("min");
            String maxPriceParam = req.queryParams("max");

            // ✅ Validate and parse numeric fields only if not empty
            Double minPrice = (minPriceParam != null && !minPriceParam.isEmpty())
                    ? Double.parseDouble(minPriceParam)
                    : null;
            Double maxPrice = (maxPriceParam != null && !maxPriceParam.isEmpty())
                    ? Double.parseDouble(maxPriceParam)
                    : null;

            List<Item> filteredItems = items.values().stream()
                    .filter(i -> (nameFilter == null || i.getName().toLowerCase().contains(nameFilter.toLowerCase())) &&
                            (minPrice == null || i.getPrice() >= minPrice) &&
                            (maxPrice == null || i.getPrice() <= maxPrice))
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
