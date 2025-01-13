package server.api;

import commons.Collection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class CollectionConfigTest {

    private CollectionConfig collectionConfig;

    @BeforeEach
    void setUp() {
        collectionConfig = new CollectionConfig();
    }

    @Test
    public void constructorTest() {
        assertNotNull(collectionConfig.getCollections());

        assertTrue(collectionConfig.getCollections().isEmpty());
    }

    @Test
    void getCollections() {
        assertNotNull(collectionConfig.getCollections());
        ArrayList<Collection> collectionsTest = new ArrayList<>();

        Collection collTest = new Collection("Test1", new ArrayList<>());
        collectionsTest.add(collTest);

        collectionConfig.setCollections(collectionsTest);
        assertNotNull(collectionConfig.getCollections());
        assertEquals(collectionConfig.getCollections(), collectionsTest);

    }

    @Test
    void setCollections() {

        ArrayList<Collection> collectionsTest = new ArrayList<>();
        Collection collTest = new Collection("Test2", new ArrayList<>());
        collectionsTest.add(collTest);

        collectionConfig.setCollections(collectionsTest);

        assertEquals(collectionConfig.getCollections(), collectionsTest);
        assertNotNull(collectionConfig.getCollections());
    }

    @Test
    void addCollection() {
        Collection collTest = new Collection("Test3", new ArrayList<>());
        collectionConfig.addCollection(collTest);
        assertNotNull(collectionConfig.getCollections());
        assertEquals(collectionConfig.getCollections().get(0).getTitle(), "Test3");

    }

    @Test
    void addDuplicateCollection() {
        Collection collTest = new Collection("Test3", new ArrayList<>());
        Collection collTest2 = new Collection("Test3", new ArrayList<>());
        collectionConfig.addCollection(collTest);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> collectionConfig.addCollection(collTest2));
        assertEquals("A collection with this title already exists.", thrown.getMessage());
        assertEquals(collectionConfig.getCollections().size(), 1);
    }

    @Test
    void addEmptyTitle() {
        Collection collTest = new Collection("", new ArrayList<>());
        collectionConfig.addCollection(collTest);
        assertEquals(collectionConfig.getCollections().size(), 1);
        assertEquals(collectionConfig.getCollections().get(0).getTitle(), "");
    }

    @Test
    void removeCollection() {
        Collection collTest = new Collection("Test4", new ArrayList<>());

        collectionConfig.addCollection(collTest);
        collectionConfig.removeCollection(collTest.getId());

        assertNotNull(collectionConfig.getCollections());
        assertTrue(collectionConfig.getCollections().isEmpty());

    }

    @Test
    void removeWhileEmpty() {
        Collection collTest = new Collection("Test5", new ArrayList<>());
        collectionConfig.removeCollection(collTest.getId());
        assertTrue(collectionConfig.getCollections().isEmpty());

    }

    @Test
    void updateCollection() {
        Collection collTest = new Collection("Test5", new ArrayList<>());
        collectionConfig.addCollection(collTest);

        Collection updatedCollection = new Collection("UpdatedCollection", new ArrayList<>());
        collectionConfig.updateCollection("Test5", updatedCollection);
        assertNotNull(collectionConfig.getCollections());
        assertEquals("UpdatedCollection", collectionConfig.getCollections().get(0).getTitle());
        assertEquals(1, collectionConfig.getCollections().size());
    }

    // Only important if we allow Collections with duplicate titles.
    @Test
    void updatingToDuplicateCollection() {
        Collection collTest = new Collection("Test6", new ArrayList<>());
        Collection collTest2 = new Collection("Test7", new ArrayList<>());
        collectionConfig.addCollection(collTest);
        collectionConfig.addCollection(collTest2);

        Collection updatedCollection = new Collection("Test6", new ArrayList<>());

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> collectionConfig.updateCollection("Test7", updatedCollection));
        assertEquals("Duplicate Collection titles are not allowed", thrown.getMessage());

    }
}