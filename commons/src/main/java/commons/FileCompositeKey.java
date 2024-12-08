package commons;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class FileCompositeKey implements Serializable {
    public String filename;
    private long relatedNoteId;

    public FileCompositeKey() {
    }

    public FileCompositeKey(String filename, long relatedNoteId) {
        this.filename = filename;
        this.relatedNoteId = relatedNoteId;
    }

    public long getRelatedNoteId() {
        return relatedNoteId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileCompositeKey that = (FileCompositeKey) o;
        return getRelatedNoteId() == that.getRelatedNoteId() && Objects.equals(filename, that.filename);
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, getRelatedNoteId());
    }
}
