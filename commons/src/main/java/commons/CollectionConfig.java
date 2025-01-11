package commons;

import java.util.ArrayList;
import java.util.List;

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
        collections.add(collection);
    }

    /**
     * Removes a collection from the list of collections by its ID.
     *
     * @param collectionId The ID of the collection to remove.
     */
    public void removeCollection(Long collectionId) {
        collections.removeIf(c -> c.getId().equals(collectionId));
    }

    /**
     * Updates an existing collection in the list.
     *
     * @param updatedCollection The collection with updated data.
     */
    public void updateCollection(Collection updatedCollection) {
        for (int i = 0; i < collections.size(); i++) {
            if (collections.get(i).getId().equals(updatedCollection.getId())) {
                collections.set(i, updatedCollection);
                break;
            }
        }
    }
}
