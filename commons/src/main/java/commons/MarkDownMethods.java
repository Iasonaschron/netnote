package commons;

import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class MarkDownMethods {
    private final static Parser parser = Parser.builder().build();
    private final static HtmlRenderer Htmlrenderer = HtmlRenderer.builder().build();
    private final static MarkdownRenderer MarkRenderer = MarkdownRenderer.builder().build();

    public static String renderRaw_TextToText(String raw){
        Node document = parser.parse(raw);
        return Htmlrenderer.render(document);
    }

    @SuppressWarnings("unused")
    public static String renderProcessed_NodeToText(Node document){
        return MarkRenderer.render(document);
    }

    @SuppressWarnings("unused")
    public static Node renderRaw_TextToNode(String raw){
        return parser.parse(raw);
    }


}
