package commons;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentProcessor {
    private static final Pattern TAG_PATTERN = Pattern.compile("#\\w+");

    /**
     * Processes the content to replace tags with clickable HTML links.
     *
     * @param content The raw note content containing tags
     * @return Processed content with tags replaced by clickable links
     */
    public static String processTags(String content) {
        StringBuilder processedContent = new StringBuilder();
        Matcher matcher = TAG_PATTERN.matcher(content);

        int lastIndex = 0;

        while (matcher.find()) {
            processedContent.append(content, lastIndex, matcher.start());
            String tag = matcher.group();
            String tagTitle = tag.substring(1); // Exclude the '#' from the tag name

            // Create the properly formatted <a> element
            processedContent.append("<a href=\"tag://")
                    .append(tagTitle)
                    .append("\" onclick=\"alert('tag://")
                    .append(tagTitle)
                    .append("')\">")
                    .append(tag)
                    .append("</a>");
            lastIndex = matcher.end();
        }

        processedContent.append(content.substring(lastIndex));
        return processedContent.toString();
    }

}
