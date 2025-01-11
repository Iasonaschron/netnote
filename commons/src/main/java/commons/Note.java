package commons;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @ElementCollection
    @CollectionTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"))
    @Column(name = "tag")
    private Set<String> tags;

    @Column(name = "collection_id") // Foreign key
    private Long collectionId;

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
     * @param title   The title of the note
     * @param content The content of the note
     */
    public Note(String title, String content) {
        this.title = title;
        if (content != null){
            this.content = content;
        }
        else{
            this.content = "";
        }
        collectionId = null;
        renderRawText();
        this.tags = new HashSet<>();
        extractTagsFromContent();
    }

    /**
     * Additional constructor for mentioning the collection id of the note
     *
     * @param title   The title of the note
     * @param content The content of the note
     * @param collectionId The id of the collection
     */
    public Note(String title, String content, Long collectionId) {
        this(title, content);
        this.collectionId = collectionId;
    }

    /**
     * Extracts all tags from the note's content and adds them to the tags set.
     * Skips processing if the content is null.
     */
    public void extractTagsFromContent() {
        String regex = "#(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            tags.add(matcher.group(1));
        }
    }

    /**
     * Renders the raw Text currently in content into HTML
     */
    public void renderRawText() {
        String processedContent = ContentProcessor.processTags(content);

        this.html = MarkDownMethods.renderRawTextToText(processedContent, id);
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
     * Getter for the id of the note
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
     * Retrieves the collection associated with this object.
     *
     * @return a Collection representing the current collection.
     */
    public Long getCollectionId() {
        return collectionId;
    }

    /**
     * Returns the tags in the content of the note
     *
     * @return Set containing all the tags inside the content
     */
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Sets the tags of the note
     *
     * @param tags The new Set of tags of the note
     */
    public void setTags(Set<String> tags) {
        this.tags = tags;
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
     * Sets the id of the note, primarily used for testing
     *
     * @param id long representing the id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the collection that the note belongs to
     *
     * @param collection the new collection that the note now belongs to.
     */
    public void setCollectionId(Long collection) {
        this.collectionId = collection;
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
