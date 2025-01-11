package server.service;

import commons.Collection;
import commons.Note;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.CollectionRepository;
import server.database.NoteRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
public class NoteService {

    private final NoteRepository noteRepository;
    private final CollectionRepository collectionRepository;

    private static final String DEFAULT_COLLECTION_TITLE = "Default Collection";

    /**
     * Initialize the NoteService class
     *
     * @param noteRepository The repository containing all the notes
     * @param collectionRepository The repository containing all the collections
     */
    public NoteService(NoteRepository noteRepository, CollectionRepository collectionRepository) {
        this.noteRepository = noteRepository;
        this.collectionRepository = collectionRepository;
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
            // Fetch the default collection or create it if it doesn't exist
            Collection defaultCollection = collectionRepository
                    .findByTitle(DEFAULT_COLLECTION_TITLE)
                    .orElseGet(this::createDefaultCollection);
            note.setCollectionId(defaultCollection.getId());
        }
        return noteRepository.save(note);
    }

    /**
     * Creates and saves the default collection if it doesn't exist.
     *
     * @return The saved default collection
     */
    private Collection createDefaultCollection() {
        Collection defaultCollection = new Collection(DEFAULT_COLLECTION_TITLE, new ArrayList<>());
        return collectionRepository.save(defaultCollection);
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
     * Deletes a note by its ID.
     *
     * @param id The ID of the note to delete
     */
    public void deleteNoteById(long id) {
        noteRepository.deleteById(id);
    }
}

