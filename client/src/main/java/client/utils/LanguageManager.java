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

    /**
     * Returns a string from the current bundle based on the given key
     *
     * @param key The key of the string
     * @return The string from the resource bundle
     */
    public static String getString(String key) {
        return getBundle().getString(key);
    }
}
