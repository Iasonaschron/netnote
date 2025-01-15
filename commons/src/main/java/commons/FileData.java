package commons;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import org.springframework.beans.factory.annotation.Autowired;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileData {

    @EmbeddedId
    private FileCompositeKey id;

    @Lob
    private byte[] data;

    /**
     * Constructor for object mappers
     */
    public FileData() {
    }

    /**
     * Constructor for a FileData object
     * @param filename The name of the file
     * @param data the binary data of the file
     * @param relatedNoteId the id of the related note
     */
    @Autowired
    public FileData(String filename, byte[] data, long relatedNoteId) {
        this.data = data;
        id = new FileCompositeKey(filename, relatedNoteId);
    }

    /**
     *
     * @return The id of the related note
     */
    public long getRelatedNoteId() {
        return id.getRelatedNoteId();
    }

    /**
     *
     * @return The name of the file
     */
    public String getFileName() {
        return id.filename;
    }

    /**
     *
     * @return The primary key of this object
     */
    public FileCompositeKey getId() {
        return id;
    }

    /**
     * sets the filename
     * @param name
     */
    public void setFileName(String name){
        id.filename = name;
    }

    /**
     *
     * @return The binary data of the file
     */
    public byte[] getData() {
        return data;
    }
}
