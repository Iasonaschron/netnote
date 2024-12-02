package commons;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String title;
    private String content;
    private String html;


    /**
     * Default constructor required for object mappers
     */
    @SuppressWarnings("unused")
    public Note() {
        // for object mappers
    }

    /**
     * Constructs a new Note with the given title and content
     *
     * @param title The title of the note
     * @param content The content of the note
     */
    @SuppressWarnings("unused")
    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        renderRawText();
    }

    /**
     * Converts the raw content of the note into its HTML representation
     */
    public void renderRawText() {
        this.html = MarkDownMethods.renderRawTextToText(content);
    }

    /**
     * Getter for the title of the note
     *
     * @return The title of the note
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter for the content of the note
     *
     * @return The content of the note
     */
    public String getContent() {
        return content;
    }

    /**
     * Getter for the ID of the note
     *
     * @return The id of the note
     */
    public long getId() {
        return id;
    }

    /**
     * Returns the HTML representation of this note's content
     *
     * @return String containing the HTML representation of the content
     */
    @SuppressWarnings("unused")
    public String getHTML() {
        return this.html;
    }

    /**
     * Sets the title of the note
     *
     * @param title The new title of the note
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the content of the note and updates the HTML attribute
     * 
     * @param content String containing the raw content
     */
    public void setContent(String content) {
        this.content = content;
        renderRawText();
    }

    /**
    * Sets the html of the note independently
    *
    *@param html String containing new html
    */ 
    public void setHTML(String html) {
        this.html = html;
    }

    /**
     * Checks if this note is equal to another object
     *
     * @param obj The object to compare to
     * @return True if the two objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    /**
     * Generates a hash code for this note
     *
     * @return The hash code of this note
     */
    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    /**
     * Returns a string representation of the note
     *
     * @return The title of the note
     */
    @Override
    public String toString() {
        return title;
    }
}
