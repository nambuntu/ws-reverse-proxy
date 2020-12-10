package com.zenhomes.nfreverseproxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Represents a 'hop' in the proxying chain, establishes a 'client' to
 * communicate with the next server, with a {@link WebSocketProxyClientHandler}
 * to copy data from the 'client' to the supplied 'server' session.
 */
public class NextHop {

    private final WebSocketSession webSocketClientSession;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public NextHop(WebSocketSession webSocketServerSession) {
        webSocketClientSession = createWebSocketClientSession(webSocketServerSession);
    }

    private WebSocketHttpHeaders getWebSocketHttpHeaders(final WebSocketSession userAgentSession) {
        WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
        Principal principal = userAgentSession.getPrincipal();
        return headers;
    }

    private WebSocketSession createWebSocketClientSession(WebSocketSession webSocketServerSession) {
        try {
            WebSocketHttpHeaders headers = getWebSocketHttpHeaders(webSocketServerSession);
            return new StandardWebSocketClient()
                    .doHandshake(new WebSocketProxyClientHandler(webSocketServerSession), headers, new URI("ws://localhost:9999"))
                    .get(1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessageToNextHop(WebSocketMessage<?> webSocketMessage) throws IOException {
        webSocketClientSession.sendMessage(webSocketMessage);
    }
}
