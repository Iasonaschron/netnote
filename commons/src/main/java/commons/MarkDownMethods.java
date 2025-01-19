package commons;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.commonmark.renderer.markdown.MarkdownRenderer;

public class MarkDownMethods {
    private final static Parser parser = Parser.builder().build();
    private final static HtmlRenderer Htmlrenderer = HtmlRenderer.builder().build();
    private final static MarkdownRenderer MarkRenderer = MarkdownRenderer.builder().build();
    private static final String SERVER = "http://localhost:8080/";

    /**
     * @param raw The String to parse into HTML
     * @param noteid the id of the note request a string to be parsed
     * @return Returns the parsed HTML
     */
    public static String renderRawTextToText(String raw, long noteid) {
        String[] a1 = raw.split("(\\[\\[embedded]]\\()|(\\))");
        String convertedString = "";
        for(int i = 0; i < a1.length; i += 2){
            convertedString += a1[i];
            if(i < a1.length-1){
                convertedString += "![" + a1[i+1] + "](http://localhost:8080/api/files/" + noteid + "/" + a1[i+1] + ")";
            }
        }

        String tagProcessing = ContentProcessor.processTags(convertedString);

        Node document = parser.parse(tagProcessing);
        String s1 = Htmlrenderer.render(document);
        return s1;
    }

    /**
     * @param document The node containing the HTML data to turn into raw Text
     * @return The raw Text the Node Translates to
     */
    @SuppressWarnings("unused")
    public static String renderProcessedNodeToText(Node document) {
        return MarkRenderer.render(document);
    }

    /**
     * @param raw The raw String to turn into a Node
     * @return The Node the String Parses to
     */
    @SuppressWarnings("unused")
    public static Node renderRawTextToNode(String raw) {
        return parser.parse(raw);
    }

}
