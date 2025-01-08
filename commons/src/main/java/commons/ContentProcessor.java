package commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor {
    private static final Pattern TAG_PATTERN = Pattern.compile("#\\w+");

    /**
     * Processes the content to replace tags with **tag** for Markdown rendering.
     *
     * @param content The raw note content containing tags
     * @return Processed content with tags replaced
     */
    public static String processTags(String content) {
        StringBuilder processedContent = new StringBuilder();
        Matcher matcher = TAG_PATTERN.matcher(content);

        int lastIndex = 0;

        while (matcher.find()) {
            processedContent.append(content, lastIndex, matcher.start());
            String tag = matcher.group();
            processedContent.append("**").append(tag).append("**");
            lastIndex = matcher.end();
        }

        processedContent.append(content.substring(lastIndex));

        return processedContent.toString();
    }
}
