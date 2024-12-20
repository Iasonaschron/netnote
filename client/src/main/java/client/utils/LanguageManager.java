package client.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = Locale.ENGLISH;

    /**
     * Changes the locale of the language manager with the given language code
     *
     * @param languageCode The language code of the language (e.g. "ro", "en", "nl")
     */
    public static void loadLocale(String languageCode) {
        currentLocale = Locale.of(languageCode);
    }

    /**
     * Returns the bundle based on the current locale
     *
     * @return A matching resource bundle based on the current locale
     */
    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("client.localization.labels", currentLocale);
    }
}
