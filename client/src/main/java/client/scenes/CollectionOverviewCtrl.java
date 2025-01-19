package client.scenes;

import commons.Collection;

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
     * Constructor for the CollectionOverviewCtrl class.
     *
     * @param server The server utility instance used for server communication.
     */
    @Inject
    public CollectionOverviewCtrl(ServerUtils server) {
        this.server = server;
        this.collectionConfigService = new CollectionConfigService();
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded. It can be used to perform any
     * necessary setup or initialization tasks.
     *
     * @param location  The location used to resolve relative paths for the root
     *                  object, or null if the location is not known.
     * @param resources The resources used to localize the root object, or null if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // refresh();
    }

    /**
     * Initiates the process of adding a new collection.
     * Sets the editing mode to true and clears the input fields
     * for collection title and collection name.
     */
    public void addCollection() {
        isEditing = true;
        collectionTitleField.clear();
        collectionNameField.clear();
    }

    /**
     * Sets the editing state to true.
     * This method is used to indicate that the collection is currently being
     * edited.
     */
    public void edit() {
        isEditing = true;
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
     * configuration service. If the view is currently in editing mode, the refresh
     * operation is skipped.
     * 
     * This method attempts to read the collection configuration and update the
     * observable list of collections. If an exception occurs during this process,
     * the stack trace is printed.
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
     * Handles the event when the selected collection changes.
     * If the current collection is null, the method returns immediately.
     * Otherwise, it sets the editing state to false and updates the collection
     * fields.
     */
    public void selectedCollectionChange() {
        Collection collection = getCurrentCollection();
        if (collection == null) {
            return;
        }

        isEditing = false;

        setCollectionFields(collection);
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

        isEditing = false;

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

    /**
     * Deletes the current collection.
     *
     * This method retrieves the current collection and attempts to remove it from
     * the configuration. If the collection is null, it returns false. If an
     * exception occurs during the removal process, it prints the stack trace and
     * returns false.
     *
     * @return true if the collection was successfully deleted, false otherwise.
     */
    public boolean deleteCollection() {
        Collection collection = getCurrentCollection();
        if (collection == null) {
            return false;
        }

        isEditing = false;

        try {
            collectionConfigService.removeCollectionFromConfig(collection.getTitle());
            collectionConfigService.saveCollections();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}