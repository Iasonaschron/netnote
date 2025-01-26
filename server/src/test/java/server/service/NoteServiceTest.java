package server.service;

import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class NoteServiceTest {

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveNote_savesAndReturnsNote() {
        Note note = new Note();
        when(noteRepository.save(note)).thenReturn(note);

        Note savedNote = noteService.saveNote(note);

        assertEquals(note, savedNote);
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void getAllNotes_returnsListOfAllNotes() {
        List<Note> notes = new ArrayList<>();
        when(noteRepository.findAll()).thenReturn(notes);

        List<Note> result = noteService.getAllNotes();

        assertEquals(notes, result);
        verify(noteRepository, times(1)).findAll();
    }

    @Test
    void getNoteById_returnsNoteIfExists() {
        Note note = new Note();
        when(noteRepository.findById(1L)).thenReturn(Optional.of(note));

        Note result = noteService.getNoteById(1L);

        assertEquals(note, result);
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    void getNoteById_returnsNullIfNotExists() {
        when(noteRepository.findById(1L)).thenReturn(Optional.empty());

        Note result = noteService.getNoteById(1L);

        assertNull(result);
        verify(noteRepository, times(1)).findById(1L);
    }

    @Test
    void updateNote_updatesAndReturnsNoteIfExists() {
        Note existingNote = new Note();
        existingNote.setId(1L);
        existingNote.setTitle("Old Title");
        existingNote.setContent("Old Content");
        when(noteRepository.existsById(1L)).thenReturn(true);
        when(noteRepository.findById(1L)).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        Note updatedNote = new Note();
        updatedNote.setTitle("New Title");
        updatedNote.setContent("New Content");

        Note result = noteService.updateNote(1L, updatedNote);

        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
        verify(noteRepository, times(1)).existsById(1L);
        verify(noteRepository, times(1)).findById(1L);
        verify(noteRepository, times(1)).save(existingNote);
    }

    @Test
    void updateNote_returnsNullIfNotExists() {
        when(noteRepository.existsById(1L)).thenReturn(false);

        Note updatedNote = new Note();
        Note result = noteService.updateNote(1L, updatedNote);

        assertNull(result);
        verify(noteRepository, times(1)).existsById(1L);
        verify(noteRepository, never()).findById(1L);
        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void deleteNoteById_deletesNoteIfExists() {
        when(noteRepository.existsById(1L)).thenReturn(true);

        ResponseEntity<Void> response = noteService.deleteNoteById(1L);

        assertEquals(ResponseEntity.ok().build(), response);
        verify(noteRepository, times(1)).existsById(1L);
        verify(noteRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteNoteById_returnsBadRequestIfNotExists() {
        when(noteRepository.existsById(1L)).thenReturn(false);

        ResponseEntity<Void> response = noteService.deleteNoteById(1L);

        assertEquals(ResponseEntity.badRequest().build(), response);
        verify(noteRepository, times(1)).existsById(1L);
        verify(noteRepository, never()).deleteById(1L);
    }
}