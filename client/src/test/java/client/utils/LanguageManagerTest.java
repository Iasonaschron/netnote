package client.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LanguageManagerTest {

    private Preferences preferences;

    @BeforeEach
    void setUp() {
        preferences = mock(Preferences.class);
        when(preferences.get(anyString(), anyString())).thenReturn("en");
        LanguageManager.setPreferences(preferences);
        LanguageManager.loadLocale("en");
    }

    @Test
    void loadLocale_updatesCurrentLocale() {
        LanguageManager.loadLocale("fr");
        assertEquals("fr", LanguageManager.getCurrentLanguageCode());
    }

    @Test
    void getBundle_returnsCorrectResourceBundle() {
        ResourceBundle bundle = LanguageManager.getBundle();
        assertNotNull(bundle);
        assertEquals("client.localization.labels", bundle.getBaseBundleName());
    }

    @Test
    void getCurrentLanguageCode_returnsCorrectLanguageCode() {
        LanguageManager.loadLocale("ro");
        assertEquals("ro", LanguageManager.getCurrentLanguageCode());
    }
}