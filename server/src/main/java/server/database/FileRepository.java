package server.database;

import commons.FileCompositeKey;
import commons.FileData;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FileRepository extends JpaRepository<FileData, FileCompositeKey> {
    /**
     * Perform a query that deletes all files in a certain note
     * @param noteid The id of the note
     */
    @Transactional
    @Modifying
    @Query("DELETE FROM FileData f WHERE f.id.relatedNoteId = :noteid")
    void deleteByNoteId(long noteid);
}
