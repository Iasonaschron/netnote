package client.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static Locale currentLocale = Locale.ENGLISH;

    public static void loadLocale(String languageCode) {
        currentLocale = Locale.of(languageCode);
    }

    public static ResourceBundle getBundle() {
        return ResourceBundle.getBundle("client.localization.labels", currentLocale);
    }
}
