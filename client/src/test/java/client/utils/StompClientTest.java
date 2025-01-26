package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Note;
import javafx.application.Platform;
import org.java_websocket.handshake.ServerHandshake;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StompClientTest {

    @Mock
    private UpdateListener listener;

    private StompClient stompClient;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        stompClient = new StompClient(new URI("ws://localhost:8080"), listener);
    }

    @Test
    void onMessage_ignoresNonMessageFrames() {
        String message = "CONNECTED\nversion:1.2\n\n\0";
        stompClient.onMessage(message);
        verifyNoInteractions(listener);
    }

    @Test
    void onClose_logsDisconnection() {
        stompClient.onClose(1000, "Normal closure", true);
        // Check console output for "Disconnected from WebSocket server: Normal closure"
    }

    @Test
    void onError_logsError() {
        Exception ex = new Exception("Test error");
        stompClient.onError(ex);
        // Check console output for "WebSocket error: Test error"
    }
}