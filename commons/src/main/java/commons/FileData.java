package commons;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;

@Entity
public class FileData {

    @EmbeddedId
    private FileCompositeKey id;

    @Lob
    private byte[] data;

    public FileData() {
    }

    public FileData(String filename, byte[] data, long relatedNoteId) {
        this.data = data;
        id = new FileCompositeKey(filename, relatedNoteId);
    }

    public long getRelatedNoteId() {
        return id.getRelatedNoteId();
    }

    public String getFileName() {
        return id.filename;
    }

    public FileCompositeKey getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
