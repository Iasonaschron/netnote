package server.service;

import commons.Collection;
import commons.CollectionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

@Service
public class CollectionConfigService {

    private static final String CONFIG_FILE_PATH = "server/src/main/resources/collections_config.json";
    private final ObjectMapper objectMapper;
    private CollectionConfig collectionConfig;

    public CollectionConfigService() {
        this.objectMapper = new ObjectMapper();
    }

    public CollectionConfig readConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            String jsonString = FileUtils.readFileToString(configFile, "UTF-8");
            return objectMapper.readValue(jsonString, CollectionConfig.class);
        }
        return new CollectionConfig();
    }

    public void writeConfig(CollectionConfig config) throws IOException {
        String jsonString = objectMapper.writeValueAsString(config);
        FileUtils.writeStringToFile(new File(CONFIG_FILE_PATH), jsonString, "UTF-8");
    }

    public void addCollectionToConfig(Collection newCollection) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }
        collectionConfig.addCollection(newCollection);
        writeConfig(collectionConfig);
    }

    public void updateCollectionInConfig(Collection updatedCollection) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }
        collectionConfig.updateCollection(updatedCollection);
        writeConfig(collectionConfig);
    }

    public void removeCollectionFromConfig(Long collectionId) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }
        collectionConfig.removeCollection(collectionId);
        writeConfig(collectionConfig);
    }

    @PostConstruct
    public void loadCollections() {
        try {
            collectionConfig = readConfig();  // Load collections from the config file at startup
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void saveCollections() {
        try {
            if (collectionConfig != null) {
                writeConfig(collectionConfig);  // Save collections to the config file at shutdown
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
