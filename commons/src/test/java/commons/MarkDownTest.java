package commons;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkDownTest {
    /**
     * Tests the renderRawTextToText method from MarkDownMethods class.
     * This test verifies that the raw markdown text is correctly processed
     * and rendered as plain text.
     */
    @Test
    public void rawRenderTextToText() {
        String rawText = "This is *Markdown* [[embedded]](AAAAAAAAAAAAA.png)";
        String processedText = "<p>This is <em>Markdown</em></p>\n";
        assertEquals(processedText, MarkDownMethods.renderRawTextToText(rawText, 0));
    }

    /**
     * Tests the conversion of raw markdown text to a node and back to text.
     * This method ensures that the text remains consistent after being processed
     * by the MarkDownMethods' renderRawTextToNode and renderProcessedNodeToText
     * methods.
     * 
     * The test asserts that the raw text, when converted to a node and then back to
     * text,
     * matches the original raw text with an added newline character.
     */
    @Test
    public void rawRenderTextToNodeAndBack() {
        String rawText = "This is *Markdown*";
        assertEquals(rawText + "\n",
                MarkDownMethods.renderProcessedNodeToText(MarkDownMethods.renderRawTextToNode(rawText)));
    }

    /**
     * Tests the renderProcessedNodeToText method from MarkDownMethods class.
     * This method verifies that the processed node is correctly rendered to text.
     * It asserts that the output of renderProcessedNodeToText is equal to the
     * expected raw text followed by a newline character.
     */
    @Test
    public void ProcessedRenderNodeToText() {
        String rawText = "This is *Markdown*";
        Parser parser = Parser.builder().build();
        Node document = parser.parse(rawText);
        assertEquals(rawText + "\n", MarkDownMethods.renderProcessedNodeToText(document));
    }
}
