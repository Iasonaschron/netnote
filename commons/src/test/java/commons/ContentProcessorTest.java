package commons;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContentProcessorTest {

    @Test
    void processTags_replacesSingleTagWithLink() {
        String content = "This is a #tag.";
        String expected = "This is a <a href=\"tag://tag\" onclick=\"alert('tag://tag')\">#tag</a>.";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }

    @Test
    void processTags_replacesMultipleTagsWithLinks() {
        String content = "This is a #tag1 and another #tag2.";
        String expected = "This is a <a href=\"tag://tag1\" onclick=\"alert('tag://tag1')\">#tag1</a> and another <a href=\"tag://tag2\" onclick=\"alert('tag://tag2')\">#tag2</a>.";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }

    @Test
    void processTags_handlesContentWithoutTags() {
        String content = "This is a content without tags.";
        String expected = "This is a content without tags.";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }

    @Test
    void processTags_handlesEmptyContent() {
        String content = "";
        String expected = "";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }

    @Test
    void processTags_handlesContentWithSpecialCharacters() {
        String content = "This is a #tag with special characters like @, $, and %.";
        String expected = "This is a <a href=\"tag://tag\" onclick=\"alert('tag://tag')\">#tag</a> with special characters like @, $, and %.";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }

    @Test
    void processTags_handlesContentWithAdjacentTags() {
        String content = "This is a #tag1#tag2.";
        String expected = "This is a <a href=\"tag://tag1\" onclick=\"alert('tag://tag1')\">#tag1</a><a href=\"tag://tag2\" onclick=\"alert('tag://tag2')\">#tag2</a>.";
        String result = ContentProcessor.processTags(content);
        assertEquals(expected, result);
    }
}