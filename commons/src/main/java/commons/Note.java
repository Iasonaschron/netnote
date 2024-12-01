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


    @SuppressWarnings("unused")
    public Note() {
        // for object mappers
    }

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

    @SuppressWarnings("unused")
    public String getHTML() {
        return this.html;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This is just a Setter, but also updates the HTML once the content is set
     * 
     * @param content String containing the raw content
     */
    public void setContent(String content) {
        this.content = content;
        renderRawText();
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @Override
    public String toString() {
        return title;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getId() {
        return id;
    }
}
