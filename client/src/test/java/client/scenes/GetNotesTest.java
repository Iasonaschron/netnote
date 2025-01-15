package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import client.utils.ServerUtils;
import commons.Note;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Arrays;
import java.util.List;

class GetNotesTest {

    @Mock
    private ServerUtils serverUtils; // This will be the mocked version of ServerUtils

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize the mocks
    }

    @Test
    void getNotesTest() {
        // Arrange
        Note note1 = new Note();
        note1.setId(1);
        note1.setTitle("Note 1");
        note1.setContent("Content of Note 1");

        Note note2 = new Note();
        note2.setId(2);
        note2.setTitle("Note 2");
        note2.setContent("Content of Note 2");

        List<Note> mockNotes = Arrays.asList(note1, note2); // List of notes to mock the response

        // Mock the getNotes method to return the predefined list of notes
        when(serverUtils.getNotes("http://localhost:8080/")).thenReturn(mockNotes);

        // Act
        List<Note> result = serverUtils.getNotes("http://localhost:8080/"); // This will call the mocked method

        // Assert
        assertEquals(2, result.size()); // Check that the returned list has 2 notes
        assertEquals(1, result.get(0).getId()); // Check that the first note has ID 1
        assertEquals("Note 1", result.get(0).getTitle()); // Check that the title of the first note is correct
        assertEquals("Content of Note 1", result.get(0).getContent()); // Check the content of the first note

        assertEquals(2, result.get(1).getId()); // Check that the second note has ID 2
        assertEquals("Note 2", result.get(1).getTitle()); // Check that the title of the second note is correct
        assertEquals("Content of Note 2", result.get(1).getContent()); // Check the content of the second note
    }

    @Test
    void getEmptyNotesTest() {
        // Arrange
        List<Note> emptyList = Arrays.asList(); // Empty list to mock the response

        // Mock the getNotes method to return an empty list
        when(serverUtils.getNotes("http://localhost:8080/")).thenReturn(emptyList);

        // Act
        List<Note> result = serverUtils.getNotes("http://localhost:8080/"); // This will call the mocked method

        // Assert
        assertEquals(0, result.size()); // Check that the returned list is empty
    }
}
