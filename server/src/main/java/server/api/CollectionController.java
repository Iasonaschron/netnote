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

            // Check if a collection with the same title already exists.
            if (repo.existsByTitle(coll.getTitle())) {
                System.out.println("A collection with the title already exists: " + coll.getTitle());
                return ResponseEntity.badRequest().build();
            }

            // checks if the collection already exists and does nothing if true.
            if (repo.existsById(coll.getId())) {
                System.out.println("it already exists" + coll.getId());
                return ResponseEntity.badRequest().build();
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
}
