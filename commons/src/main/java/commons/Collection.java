package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.stream.Collectors;


/**
 * Represents a collection of notes.
 * It is related to Notes in that every note has exactly 1 collection it belongs to.
 */
@Entity
public class Collection {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)

    @Column
    private long id;

    @Column
    private String title;

    @OneToMany(mappedBy = "collection", cascade = CascadeType.ALL)
    private List<Note> collection;

    /**
     *Default constructor required for the object mapper.
     */
    public Collection() {}

    /**
     * Constructs a new collection provided a title and List of notes
     * @param title The title of the collection
     * @param collection The list of notes that belong to this collection.
     */
    public Collection(String title, List<Note> collection) {
        this.title = title;
        this.collection = collection;
    }

    /**
     * Getter for the title of the collection.
     * @return The title of the collection.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the collection of notes.
     * @return The collection of notes.
     */
    public List<Note> getCollection() {
        return collection;
    }

    /**
     * The getter for the id of the collection.
     * @return The id of the collection.
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the title of the collection.
     * @param title the new title of the collection.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the list of notes that belongs to the collection.
     * @param collection the new list of notes.
     */
    public void setCollection(List<Note> collection) {
        this.collection = collection;
    }

    /**
     * Adds a note to the collection.
     * @param note the new note to be added.
     */
    public void addNote(Note note) {
        collection.add(note);
    }

    /**
     * Checks if the provided object is equal to the collection.
     * @param o the object to be compared.
     * @return true if the objects are equal, otherwise returns false.
     */
    @Override
    public boolean equals(Object o) {
        return EqualsBuilder.reflectionEquals(this, o);
    }

    /**
     * Generates a hashcode for this collection.
     * @return the hashcode for this collection.
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the collection
     * @return the titles of the collection and the titles of the notes in the collection.
     */
    @Override
    public String toString() {
        return title + ":"  + collection.stream().map(Note::toString).collect(Collectors.joining(", "));
    }
}



