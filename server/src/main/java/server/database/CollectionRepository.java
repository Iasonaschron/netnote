package server.database;

import org.springframework.data.jpa.repository.JpaRepository;
import commons.Collection;

import java.util.Optional;

public interface CollectionRepository extends JpaRepository<Collection, Long> {
    Optional<Collection> findByTitle(String title);

    boolean existsByTitle(String title);
}
