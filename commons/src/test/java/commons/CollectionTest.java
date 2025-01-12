package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CollectionTest {

    private Collection collection;
    private Note note1;
    private Note note2;

    @BeforeEach
    public void setUp() {
        note1 = new Note();
        note2 = new Note();
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        collection = new Collection("Test Collection", notes);
    }

    @Test
    public void testGetTitle() {
        assertEquals("Test Collection", collection.getTitle());
    }

    @Test
    public void testGetCollection() {
        List<Note> notes = collection.getNotes();
        assertEquals(2, notes.size());
        assertTrue(notes.contains(note1));
        assertTrue(notes.contains(note2));
    }

    @Test
    public void testGetId() {
        assertEquals(0, collection.getId()); // Default value since id is not set
    }

    @Test
    public void testSetTitle() {
        collection.setTitle("New Title");
        assertEquals("New Title", collection.getTitle());
    }

    @Test
    public void testSetCollection() {
        Note note3 = new Note();
        List<Note> newNotes = new ArrayList<>();
        newNotes.add(note3);
        collection.setNotes(newNotes);
        assertEquals(1, collection.getNotes().size());
        assertTrue(collection.getNotes().contains(note3));
    }

    @Test
    public void testAddNote() {
        Note note3 = new Note();
        collection.addNote(note3);
        assertEquals(3, collection.getNotes().size());
        assertTrue(collection.getNotes().contains(note3));
    }

    @Test
    public void testEquals() {
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        Collection otherCollection = new Collection("Test Collection", notes);
        assertTrue(collection.equals(otherCollection));
    }

    @Test
    public void testHashCode() {
        List<Note> notes = new ArrayList<>();
        notes.add(note1);
        notes.add(note2);
        Collection otherCollection = new Collection("Test Collection", notes);
        assertEquals(collection.hashCode(), otherCollection.hashCode());
    }

    @Test
    public void testToString() {
        String expected = "Test Collection:" + note1.toString() + ", " + note2.toString();
        assertEquals(expected, collection.toString());
    }
}
