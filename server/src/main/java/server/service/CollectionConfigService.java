package server.service;

import commons.Collection;
import commons.CollectionConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class CollectionConfigService {

    private static final String CONFIG_FILE_PATH = "collections_config.json";
    private final ObjectMapper objectMapper;

    public CollectionConfigService() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Reads the collections from the config file and deserializes it into a Config object.
     *
     * @return The Config object containing the collections.
     * @throws IOException If an error occurs while reading the file.
     */
    public CollectionConfig readConfig() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) {
            String jsonString = FileUtils.readFileToString(configFile, "UTF-8");
            return objectMapper.readValue(jsonString, CollectionConfig.class);
        }
        return new CollectionConfig(); // Return an empty config if no file exists
    }

    /**
     * Writes the collections to the config file by serializing the Config object to JSON.
     *
     * @param config The Config object containing the collections.
     * @throws IOException If an error occurs while writing the file.
     */
    public void writeConfig(CollectionConfig config) throws IOException {
        String jsonString = objectMapper.writeValueAsString(config);
        FileUtils.writeStringToFile(new File(CONFIG_FILE_PATH), jsonString, "UTF-8");
    }

    /**
     * Adds a collection to the config and writes the updated config to the file.
     *
     * @param newCollection The collection to add.
     * @throws IOException If an error occurs while reading/writing the file.
     */
    public void addCollectionToConfig(Collection newCollection) throws IOException {
        CollectionConfig config = readConfig();
        List<Collection> collections = config.getCollections();
        collections.add(newCollection);
        writeConfig(config);
    }
}
