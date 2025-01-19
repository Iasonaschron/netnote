package client.utils;

import client.scenes.NoteOverviewCtrl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.Note;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.client.WebSocketClient;
import server.service.NoteService;

import java.net.URI;

public class WsClientManager {

    private final ServerUtils serverUtils = new ServerUtils();
    private final NoteOverviewCtrl useful;
    private final WebSocketClient client;
    private final ObjectMapper objectMapper;

    @Inject
    public WsClientManager(String serverUri, NoteService noteService, NoteOverviewCtrl useful) throws Exception {
        this.objectMapper = new ObjectMapper();
        this.useful = useful;
        this.client = new WebSocketClient(new URI("ws://localhost:8080/ws")) {

            @Override
            public void onOpen(ServerHandshake handshake) {
                System.out.println("Connected to WebSocket server.");
                // Subscription messages, setup logic
                send("SUBSCRIBE /topic/note-updates");
                send("SUBSCRIBE /topic/note-deletions");
            }

            @Override
            public void onMessage(String message) {
                try {
                    Note updatedNote = objectMapper.readValue(message, Note.class);
                    if (updatedNote.getId() == useful.getSelectedNote().getId()) {
                        useful.setSelectedNote(updatedNote);
                    }
                    else if (updatedNote.getCollectionId().equals(useful.getCurrentCollection().getTitle())) {
                        if (noteService.getNoteById(updatedNote.getId()) == null) {
                            useful.addNoteToData(updatedNote);
                            if (useful.getHasSelectedTag()) {
                                useful.tagUpdateList();
                                return;
                            }
                            useful.updateList();
                        }
                        else {
                            Note targetNote = noteService.getNoteById(updatedNote.getId());
                            targetNote.setTitle(updatedNote.getTitle());
                            if (useful.getHasSelectedTag()) {
                                useful.tagUpdateList();
                                return;
                            }
                            useful.updateList();
                        }
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

    public void connect() {
        this.client.connect();
    }

    public void disconnect() {
        this.client.close();
    }
}
