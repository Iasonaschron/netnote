package server.api;

import static org.junit.jupiter.api.Assertions.*;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class WsControllerTest {

    @Autowired
    private WsController wsController;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    public void setUp() {
        // Setup code if needed
    }

    @Test
    void broadcastNoteChanges_returnsNewNote() {
        Note newNote = new Note();
        Note result = wsController.broadcastNoteChanges(newNote);
        assertEquals(newNote, result);
    }

    @Test
    void broadcastNoteDeletions_returnsDeletedNote() {
        Note deletedNote = new Note();
        Note result = wsController.broadcastNoteDeletions(deletedNote);
        assertEquals(deletedNote, result);
    }
}