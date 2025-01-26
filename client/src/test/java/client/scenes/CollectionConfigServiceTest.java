package client.scenes;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import client.service.CollectionConfigService;
import commons.Collection;
import commons.CollectionConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CollectionConfigServiceTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FileUtils fileUtils;

    @InjectMocks
    private CollectionConfigService collectionConfigService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void readConfig_returnsEmptyConfigIfFileNotExists() throws IOException {
        File configFile = mock(File.class);
        when(configFile.exists()).thenReturn(false);

        CollectionConfig config = collectionConfigService.readConfig();

        assertNotNull(config);
        assertTrue(config.getCollections().isEmpty());
    }

    @Test
    void getCollectionByTitle_returnsNullIfNotExists() {
        CollectionConfig config = new CollectionConfig();
        collectionConfigService.loadCollections();

        Collection result = collectionConfigService.getCollectionByTitle("Nonexistent Title");

        assertNull(result);
    }
}