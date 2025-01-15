package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import client.utils.ServerUtils;
import commons.Note;
import org.aspectj.weaver.ast.Not;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

class AddingNoteTest {

    @Mock
    private ServerUtils serverUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validNoteTest() {
        Note inputNote = new Note();
        inputNote.setTitle("Test Title");
        inputNote.setContent("Test Content");

        Note mockNote = new Note();
        mockNote.setId(1); // Simulate the ID set by the server (e.g., auto-generated ID)
        mockNote.setTitle("Test Title");
        mockNote.setContent("Test Content");

        when(serverUtils.addNote(inputNote, "http://localhost:8080/")).thenReturn(mockNote); // Define the mock behavior

        Note result = serverUtils.addNote(inputNote, "http://localhost:8080/"); // This will call the mocked method

        assertEquals(1, result.getId()); // Check that the ID is set to 1
        assertEquals("Test Title", result.getTitle()); // Check the title
        assertEquals("Test Content", result.getContent()); // Check the content
    }

    @Test
    void emptyTitleTest() {
        Note inputNote = new Note();
        inputNote.setTitle(""); // Empty title
        inputNote.setContent("Test Content");

        Note mockNote = new Note();
        mockNote.setId(2); // Simulate the ID set by the server
        mockNote.setTitle(""); // Empty title
        mockNote.setContent("Test Content");

        when(serverUtils.addNote(inputNote, "http://localhost:8080/")).thenReturn(mockNote); // Define the mock behavior

        Note result = serverUtils.addNote(inputNote, "http://localhost:8080/"); // This will call the mocked method

        assertEquals(2, result.getId()); // Check that the ID is set to 2
        assertEquals("", result.getTitle()); // Ensure the title is empty
        assertEquals("Test Content", result.getContent()); // Ensure the content is correct
    }

    @Test
    void emptyContentTest() {
        Note inputNote = new Note();
        inputNote.setTitle("Test Title");
        inputNote.setContent(""); // Empty content

        Note mockNote = new Note();
        mockNote.setId(3); // Simulate the ID set by the server
        mockNote.setTitle("Test Title");
        mockNote.setContent(""); // Empty content

        when(serverUtils.addNote(inputNote, "http://localhost:8080/")).thenReturn(mockNote); // Define the mock behavior

        Note result = serverUtils.addNote(inputNote, "http://localhost:8080/"); // This will call the mocked method

        assertEquals(3, result.getId()); // Check that the ID is set to 3
        assertEquals("Test Title", result.getTitle()); // Ensure the title is correct
        assertEquals("", result.getContent()); // Ensure the content is empty
    }
}
