package commons;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class MarkDownMethods {
    private final static Parser parser = Parser.builder().build();
    private final static HtmlRenderer Htmlrenderer = HtmlRenderer.builder().build();
    private final static MarkdownRenderer MarkRenderer = MarkdownRenderer.builder().build();

    /**
     * @param raw The String to parse into HTML
     * @return Returns the parsed HTML
     */
    public static String renderRaw_TextToText(String raw){
        Node document = parser.parse(raw);
        return Htmlrenderer.render(document);
    }

    /**
     *
     * @param document The node containing the HTML data to turn into raw Text
     * @return The raw Text the Node Translates to
     */
    @SuppressWarnings("unused")
    public static String renderProcessed_NodeToText(Node document){
        return MarkRenderer.render(document);
    }

    /**
     *
     * @param raw The raw String to turn into a Node
     * @return The Node the String Parses to
     */
    @SuppressWarnings("unused")
    public static Node renderRaw_TextToNode(String raw){
        return parser.parse(raw);
    }


}
