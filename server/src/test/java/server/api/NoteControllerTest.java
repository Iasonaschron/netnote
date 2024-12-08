package server.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import server.database.NoteRepository;
import commons.Note;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests for the NoteController class.
 */
public class NoteControllerTest {

    @Mock
    private NoteRepository repo;

    @InjectMocks
    private NoteController controller;

    /**
     * Sets up the test environment before each test method is executed.
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the getAllNotes method of the NoteController.
     * Verifies that the method returns the correct list of notes.
     */
    @Test
    public void testGetAllNotes() {
        Note note1 = new Note();
        note1.setTitle("Note 1");
        note1.setContent("Content 1");

        Note note2 = new Note();
        note2.setTitle("Note 2");
        note2.setContent("Content 2");

        when(repo.findAll()).thenReturn(Arrays.asList(note1, note2));

        List<Note> notes = controller.getAllNotes();
        assertEquals(2, notes.size());
        assertEquals("Note 1", notes.get(0).getTitle());
        assertEquals("Note 2", notes.get(1).getTitle());
    }

    /**
     * Tests the addNote method of the NoteController.
     * Verifies that a new note is added successfully and returns the correct
     * response.
     */
    @Test
    public void testAddNote() {
        Note note = new Note();
        note.setTitle("New Note");
        note.setContent("New Content");

        when(repo.save(note)).thenReturn(note);

        ResponseEntity<Note> response = controller.addNote(note);
        assertEquals(200, response.getStatusCode().value());
        Note responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("New Note", responseBody.getTitle());
    }

    /**
     * Tests the addNote method of the NoteController with invalid input.
     * Verifies that the method returns a bad request response when the note title
     * is empty.
     */
    @Test
    public void testAddNoteBadRequest() {
        Note note = new Note();
        note.setTitle("");
        note.setContent("Content");

        ResponseEntity<Note> response = controller.addNote(note);
        assertEquals(400, response.getStatusCode().value());
    }

    /**
     * Tests the getNoteById method of the NoteController.
     * Verifies that the method returns the correct note when the note exists.
     */
    @Test
    public void testGetNoteById() {
        Note note = new Note();
        note.setTitle("Note");
        note.setContent("Content");

        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(note));

        ResponseEntity<Note> response = controller.getNoteById(1L);
        assertEquals(200, response.getStatusCode().value());
        Note responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Note", responseBody.getTitle());
    }

    /**
     * Tests the getNoteById method of the NoteController when the note does not
     * exist.
     * Verifies that the method returns a not found response.
     */
    @Test
    public void testGetNoteByIdNotFound() {
        when(repo.existsById(1L)).thenReturn(false);

        ResponseEntity<Note> response = controller.getNoteById(1L);
        assertEquals(400, response.getStatusCode().value());
    }

    /**
     * Tests the updateNote method of the NoteController.
     * Verifies that an existing note is updated successfully and returns the
     * correct response.
     */
    @Test
    public void testUpdateNote() {
        Note existingNote = new Note();
        existingNote.setTitle("Existing Note");
        existingNote.setContent("Existing Content");

        Note updatedNote = new Note();
        updatedNote.setTitle("Updated Note");
        updatedNote.setContent("Updated Content");

        when(repo.existsById(1L)).thenReturn(true);
        when(repo.findById(1L)).thenReturn(Optional.of(existingNote));
        when(repo.save(existingNote)).thenReturn(existingNote);

        ResponseEntity<Note> response = controller.updateNote(1L, updatedNote);
        assertEquals(200, response.getStatusCode().value());
        Note responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Updated Note", responseBody.getTitle());
    }

    /**
     * Tests the updateNote method of the NoteController when the note does not
     * exist.
     * Verifies that the method returns a not found response.
     */
    @Test
    public void testUpdateNoteNotFound() {
        Note updatedNote = new Note();
        updatedNote.setTitle("Updated Note");
        updatedNote.setContent("Updated Content");

        when(repo.existsById(1L)).thenReturn(false);

        ResponseEntity<Note> response = controller.updateNote(1L, updatedNote);
        assertEquals(400, response.getStatusCode().value());
    }

    /**
     * Tests the deleteNoteById method of the NoteController.
     * Verifies that an existing note is deleted successfully and returns the
     * correct response.
     */
    @Test
    public void testDeleteNoteById() {
        when(repo.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteNoteById(1L);
        assertEquals(200, response.getStatusCode().value());
        verify(repo, times(1)).deleteById(1L);
    }

    /**
     * Tests the deleteNoteById method of the NoteController when the note does not
     * exist.
     * Verifies that the method returns a not found response.
     */
    @Test
    public void testDeleteNoteByIdNotFound() {
        when(repo.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteNoteById(1L);
        assertEquals(400, response.getStatusCode().value());
    }
}
