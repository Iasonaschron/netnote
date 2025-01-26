package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Note;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class StompClient extends WebSocketClient {

    private final UpdateListener listener;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructs a new StompClient with the specified server URI and update listener.
     * @param serverUri the URI of the server
     * @param listener the listener for updates
     */
    public StompClient(URI serverUri, UpdateListener listener) {
        super(serverUri);
        this.listener = listener;
    }

    /**
     * Called when the WebSocket connection is opened.
     * @param handshake the handshake data
     */
    @Override
    public void onOpen(ServerHandshake handshake) {
        send("CONNECT\naccept-version:1.2\n\n\0");
        // Subscribes to the deletion topic and update topic
        send("SUBSCRIBE\ndestination:/topic/note-updates\nid:sub-1\n\n\0");
        send("SUBSCRIBE\ndestination:/topic/note-deletions\nid:sub-2\n\n\0");
    }

    /**
     * Called when a message is received from the WebSocket server.
     * @param message the message received
     */
    @Override
    public void onMessage(String message) {
        try {
            if (message.startsWith("MESSAGE")) {
                // Split message by newline to separate headers from the body
                String[] messageParts = message.split("\n\n", 2); // Separate headers from the body

                String headers = messageParts[0];
                String[] headerLines = headers.split("\n");

                String destination = null;
                for (String line : headerLines) {
                    if (line.startsWith("destination:")) {
                        destination = line.split(":")[1].trim();
                        break;
                    }
                }

                String body = messageParts.length > 1 ? messageParts[1] : "";

                if (body != null && !body.isEmpty()) {
                    if (destination != null && destination.equals("/topic/note-updates")) {
                        Note updatedNote = objectMapper.readValue(body, Note.class);
                        System.out.println("Note updated: " + updatedNote.toString());
                        Platform.runLater(() -> {
                            listener.handleNoteUpdate(updatedNote);
                        });

                    } else if (destination != null && destination.equals("/topic/note-deletions")) {
                        Note deletedNote = objectMapper.readValue(body, Note.class);
                        System.out.println("Note deleted with ID: " + deletedNote.getId());
                        Platform.runLater(() -> {
                            listener.handleNoteDeletion(deletedNote.getId());
                        });
                    }
                }
            } else {
                return;
            }

        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
        }
    }

    /**
     * Called when the WebSocket connection is closed.
     * @param code the close code
     * @param reason the reason for closing
     * @param remote whether the close was initiated by the remote server
     */
    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
    }

    /**
     * Called when an error occurs in the WebSocket connection.
     * @param ex the exception that occurred
     */
    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
}
