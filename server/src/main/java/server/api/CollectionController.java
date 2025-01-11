package server.api;

import commons.Collection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.database.CollectionRepository;
import server.service.CollectionConfigService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/collection")
public class CollectionController {

    private final CollectionRepository repo;
    private final CollectionConfigService collectionConfigService;

    /**
     * Initializes the Controller with a Collection Repository and Collection Config Service
     *
     * @param repo The Collection Repository in which the collections will be
     *             stored.
     * @param collectionConfigService The service for managing the collection config file.
     */
    public CollectionController(CollectionRepository repo, CollectionConfigService collectionConfigService) {
        this.repo = repo;
        this.collectionConfigService = collectionConfigService;
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
                return ResponseEntity.badRequest().build();
            }

            // Save the collection to the repository.
            Collection saved = repo.save(coll);
            collectionConfigService.addCollectionToConfig(saved); // Update the config
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Updates an existing collection in the repository.
     *
     * @param id The id of the collection to update.
     * @param updatedCollection The updated collection data.
     * @return ResponseEntity with the updated collection if successful, bad request
     *         otherwise.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable Long id, @RequestBody Collection updatedCollection) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        Collection existingCollection = repo.findById(id).get();

        existingCollection.setTitle(updatedCollection.getTitle());
        existingCollection.setNotes(updatedCollection.getNotes());
        Collection updated = repo.save(existingCollection);

        try {
            collectionConfigService.updateCollectionInConfig(updated); // Update the config
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(updated);
    }

    /**
     * Deletes a collection from the repository and the config file.
     *
     * @param id The id of the collection to delete.
     * @return ResponseEntity with no content if successful, bad request otherwise.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.badRequest().build();
        }

        // Delete the collection from the repository
        Collection collectionToDelete = repo.findById(id).get();
        repo.delete(collectionToDelete);

        try {
            collectionConfigService.removeCollectionFromConfig(id); // Update the config
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.noContent().build();
    }
}
