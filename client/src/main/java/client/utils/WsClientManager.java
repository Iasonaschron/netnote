package client.utils;

import client.scenes.NoteOverviewCtrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import commons.Note;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.client.WebSocketClient;


import java.net.URI;

public class WsClientManager {

    ServerUtils serverUtils = new ServerUtils();
    NoteOverviewCtrl useful = new NoteOverviewCtrl(serverUtils);
    private final WebSocketClient client;
    private final ObjectMapper objectMapper;

    public WsClientManager(String serverUri) throws Exception {
        this.objectMapper = new ObjectMapper(); // For JSON parsing
        this.client = new WebSocketClient(new URI("ws://localhost:8080/ws")) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket server.");
                // Subscription messages, setup logic
                send("SUBSCRIBE /topic/notes");
            }

            @Override
            public void onMessage(String message) {
                try {
                    // Parse incoming message as Note
                    Note updatedNote = objectMapper.readValue(message, Note.class);
                    if(updatedNote.getId() == useful.getSelectedNote().getId()) {
                        useful.setSelectedNote(updatedNote);
                    }
                    else {
                        //todo: update titles in listview
                    }
                } catch (Exception e) {
                    System.err.println("Error processing WebSocket message: " + e.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed: " + reason);
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
            }
        };
    }

    // Connect to the WebSocket server
    public void connect() {
        this.client.connect();
    }

    // Disconnect from the WebSocket server
    public void disconnect() {
        this.client.close();
    }
}
