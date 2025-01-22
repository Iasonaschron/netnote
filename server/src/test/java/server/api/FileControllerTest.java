package server.api;

import commons.FileCompositeKey;
import commons.FileData;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import server.database.FileRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileControllerTest {

    @Mock
    private FileRepository repo;

    @InjectMocks
    private FileController fileController;

    public FileControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    // Utility method for generating a MockMultipartFile
    private MockMultipartFile createMockFile(String name, String content) {
        return new MockMultipartFile(name, content.getBytes());
    }

    /**
     * Tests the successful retrieval of a file.
     */
    @Test
    void testGetFile_Success() {
        FileCompositeKey key = new FileCompositeKey("test.txt", 1L);
        byte[] fileData = "Hello, World!".getBytes();
        when(repo.findById(key)).thenReturn(Optional.of(new FileData("test.txt", fileData, 1L)));

        ResponseEntity<byte[]> response = fileController.getFile(1L, "test.txt");

        assertEquals(200, response.getStatusCodeValue());
        assertArrayEquals(fileData, response.getBody());
    }

    /**
     * Tests the scenario when a file is not found.
     */
    @Test
    void testGetFile_NotFound() {
        FileCompositeKey key = new FileCompositeKey("test.txt", 1L);
        when(repo.findById(key)).thenReturn(Optional.empty());

        ResponseEntity<byte[]> response = fileController.getFile(1L, "test.txt");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }

    /**
     * Tests the successful uploading of a file.
     */
    @Test
    void testPostFile_Success() throws Exception {
        String filename = "test.txt";
        byte[] data = "File content".getBytes();
        FileData fileData = new FileData(filename, data, 1L);

        when(repo.save(fileData)).thenReturn(fileData);

        ResponseEntity<String> response = fileController.postFile(1L, createMockFile(filename, "File content"), filename);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("File uploaded successfully\n", response.getBody());
    }

    /**
     * Tests the failure scenario when uploading a file.
     */
    @Test
    void testPostFile_Failure() throws Exception {
        String filename = "test.txt";
        doThrow(new RuntimeException("Error saving file")).when(repo).save(any(FileData.class));

        ResponseEntity<String> response = fileController.postFile(1L, createMockFile(filename, "Invalid content"), filename);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("File upload failed\n", response.getBody());
    }

    /**
     * Tests the successful deletion of a file.
     */
    @Test
    void testDeleteFile_Success() {
        FileCompositeKey key = new FileCompositeKey("test.txt", 1L);
        doNothing().when(repo).deleteById(key);

        ResponseEntity<String> response = fileController.deleteFile(1L, "test.txt");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("File deleted successfully\n", response.getBody());
    }

    /**
     * Tests the failure scenario when deleting a file.
     */
    @Test
    void testDeleteFile_Failure() {
        FileCompositeKey key = new FileCompositeKey("test.txt", 1L);
        doThrow(new RuntimeException("Delete error")).when(repo).deleteById(key);

        ResponseEntity<String> response = fileController.deleteFile(1L, "test.txt");

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Error deleting file"));
    }

    /**
     * Tests the successful download of a file.
     */
    @Test
    void testDownloadFile_Success() {
        FileCompositeKey fck = new FileCompositeKey("example.txt", 1L);
        byte[] fileData = "Test file content".getBytes();
        FileData file = new FileData("example.txt", fileData, 1L);

        when(repo.findById(fck)).thenReturn(Optional.of(file));

        // Expecting ResponseEntity<Resource>
        ResponseEntity<Resource> response = fileController.downloadFile(1L, "example.txt");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("attachment; filename=\"example.txt\"", response.getHeaders().get("Content-Disposition").get(0));

        // Validate resource content
        Resource resource = response.getBody();
        assertNotNull(resource);
        assertTrue(resource instanceof ByteArrayResource);
        assertArrayEquals(fileData, ((ByteArrayResource) resource).getByteArray());
    }

    /**
     * Tests fetching file names by ID.
     */
    @Test
    void testFetchFileName_Success() {
        FileCompositeKey fck = new FileCompositeKey("example.txt", 1L);
        FileData file = new FileData("example.txt", "Sample content".getBytes(), 1L);

        when(repo.fetchAllFileNamesById(1L)).thenReturn(java.util.Collections.singletonList(file));

        ResponseEntity<java.util.List<FileData>> response = fileController.fetchFileName(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertFalse(response.getBody().isEmpty());
        assertEquals("example.txt", response.getBody().get(0).getFileName());
    }

    /**
     * Tests successfully changing a file name.
     */
    @Test
    void testChangeName_Success() {
        FileCompositeKey key = new FileCompositeKey("oldName.txt", 1L);
        byte[] fileData = "Some file data".getBytes();
        FileData file = new FileData("oldName.txt", fileData, 1L);

        when(repo.findById(key)).thenReturn(Optional.of(file));
        doNothing().when(repo).deleteById(any(FileCompositeKey.class));
        when(repo.save(any(FileData.class))).thenReturn(new FileData("newName.txt", fileData, 1L));

        ResponseEntity<String> response = fileController.changeName(1L, "oldName.txt", "newName.txt");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("File name changed successfully\n", response.getBody());
    }

    /**
     * Tests failure scenario for changing a file name.
     */
    @Test
    void testChangeName_Failure() {
        FileCompositeKey key = new FileCompositeKey("oldName.txt", 1L);
        when(repo.findById(key)).thenReturn(Optional.empty());

        ResponseEntity<String> response = fileController.changeName(1L, "oldName.txt", "newName.txt");

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("File name change failed\n", response.getBody());
    }

    /**
     * Tests the successful deletion of all related files by note ID.
     */
    @Test
    void testDeleteAllRelated_Success() {
        doNothing().when(repo).deleteByNoteId(1L);

        ResponseEntity<String> response = fileController.deleteAllRelated(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Files successfully deleted\n", response.getBody());
    }

    /**
     * Tests failure scenario for deleting all related files by note ID.
     */
    @Test
    void testDeleteAllRelated_Failure() {
        doThrow(new RuntimeException("Error deleting files")).when(repo).deleteByNoteId(1L);

        ResponseEntity<String> response = fileController.deleteAllRelated(1L);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Error deleting files"));
    }

    /**
     * Tests the successful deletion of all files.
     */
    @Test
    void testDeleteAll_Success() {
        doNothing().when(repo).deleteAll();

        ResponseEntity<String> response = fileController.deleteAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Files Deleted success\n", response.getBody());
    }

    /**
     * Tests failure scenario for deleting all files.
     */
    @Test
    void testDeleteAll_Failure() {
        doThrow(new RuntimeException("Error Deleting")).when(repo).deleteAll();

        ResponseEntity<String> response = fileController.deleteAll();

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().contains("Error Deleting"));
    }
}
