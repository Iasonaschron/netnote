package server.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import commons.Note;
import server.database.NoteRepository;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

    private final NoteRepository repo;

    public NoteController(NoteRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Note> getAllNotes() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty() || note.getContent() == null) {
            return ResponseEntity.badRequest().build();
        }
        Note saved = repo.save(note);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(repo.findById(id).get());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable("id") long id, @RequestBody Note updatedNote) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        Note existingNote = repo.findById(id).get();
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.renderRawText();
        repo.save(existingNote);
        return ResponseEntity.ok(existingNote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNoteById(@PathVariable("id") long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        repo.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
