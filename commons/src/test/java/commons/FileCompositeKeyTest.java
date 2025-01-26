package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileCompositeKeyTest {

    @Test
    void equals_returnsTrueForSameObject() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        assertTrue(key.equals(key));
    }

    @Test
    void equals_returnsTrueForEqualObjects() {
        FileCompositeKey key1 = new FileCompositeKey("file.txt", 1L);
        FileCompositeKey key2 = new FileCompositeKey("file.txt", 1L);
        assertTrue(key1.equals(key2));
    }

    @Test
    void equals_returnsFalseForDifferentObjects() {
        FileCompositeKey key1 = new FileCompositeKey("file1.txt", 1L);
        FileCompositeKey key2 = new FileCompositeKey("file2.txt", 2L);
        assertFalse(key1.equals(key2));
    }

    @Test
    void equals_returnsFalseForNull() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        assertFalse(key.equals(null));
    }

    @Test
    void equals_returnsFalseForDifferentClass() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        String other = "not a key";
        assertFalse(key.equals(other));
    }

    @Test
    void hashCode_isConsistentWithEquals() {
        FileCompositeKey key1 = new FileCompositeKey("file.txt", 1L);
        FileCompositeKey key2 = new FileCompositeKey("file.txt", 1L);
        assertEquals(key1.hashCode(), key2.hashCode());
    }

    @Test
    void getFilename_returnsCorrectFilename() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        assertEquals("file.txt", key.getFilename());
    }

    @Test
    void getRelatedNoteId_returnsCorrectId() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        assertEquals(1L, key.getRelatedNoteId());
    }

    @Test
    void setFilename_updatesFilename() {
        FileCompositeKey key = new FileCompositeKey("file.txt", 1L);
        key.setFilename("newfile.txt");
        assertEquals("newfile.txt", key.getFilename());
    }
}