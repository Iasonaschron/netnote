package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Note;
import server.database.NoteRepository;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository repo;

    /**
     * Initializes the controller with the provided NoteRepository
     *
     * @param repo The NoteRepository used for storing the notes
     */
    public NoteController(NoteRepository repo) {
        this.repo = repo;
    }

    /**
     * Getter for all the notes in the database
     *
     * @return A list of all notes
     */
    @GetMapping
    public List<Note> getAllNotes() {
        return repo.findAll();
    }

    /**
     * Adds a new note to the database and adds it to the default collection.
     *
     * @param note The note being added
     * @return A ResponseEntity containing the note if successful, bad request
     *         otherwise
     */
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty() || note.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }
        note.setCollectionId(1001L);
        Note saved = repo.save(note);
        return ResponseEntity.ok(saved);
    }

    /**
     * Getter for a specific note given the ID
     *
     * @param id The ID of the note
     * @return A ResponseEntity containing the note if successful, bad request
     *         otherwise
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    /**
     * Updates an existing note
     *
     * @param id          The ID of the note
     * @param updatedNote The Note containing the new information
     * @return A ResponseEntity containing the updated note if successful, bad
     *         request otherwise
     */
    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note updatedNote) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Note existingNote = repo.findById(id).get();
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.renderRawText();
        if (existingNote.getTags() == null) {
            existingNote.setTags(new HashSet<>());
        }
        existingNote.getTags().clear();
        existingNote.getTags().clear();
        existingNote.extractTagsFromContent();
        repo.save(existingNote);
        return ResponseEntity.ok(existingNote);
    }

    /**
     * Deletes an existing note
     *
     * @param id The ID of the note
     * @return A ResponseEntity indicating the result of the operation
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
