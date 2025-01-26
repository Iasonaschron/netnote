package client.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {
    private static final String LANGUAGE_KEY = "user_language";
    private static final String DEFAULT_LANGUAGE = "en";
    private static final Preferences preferences = Preferences.userNodeForPackage(LanguageManager.class);
    private static Locale currentLocale;

    // This code is executed when the class is first loaded into memory
    static {
        String savedLanguage = preferences.get(LANGUAGE_KEY, DEFAULT_LANGUAGE);
        currentLocale = Locale.of(savedLanguage);
    }

    /**
     * Changes the locale of the language manager with the given language code
     * and updates the currently stored config
     *
     * @param languageCode The language code of the language (e.g. "ro", "en", "nl")
     */
    public static void loadLocale(String languageCode) {
        currentLocale = Locale.of(languageCode);
        preferences.put(LANGUAGE_KEY, languageCode);
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

    /**
     * Returns the current language code (e.g. "en", "fr", "ro")
     *
     * @return The current language code
     */
    public static String getCurrentLanguageCode() {
        return currentLocale.getLanguage();
    }

    /**
     * Sets the preferences for the language manager
     * @param preferences The preferences to set
     */
    public static void setPreferences(Preferences preferences) {
        preferences = preferences;
    }
}
