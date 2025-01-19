package client.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Note;
import javafx.application.Platform;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.messaging.Message;

import java.net.URI;
import java.util.Map;

public class StompClient extends WebSocketClient {

    private final UpdateListener listener;
    ObjectMapper objectMapper = new ObjectMapper();

    public StompClient(URI serverUri, UpdateListener listener) {
        super(serverUri);
        this.listener = listener;
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Connected to STOMP WebSocket server.");
        send("CONNECT\naccept-version:1.2\n\n\0");
        // Subscribes to the deletion topic and update topic
        send("SUBSCRIBE\ndestination:/topic/note-updates\nid:sub-1\n\n\0");
        send("SUBSCRIBE\ndestination:/topic/note-deletions\nid:sub-2\n\n\0");
    }

    @Override
    public void onMessage(String message) {
        try {
            System.out.println("Received message: " + message);

            // Check if it's a STOMP frame message
            if (message.startsWith("MESSAGE")) {
                // Split message by newline to separate headers from the body
                String[] messageParts = message.split("\n\n", 2); // Separate headers from the body

                // Extract the headers (first part)
                String headers = messageParts[0];
                String[] headerLines = headers.split("\n");

                // Find the destination header
                String destination = null;
                for (String line : headerLines) {
                    if (line.startsWith("destination:")) {
                        destination = line.split(":")[1].trim();
                        break;
                    }
                }

                // The body contains the payload
                String body = messageParts.length > 1 ? messageParts[1] : "";

                // Parse the payload if the body exists
                if (body != null && !body.isEmpty()) {
                    // Check if the destination is "note-updates" or "note-deletions"
                    if (destination != null) {
                        if (destination.equals("/topic/note-updates")) {
                            Note updatedNote = objectMapper.readValue(body, Note.class);
                            System.out.println("Note updated: " + updatedNote.toString());
                            Platform.runLater(() -> {listener.handleNoteUpdate(updatedNote);});

                        } else if (destination.equals("/topic/note-deletions")) {
                            Note deletedNote = objectMapper.readValue(body, Note.class);
                            System.out.println("Note deleted with ID: " + deletedNote.getId());
                            Platform.runLater(() -> {listener.handleNoteDeletion(deletedNote.getId());});
                        }
                    }
                }
            } else {
                // Handle unexpected message format (not a valid STOMP frame)
                System.err.println("Unexpected message format: " + message);
            }

        } catch (Exception e) {
            System.err.println("Error processing WebSocket message: " + e.getMessage());
        }
    }

//    @Override
//    public void onMessage(String message) {
//        try {
//            System.out.println("Received message: " + message);
//
////            // Extracting destination (like "/topic/note-updates")
////            String destination = message.getHeaders().get("destination", String.class);
////
////            // Extracting the payload (in this case, the "newNote" content)
////            String payload = new String(message.getPayload());
//
//            // Parse the message
//            Map<String, Object> messageData = objectMapper.readValue(message, Map.class);
//
//            // Extract the destination/topic and payload
//            String destination = (String) messageData.get("destination");
//            System.out.println("Destination: " + destination);
//            Object payload = messageData.get("payload");
//            System.out.println("Payload: " + payload);
//
//            if (destination.equals("/topic/note-updates")) {
//                Note updatedNote = objectMapper.convertValue(payload, Note.class);
//                System.out.println("Note updated: " + updatedNote);
//                listener.handleNoteUpdate(updatedNote);
//
//            }
//            else if (destination.equals("/topic/note-deletions")) {
//                Long deletedNoteId = objectMapper.convertValue(payload, Long.class);
//                System.out.println("Note deleted with ID: " + deletedNoteId);
//                listener.handleNoteDeletion(deletedNoteId);
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error processing WebSocket message: " + e.getMessage());
//        }
//    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from WebSocket server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("WebSocket error: " + ex.getMessage());
    }
}
