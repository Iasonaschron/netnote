package commons;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Represents a collection of notes.
 * It is related to Notes in that every note has exactly 1 collection it belongs to.
 */
public class Collection {
    private String title;
    private String name;
    private String server;

    /**
     * Default constructor required for the object mapper.
     */
    public Collection() {
    }

    /**
     * Constructs a new Collection with the specified title, using the title for both the name and the URL.
     *
     * @param title The title of the collection.
     */
    public Collection(String title) {
        this(title, title, "http://localhost:8080/");
    }

    /**
     * Constructs a new Collection with the specified title and name, using a default URL.
     *
     * @param title The title of the collection.
     * @param name The name of the collection.
     */
    public Collection(String title, String name) {
        this(title, name, "http://localhost:8080/");
    }


    /**
     * Constructs a new collection provided a title, name, and server URL.
     *
     * @param title  The title of the collection.
     * @param name   The name of the collection.
     * @param server The server URL of the collection.
     */
    public Collection(String title, String name, String server) {
        this.title = title;
        this.name = name;
        this.server = server;
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
     * Setter for the title of the collection.
     *
     * @param title the new title of the collection.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Getter for the name of the collection.
     *
     * @return The name of the collection.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the name of the collection.
     *
     * @param name the new name of the collection.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the server URL of the collection.
     *
     * @return The server URL of the collection.
     */
    public String getServer() {
        return server;
    }

    /**
     * Setter for the server URL of the collection.
     *
     * @param server the new server URL of the collection.
     */
    public void setServer(String server) {
        this.server = server;
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
     * Returns a string representation of the collection.
     *
     * @return the title of the collection.
     */
    @Override
    public String toString() {
        return title;
    }
}
