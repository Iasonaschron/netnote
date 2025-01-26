package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class FileDataTest {

    @Test
    void constructor_initializesFieldsCorrectly() {
        byte[] data = {1, 2, 3};
        FileData fileData = new FileData("file.txt", data, 1L);

        assertEquals("file.txt", fileData.getFileName());
        assertArrayEquals(data, fileData.getData());
        assertEquals(1L, fileData.getRelatedNoteId());
    }

    @Test
    void getFileName_returnsCorrectFilename() {
        FileData fileData = new FileData("file.txt", new byte[]{}, 1L);
        assertEquals("file.txt", fileData.getFileName());
    }

    @Test
    void getRelatedNoteId_returnsCorrectId() {
        FileData fileData = new FileData("file.txt", new byte[]{}, 1L);
        assertEquals(1L, fileData.getRelatedNoteId());
    }

    @Test
    void getData_returnsCorrectData() {
        byte[] data = {1, 2, 3};
        FileData fileData = new FileData("file.txt", data, 1L);
        assertArrayEquals(data, fileData.getData());
    }

    @Test
    void setFileName_updatesFilename() {
        FileData fileData = new FileData("file.txt", new byte[]{}, 1L);
        fileData.setFileName("newfile.txt");
        assertEquals("newfile.txt", fileData.getFileName());
    }

    @Test
    void getId_returnsCorrectId() {
        FileData fileData = new FileData("file.txt", new byte[]{}, 1L);
        FileCompositeKey id = fileData.getId();
        assertEquals("file.txt", id.getFilename());
        assertEquals(1L, id.getRelatedNoteId());
    }
}