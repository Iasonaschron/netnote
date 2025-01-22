package commons;

import jakarta.persistence.Embeddable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;

@Component
@Embeddable
public class FileCompositeKey implements Serializable {

    @Autowired
    private String filename;

    private long relatedNoteId;

    /**
     * Constructor for object mappers
     */
    public FileCompositeKey() {
    }

    /**
     * Get the filename
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Set the filename
     * @param filename the filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

    /**
     * Constructor for creating a composite key
     * @param filename the filename
     * @param relatedNoteId the id of the related note
     */
    @Autowired
    public FileCompositeKey(String filename, long relatedNoteId) {
        this.filename = filename;
        this.relatedNoteId = relatedNoteId;
    }

    /**
     *
     * @return the id of the related note
     */
    public long getRelatedNoteId() {
        return relatedNoteId;
    }

    /**
     * Compares this with the argument object and returns true if they are equal
     * @param o the object to compare to
     * @return the truth value of the comparison
     */
    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FileCompositeKey that = (FileCompositeKey) o;
        return getRelatedNoteId() == that.getRelatedNoteId() && Objects.equals(filename, that.filename);
    }

    /**
     *
     * @return the hashcode for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(filename, getRelatedNoteId());
    }
}
