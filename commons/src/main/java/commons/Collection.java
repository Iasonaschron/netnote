package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a collection of notes.
 * It is related to Notes in that every note has exactly 1 collection it belongs
 * to.
 */
public class Collection {
    private String title;

    /**
     * Default constructor required for the object mapper.
     */
    public Collection() {
    }

    /**
     * Constructs a new collection provided a title and List of notes
     * 
     * @param title      The title of the collection
     */
    public Collection(String title) {
        this.title = title;
    }

    /**
     * Getter for the title of the collection.
     * 
     * @return The title of the collection.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the collection.
     * 
     * @param title the new title of the collection.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Checks if the provided object is equal to the collection.
     * 
     * @param o the object to be compared.
     * @return true if the objects are equal, otherwise returns false.
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Generates a hashcode for this collection.
     * 
     * @return the hashcode for this collection.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the collection
     * 
     * @return the titles of the collection and the titles of the notes in the
     *         collection.
     */
    @Override
    public String toString() {
        return title;
    }
}
