package server.api;

import commons.Collection;
import commons.Note;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    private final CollectionRepository repo;

    /**
     * Initializes the Controller with a Collection Repository
     * 
     * @param repo The Collection Repository in which the collections will be
     *             stored.
     */

    public CollectionController(CollectionRepository repo) {
        this.repo = repo;
    }

    /**
     * Getter for all the collections in the repository.
     * 
     * @return Returns a list of all the collections in the repository.
     */

    @GetMapping
    public List<Collection> getAll() {
        return repo.findAll();
    }

    /**
     * Getter for a collection via its ID
     * 
     * @param id The id of the Collection
     * @return A responseEntity with the collection with matching id if successful,
     *         bad request otherwise.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        } else {
            return ResponseEntity.ok(repo.findById(id).get());
        }
    }

    /**
     * Adds a note to the default collection.
     * 
     * @param note the note to be added to the collection.
     * @return A response entity containing the collection if successful otherwise a
     *         bad request.
     */
    @PostMapping("/addNote")
    public ResponseEntity<Collection> addNoteToDefault(@RequestBody Note note) {
        if (note.getTitle() == null || note.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().build();
        } else {
            Optional<Collection> coll = repo.findById(1001L);
            if (coll.isPresent()) {
                coll.get().addNote(note);
                repo.save(coll.get());
                return ResponseEntity.ok(coll.get());
            } else {
                return ResponseEntity.badRequest().build();
            }
        }
    }

    /**
     * Adds a collection to the repository.
     * 
     * @param coll the collection to be added.
     * @return ResponseEntity with the added collection if successful, bad request
     *         otherwise.
     */
    @PostMapping
    public ResponseEntity<Collection> addCollection(@RequestBody Collection coll) {
        // checks if the collection is viable.
        try {
            if (coll.getTitle() == null || coll.getTitle().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            // checks if the collection already exists and does nothing if true.
            if (repo.existsById(coll.getId())) {
                System.out.println("it already exists" + coll.getId());
                return ResponseEntity.ok(coll);
            }
            // saves the collection to the repository.
            else {
                Collection saved = repo.save(coll);
                System.out.println("Saved" + saved.getId() + " " + saved.getTitle());
                return ResponseEntity.ok(saved);
            }
        }
        catch (Exception _){
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * A getter for the default collection in the Collection repository
     * that is hardcoded with an id of 1001.
     * 
     * @return the default collection
     */

    @GetMapping("/defaultCollection")
    public Collection getDefaultCollection() {
        try {
            Optional<Collection> coll = repo.findById(1001L);
            if (coll.isPresent()) {
                return coll.get();
            } else {
                System.out.println("Collection not found");
            }
        } catch (Exception e) {
            System.out.println("It doesnt exist?");
        }
        List<Note> emptyCol = new ArrayList<>();
        return new Collection("Default Collection", emptyCol);
    }
}
