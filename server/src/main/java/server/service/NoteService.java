package server.service;

import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import server.database.NoteRepository;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final CollectionConfigService collectionConfigService;

    /**
     * Autowired constructor for NoteService
     *
     * @param noteRepository The repository for notes
     * @param collectionConfigService The service for collections
     */
    @Autowired
    public NoteService(NoteRepository noteRepository, CollectionConfigService collectionConfigService) {
        this.noteRepository = noteRepository;
        this.collectionConfigService = collectionConfigService;
    }

    /**
     * Saves the note, ensuring it has a valid collection.
     * If no collection is set, the default collection is used.
     *
     * @param note The note to be saved
     * @return The saved note
     */
    public Note saveNote(Note note) {
        if (note.getCollectionId() == null) {
            try {
                Collection defaultCollection = collectionConfigService.getOrCreateDefaultCollection();
                note.setCollectionId(defaultCollection.getTitle());
            } catch (IOException e) {
                e.printStackTrace(); // Handle or log error
                throw new RuntimeException("Error accessing the collection configuration");
            }
        }
        return noteRepository.save(note);
    }

    /**
     * Fetches all notes from the repository.
     *
     * @return A list of all notes
     */
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    /**
     * Fetches a specific note by its ID.
     *
     * @param id The ID of the note to retrieve
     * @return The note, or null if not found
     */
    public Note getNoteById(long id) {
        return noteRepository.findById(id).orElse(null);
    }

    /**
     * Updates an existing note with the provided updated note.
     *
     * @param id          The ID of the note to update
     * @param updatedNote The updated note
     * @return The updated note
     */
    public Note updateNote(long id, Note updatedNote) {
        if (!noteRepository.existsById(id)) {
            return null;
        }
        Note existingNote = noteRepository.findById(id).get();
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.renderRawText();
        if (existingNote.getTags() == null) {
            existingNote.setTags(new HashSet<>());
        }
        existingNote.getTags().clear();
        existingNote.extractTagsFromContent();
        return noteRepository.save(existingNote);
    }

    /**
     * Deletes a Note by id
     *
     * @param id The id of the note
     * @return A response entity
     */
    public ResponseEntity<Void> deleteNoteById(long id) {
        if (!noteRepository.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }
        noteRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
