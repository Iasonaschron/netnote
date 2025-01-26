package client.utils;

import commons.FileData;
import commons.Note;
import jakarta.ws.rs.ProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerUtilsTest {

    @InjectMocks
    private ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNote_throwsProcessingExceptionWhenServerIsDown() {
        Note note = new Note();
        assertThrows(ProcessingException.class, () -> serverUtils.addNote(note, "http://invalid-server/"));
    }

    @Test
    void changeFileName_returnsFalseWhenUnsuccessful() {
        assertFalse(serverUtils.changeFileName(1L, "oldName.txt", "newName.txt", "http://invalid-server/"));
    }

    @Test
    void deleteAllFiles_returnsFalseWhenUnsuccessful() {
        assertFalse(serverUtils.deleteAllFiles("http://invalid-server/"));
    }

    @Test
    void deleteFile_returnsFalseWhenUnsuccessful() {
        assertFalse(serverUtils.deleteFile(1L, "file.txt", "http://invalid-server/"));
    }

    @Test
    void uploadFile_returnsFalseWhenUnsuccessful() {
        File file = new File("test.txt");
        assertFalse(serverUtils.uploadFile(file, 1L, "http://invalid-server/"));
    }

    @Test
    void saveNote_returnsFalseWhenUnsuccessful() {
        Note note = new Note();
        assertFalse(serverUtils.saveNote(1L, note, "http://invalid-server/"));
    }

    @Test
    void deleteNoteById_returnsFalseWhenUnsuccessful() {
        assertFalse(serverUtils.deleteNoteById(1L, "http://invalid-server/"));
    }

    @Test
    void getNoteById_returnsNullWhenNotFound() {
        Note note = serverUtils.getNoteById(1L, "http://invalid-server/");
        assertNull(note);
    }
}