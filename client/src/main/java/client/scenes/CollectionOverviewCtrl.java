package client.scenes;

import commons.Collection;
import commons.CollectionConfig;

import java.net.URL;
import java.util.ResourceBundle;

import client.service.CollectionConfigService;
import client.utils.ServerUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.*;
import javafx.scene.control.*;
import com.google.inject.Inject;

public class CollectionOverviewCtrl implements Initializable {
    private MainNotesCtrl mainNotesCtrl;

    @FXML
    private Button addCollectionButton;

    @FXML
    private Button deleteCollectionButton;

    @FXML
    private Button saveButton;

    @FXML
    private TextField collectionNameField;

    @FXML
    private TextField collectionServerField;

    @FXML
    private TextField collectionTitleField;

    @FXML
    private Text collectionStatus;

    @FXML
    private ListView<Collection> collectionList;
    private ObservableList<Collection> collections;

    private boolean isEditing = false;

    private final ServerUtils server;
    private final CollectionConfigService collectionConfigService;

    /**
     * Controller for the Collection Overview scene.
     * This class handles the interactions between the server and the collection
     * overview UI.
     *
     * @param server The server utility instance used for server communication.
     */
    @Inject
    public CollectionOverviewCtrl(ServerUtils server) {
        this.server = server;
        this.collectionConfigService = new CollectionConfigService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        refresh();
    }

    /**
     * Retrieves the currently selected collection from the collection list.
     *
     * @return the currently selected Collection if there is a selection,
     *         or null if no collection is selected.
     */
    public Collection getCurrentCollection() {
        return collectionList.getSelectionModel().getSelectedItems().isEmpty() ? null
                : collectionList.getSelectionModel().getSelectedItems().getFirst();
    }

    /**
     * Sets the MainNotesCtrl instance for this controller.
     *
     * @param mainNotesCtrl the MainNotesCtrl instance to be set
     */
    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        this.mainNotesCtrl = mainNotesCtrl;
    }

    /**
     * Refreshes the collection overview by reloading the collections from the
     * configuration service.
     * If the view is currently in editing mode, the refresh operation is skipped.
     * 
     * This method attempts to read the collection configuration and update the
     * observable list
     * of collections. If an exception occurs during this process, the stack trace
     * is printed.
     */
    public void refresh() {
        if (isEditing) {
            return;
        }

        try {
            collections = FXCollections
                    .observableList(collectionConfigService.readConfig().getCollections());
        } catch (Exception e) {
            e.printStackTrace();
        }

        collectionList.setItems(collections);
    }

    /**
     * Retrieves a new Collection object based on the current input fields.
     *
     * @return a new Collection object with the title and name from the input
     *         fields.
     */
    public Collection getCollection() {
        return new Collection(collectionTitleField.getText(), collectionNameField.getText());
    }

    /**
     * Retrieves the server URL from the collection server field.
     *
     * @return the server URL as a String
     */
    public String getServerURL() {
        return collectionServerField.getText();
    }

    /**
     * Sets the fields of the collection with the provided collection data.
     *
     * @param collection the collection object containing the data to be set
     */
    public void setCollectionFields(Collection collection) {
        collectionTitleField.setText(collection.getTitle());
        collectionNameField.setText(collection.getName());
    }

    /**
     * Saves the current collection by adding it to the configuration and saving the
     * collections.
     *
     * @return true if the collection was successfully saved, false otherwise
     */
    public boolean saveCollection() {
        Collection collection = getCollection();
        if (!isValidCollection(collection)) {
            return false; // TODO Maybe show an error message
        }

        try {
            collectionConfigService.addCollectionToConfig(collection);
            collectionConfigService.saveCollections();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if the given collection is valid.
     * A collection is considered valid if:
     * - It is not null.
     * - Its title is not null.
     * - Its name is not null.
     * - Its title does not match the title of any existing collection.
     * - Its name does not match the name of any existing collection.
     *
     * @param collection the collection to be validated
     * @return true if the collection is valid, false otherwise
     */
    public boolean isValidCollection(Collection collection) {
        for (Collection existingCollection : collections) {
            if (existingCollection.getTitle().equals(collection.getTitle()) ||
                    existingCollection.getName().equals(collection.getName())) {
                return false;
            }
        }

        return collection != null && collection.getTitle() != null && collection.getName() != null;

    }
}