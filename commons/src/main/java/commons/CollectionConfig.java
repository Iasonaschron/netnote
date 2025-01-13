package commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CollectionConfig {
    private List<Collection> collections;

    /**
     * Initializes an empty config
     */
    public CollectionConfig() {
        collections = new ArrayList<>();
    }

    /**
     * Getter for the collections
     *
     * @return A list of collections
     */
    public List<Collection> getCollections() {
        return collections;
    }

    /**
     * Setter for collections
     *
     * @param collections A list of collections
     */
    public void setCollections(List<Collection> collections) {
        this.collections = collections;
    }

    /**
     * Adds a collection to the list of collections.
     *
     * @param collection The collection to add.
     */
    public void addCollection(Collection collection) {
        if (collections.stream().anyMatch(c -> Objects.equals(c.getTitle(), collection.getTitle()))) {
            throw new IllegalArgumentException("A collection with this title already exists.");
        }
        collections.add(collection);
    }

    /**
     * Removes a collection from the list of collections by its ID.
     *
     * @param collectionId The ID of the collection to remove.
     */
    public void removeCollection(String collectionId) {
        collections.removeIf(c -> c.getTitle().equals(collectionId));
    }

    /**
     * Updates an existing collection in the list.
     *
     * @param oldTitle The title of the collection to be replaced.
     * @param updatedCollection The collection with updated data.
     * @throws IllegalArgumentException if no collection with the given title exists or if a collection with the new title already exists.
     */
    public void updateCollection(String oldTitle, Collection updatedCollection) {
        if (collections.stream().anyMatch(c -> Objects.equals(c.getTitle(), updatedCollection.getTitle()))) {
            throw new IllegalArgumentException("A collection with this title already exists.");
        }

        boolean collectionUpdated = false;

        for (int i = 0; i < collections.size(); i++) {
            if (collections.get(i).getTitle().equals(oldTitle)) {
                collections.set(i, updatedCollection);
                collectionUpdated = true;
                break;
            }
        }

        if (!collectionUpdated) {
            throw new IllegalArgumentException("No collection with the given title exists.");
        }
    }

}
