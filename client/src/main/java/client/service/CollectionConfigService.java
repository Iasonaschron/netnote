package client.service;

import commons.Collection;
import commons.CollectionConfig;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Service for managing and persisting the collection configuration.
 * Handles reading, writing, and updating collections stored in a JSON file.
 */
@Service
public class CollectionConfigService {

    private static final String CONFIG_FILE_PATH = "client/src/main/resources/client/collections/collections_config.json";
    private static final String DEFAULT_COLLECTION_TITLE = "Default Collection";
    private final ObjectMapper objectMapper;
    private CollectionConfig collectionConfig;

    /**
     * constructor for object mapper
     */
    public CollectionConfigService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Reads the collection configuration from the JSON file.
     *
     * @return A CollectionConfig object containing the configuration data.
     * @throws IOException if an error occurs during file reading or JSON parsing.
     */
    public CollectionConfig readConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            String jsonString = FileUtils.readFileToString(configFile, "UTF-8");
            return objectMapper.readValue(jsonString, CollectionConfig.class);
        }
        return new CollectionConfig(); // Return empty config if no file exists
    }

    /**
     * Writes the given collection configuration to the JSON file.
     *
     * @param config The CollectionConfig object to write to the file.
     * @throws IOException if an error occurs during file writing or JSON serialization.
     */
    public void writeConfig(CollectionConfig config) throws IOException {
        String jsonString = objectMapper.writeValueAsString(config);
        FileUtils.writeStringToFile(new File(CONFIG_FILE_PATH), jsonString, "UTF-8");
    }

    /**
     * Adds a new collection to the configuration and saves it to the JSON file.
     *
     * @param newCollection The Collection object to add.
     * @throws IOException if an error occurs during file operations.
     */
    public void addCollectionToConfig(Collection newCollection) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }
        collectionConfig.addCollection(newCollection);
        writeConfig(collectionConfig);
    }

    /**
     * Updates an existing collection in the configuration and saves it to the JSON file.
     *
     * @param oldTitle The title of the collection to be replaced.
     * @param updatedCollection The Collection object with updated data.
     * @throws IOException if an error occurs during file operations.
     */
    public void updateCollectionInConfig(String oldTitle, Collection updatedCollection) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }

        collectionConfig.updateCollection(oldTitle, updatedCollection);
        writeConfig(collectionConfig);
    }


    /**
     * Removes a collection from the configuration by its ID and saves the updated configuration.
     *
     * @param collectionId The ID of the collection to remove.
     * @throws IOException if an error occurs during file operations.
     */
    public void removeCollectionFromConfig(String collectionId) throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }
        collectionConfig.removeCollection(collectionId);
        writeConfig(collectionConfig);
    }

    /**
     * Retrieves the default collection. If it does not exist, a new default collection is created.
     *
     * @return The default Collection object.
     * @throws IOException if an error occurs during file operations.
     */
    public Collection getOrCreateDefaultCollection() throws IOException {
        if (collectionConfig == null) {
            collectionConfig = readConfig();
        }

        // Try to find the default collection
        return collectionConfig.getCollections().stream()
                .filter(collection -> DEFAULT_COLLECTION_TITLE.equals(collection.getTitle()))
                .findFirst()
                .orElseGet(this::createDefaultCollection); // Create it if not found
    }

    public Collection getCollectionByTitle(String title) {
        if (collectionConfig == null) {
            return null;
        }
        return collectionConfig.getCollections().stream()
                .filter(collection -> collection.getTitle().equals(title))
                .findFirst().orElse(null);
    }

    /**
     * Creates a new default collection, adds it to the configuration, and saves it.
     *
     * @return The newly created default Collection.
     */
    private Collection createDefaultCollection() {
        Collection defaultCollection = new Collection(DEFAULT_COLLECTION_TITLE);
        try {
            addCollectionToConfig(defaultCollection);  // Add to the config
        } catch (IOException e) {
            e.printStackTrace(); // Handle or log error
        }
        return defaultCollection;
    }

    /**
     * Initializes the collection configuration by loading it from the JSON file.
     * This method is called automatically after the service is instantiated.
     */
    @PostConstruct
    public void loadCollections() {
        try {
            collectionConfig = readConfig();  // Load collections from the config file at startup
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current collection configuration to the JSON file.
     * This method is called automatically before the service is destroyed.
     */
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
