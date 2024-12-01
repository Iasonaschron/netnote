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
    public long id;

    public String title;
    public String content;
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
        this.html = MarkDownMethods.renderRaw_TextToText(content);
    }

    @SuppressWarnings("unused")
    public String getHTML() {
        return this.html;
    }

    /**
     * This is just a Setter, but also updates the HTML once the content is set
     * 
     * @param content uh its just the content
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
