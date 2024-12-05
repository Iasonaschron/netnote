package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NoteTest {

    /**
     * Tests the default constructor of the Note class.
     * Verifies that a new Note object is not null and that its
     * title, content, and HTML fields are null, and its id is 0.
     */
    @Test
    public void testDefaultConstructor() {
        Note note = new Note();
        assertNotNull(note);
        assertNull(note.getTitle());
        assertNull(note.getContent());
        assertEquals(0, note.getId());
        assertNull(note.getHTML());
    }

    /**
     * Tests the constructor of the Note class with parameters.
     * It verifies that the title and content are correctly set,
     * and that the HTML representation of the note is not null.
     */
    @Test
    public void testConstructorWithParameters() {
        String title = "Test Title";
        String content = "Test Content";
        Note note = new Note(title, content);
        assertEquals(title, note.getTitle());
        assertEquals(content, note.getContent());
        assertNotNull(note.getHTML());
    }

    /**
     * Tests the setTitle method of the Note class.
     * This test creates a new Note object, sets its title to "New Title",
     * and asserts that the title has been correctly updated.
     */
    @Test
    public void testSetTitle() {
        Note note = new Note();
        note.setTitle("New Title");
        assertEquals("New Title", note.getTitle());
    }

    /**
     * Tests the setContent method of the Note class.
     * 
     * This test creates a new Note object, sets its content to "New Content",
     * and verifies that the content is correctly set using the getContent method.
     * Additionally, it checks that the HTML representation of the note is not null
     * because this method changes the HTML content.
     */
    @Test
    public void testSetContent() {
        Note note = new Note();
        note.setContent("New Content");
        assertEquals("New Content", note.getContent());
        assertNotNull(note.getHTML());
    }

    /**
     * Tests the setHtml method of the Note class.
     * This test creates a new Note object, sets its HTML content using the setHtml
     * method,
     * and then verifies that the HTML content was correctly set by comparing it to
     * the expected value.
     */
    @Test
    public void testSetHtml() {
        Note note = new Note();
        note.setHtml("<p>HTML Content</p>");
        assertEquals("<p>HTML Content</p>", note.getHTML());
    }

    /**
     * Tests the renderRawText method of the Note class.
     * This test verifies that the HTML content is not null after rendering raw
     * text.
     * 
     * Steps:
     * 1. Create a new Note instance.
     * 2. Set the content of the note to a string with bold text markdown.
     * 3. Call the renderRawText method to convert the markdown to HTML.
     * 4. Assert that the HTML content of the note is not null, therefore the
     * markdown was converted.
     */
    @Test
    public void testRenderRawText() {
        Note note = new Note();
        note.setContent("**Bold Text**");
        note.renderRawText();
        assertNotNull(note.getHTML());
    }

    /**
     * Tests the toString method of the Note class.
     * This test creates a Note object, sets its title,
     * and asserts that the toString method returns the expected title.
     */
    @Test
    public void testToString() {
        Note note = new Note();
        note.setTitle("Sample Title");
        assertEquals("Sample Title", note.toString());
    }

    /**
     * Tests that two Note objects with equal values are equal.
     * This test creates two Note objects with the same title and content,
     * and asserts that they are considered equal.
     */
    @Test
    public void testEquals() {
        Note note1 = new Note("Title", "Content");
        Note note2 = new Note("Title", "Content");
        assertEquals(note1, note2);
    }

    /**
     * Tests that two Note objects with different values are not equal.
     * This test creates two Note instances with distinct titles and contents,
     * and asserts that they are not considered equal.
     */
    @Test
    public void testEqualsDifferent() {
        Note note1 = new Note("Title 1", "Content");
        Note note2 = new Note("Title 2", "Different Content");
        assertNotEquals(note1, note2);
    }

    /**
     * Tests that the hashCode method returns equal values for two Note objects
     * with equal titles and contents.
     */
    @Test
    public void testHashCode() {
        Note note1 = new Note("Title", "Content");
        Note note2 = new Note("Title", "Content");
        assertEquals(note1.hashCode(), note2.hashCode());
    }

    /**
     * Tests that the hashCode method returns different values for two Note objects
     * with different titles and contents.
     */
    @Test
    public void testHashCodeDifferent() {
        Note note1 = new Note("Title 1", "Content");
        Note note2 = new Note("Title 2", "Different Content");
        assertNotEquals(note1.hashCode(), note2.hashCode());
    }
}
