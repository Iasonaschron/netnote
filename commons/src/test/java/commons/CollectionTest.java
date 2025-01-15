package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionTest {

    private Collection collection;
    private Note note1;
    private Note note2;

    @BeforeEach
    public void setUp() {
        note1 = new Note();
        note2 = new Note();
        collection = new Collection("Test Collection", "Test Collection", "http://localhost:8080/");
    }

    @Test
    public void testGetTitle() {
        assertEquals("Test Collection", collection.getTitle());
    }

    @Test
    public void testSetTitle() {
        collection.setTitle("New Title");
        assertEquals("New Title", collection.getTitle());
    }

    @Test
    public void testGetName() {
        assertEquals("Test Collection", collection.getName());
    }

    @Test
    public void testSetName() {
        collection.setName("New Name");
        assertEquals("New Name", collection.getName());
    }

    @Test
    public void testGetServer() {
        assertEquals("http://localhost:8080/", collection.getServer());
    }

    @Test
    public void testSetServer() {
        collection.setServer("http://newserver.com");
        assertEquals("http://newserver.com", collection.getServer());
    }

    @Test
    public void testEquals() {
        Collection otherCollection = new Collection("Test Collection", "Test Collection", "http://localhost:8080/");
        assertTrue(collection.equals(otherCollection));
    }

    @Test
    public void testHashCode() {
        Collection otherCollection = new Collection("Test Collection", "Test Collection", "http://localhost:8080/");
        assertEquals(collection.hashCode(), otherCollection.hashCode());
    }

    @Test
    public void testToString() {
        String expected = "Test Collection";
        assertEquals(expected, collection.toString());
    }
}
