package com.collectibles.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * WebSocket for broadcasting real-time price updates.
 */
@WebSocket
public class PriceWebSocket {
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.add(session);
        System.out.println("🟢 New WebSocket connection established");
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);
        System.out.println("🔴 WebSocket connection closed");
    }

    @OnWebSocketMessage
    public void onMessage(Session sender, String message) throws IOException {
        // Broadcast message (price update) to all connected sessions
        for (Session session : sessions) {
            if (session.isOpen()) {
                session.getRemote().sendString(message);
            }
        }
    }
}
