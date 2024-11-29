package commons;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MarkDownTest {
    private String rawText;
    private Node document;
    private String processedText;

    @BeforeEach
    public void setUp(){
        Parser parser = Parser.builder().build();
        rawText = "This is *Markdown*";
        processedText = "<p>This is <em>Markdown</em></p>\n";
        document = parser.parse(rawText);
    }

    @Test
    public void rawRender_TextToText(){
        assertEquals(processedText, MarkDownMethods.renderRaw_TextToText(rawText));
    }

    @Test
    public void rawRender_TextToNodeAndBack(){
        assertEquals(rawText + "\n",
                MarkDownMethods.renderProcessed_NodeToText(MarkDownMethods.renderRaw_TextToNode(rawText)));
    }

    @Test
    public void ProcessedRender_NodeToText(){
        assertEquals(rawText + "\n"
                , MarkDownMethods.renderProcessed_NodeToText(document));
    }
}
