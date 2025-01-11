package server.api;

import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteService noteService;

    /**
     * Initializes the controller with the provided NoteService
     *
     * @param noteService The NoteService used for handling note-related operations
     */
    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * Getter for all the notes in the database
     *
     * @return A list of all notes
     */
    @GetMapping
    public List<Note> getAllNotes() {
        return noteService.getAllNotes();
    }

    /**
     * Adds a new note to the database and adds it to the default collection.
     *
     * @param note The note being added
     * @return A ResponseEntity containing the note if successful, bad request otherwise
     */
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty() || note.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }
        Note savedNote = noteService.saveNote(note);
        return ResponseEntity.ok(savedNote);
    }

    /**
     * Getter for a specific note given the ID
     *
     * @param id The ID of the note
     * @return A ResponseEntity containing the note if successful, bad request otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") long id) {
        Note note = noteService.getNoteById(id);
        if (note == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(note);
    }

    /**
     * Updates an existing note
     *
     * @param id          The ID of the note
     * @param updatedNote The Note containing the new information
     * @return A ResponseEntity containing the updated note if successful, bad request otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note updatedNote) {
        Note updated = noteService.updateNote(id, updatedNote);
        if (updated == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes an existing note
     *
     * @param id The ID of the note
     * @return A ResponseEntity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable("id") long id) {
        noteService.deleteNoteById(id);
        return ResponseEntity.ok().build();
    }
}
