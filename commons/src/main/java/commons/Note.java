package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column
    private long id;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
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
     * Renders the raw Text currently in content into HTML
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
     * Sets the html independently of content
     *
     * @param html String containing new html
     */
    public void setHtml(String html) {
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
