package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import commons.Collection;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    /**
     * Finds a collection by title
     *
     * @param title The title of the collection
     * @return An Optional containing the collection
     */
    Optional<Collection> findByTitle(String title);

    /**
     * Checks if a collection with the given title exists
     *
     * @param title The title of the collection
     * @return True if the collection exists
     */
    boolean existsByTitle(String title);
}
