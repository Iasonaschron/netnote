package client.scenes;

import client.utils.LanguageManager;
import commons.AlertMethods;
import commons.Collection;

import java.io.IOException;
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
import org.springframework.stereotype.Component;

@Component
public class CollectionOverviewCtrl implements Initializable {
    private MainNotesCtrl mainNotesCtrl;

    private NoteOverviewCtrl noteOverviewCtrl;
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
     * @param server The server utility instance used for server communication.
     * @param noteOverviewCtrl The noteOverviewCtrl instance used for communication.
     */
    @Inject
    public CollectionOverviewCtrl(ServerUtils server,NoteOverviewCtrl noteOverviewCtrl) {
        this.server = server;
        this.collectionConfigService = new CollectionConfigService();
        this.noteOverviewCtrl = noteOverviewCtrl;
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

        collectionList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedCollectionChange();});
    }

    /**
     * Initiates the process of adding a new collection.
     * Sets the editing mode to true and clears the input fields
     * for collection title and collection name.
     */
    public void addCollection() {

        collectionList.getSelectionModel().clearSelection();

        collectionTitleField.clear();
        collectionServerField.clear();
        collectionNameField.clear();

        isEditing = true;
    }

    /**
     * Checks the inputted fields to see if they fit the requirements of a collection.
     * The collection must have a unique title, server and name.
     * The server must start with https:// or http://
     * @return  boolean of whether the collection is valid or not.
     * @throws IOException
     */

    public boolean checkInput() throws IOException {
        isEditing = false;


        if( collectionTitleField.getText() == null || collectionTitleField.getText().isBlank() ){
            AlertMethods.createWarning(LanguageManager.getString("collection_title_empty"));

            return false;
        }

        String title = collectionTitleField.getText();
        if(collectionConfigService.getCollections().stream().anyMatch(collection -> collection.getTitle().equals(title)
                && !collection.equals(getCurrentCollection()))){
            AlertMethods.createWarning(LanguageManager.getString("collection_title_exists"));

            return false;
        }


        if( collectionNameField.getText() == null || collectionNameField.getText().isBlank() ){
            AlertMethods.createWarning(LanguageManager.getString("collection_name_empty"));

            return false;
        }

        String name = collectionNameField.getText();
        if(collectionConfigService.getCollections().stream().anyMatch(collection -> collection.getName().equals(name)
                && !collection.equals(getCurrentCollection()))){
            AlertMethods.createWarning(LanguageManager.getString("collection_name_exists"));

            return false;
        }

        String server = collectionServerField.getText();
        if( server == null || server.isBlank()){
            AlertMethods.createWarning(LanguageManager.getString("invalid_server"));
            return false;
        }

        if (collectionConfigService.getCollections().stream()
                .anyMatch(collection -> normalizeServer(collection.getServer()).equals(normalizeServer(server)))) {
            AlertMethods.createWarning(LanguageManager.getString("duplicate_server"));
            return false;
        }

        return true;

    }

    private String normalizeServer(String server) {
        return server.endsWith("/") ? server.substring(0, server.length() - 1) : server;
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
        setCollectionFields(collection);

        isEditing = true;
    }

    /**
     * Sets the fields of the collection with the provided collection data.
     *
     * @param collection the collection object containing the data to be set
     */
    public void setCollectionFields(Collection collection) {

        collectionTitleField.setText(collection.getTitle());
        collectionNameField.setText(collection.getName());
        collectionServerField.setText(collection.getServer());
    }

    /**
     * Saves the current collection by adding it to the configuration and saving the
     * collections.
     *
     * @return true if the collection was successfully saved, false otherwise
     */
    public boolean saveCollection() throws IOException {
        if (!checkInput()) {
            //AlertMethods.createWarning("The provided Collection is not valid");
            return false; // TODO Maybe show an error message
        }

        isEditing = false;

        Collection current = getCurrentCollection();
        if (current == null) {
            Collection collection = new Collection();
            collection.setTitle(collectionTitleField.getText());
            collection.setServer(collectionServerField.getText());
            collection.setName(collectionNameField.getText());

            try {
                collectionConfigService.addCollectionToConfig(collection);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            String oldTitle = current.getTitle();
            current.setTitle(collectionTitleField.getText());
            current.setServer(collectionServerField.getText());
            current.setName(collectionNameField.getText());

            try {
                collectionConfigService.updateCollectionInConfig(oldTitle, current);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            collectionConfigService.saveCollections();
            noteOverviewCtrl.updateCollectionMenu();
            refresh();
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
                    existingCollection.getName().equals(collection.getName()) || existingCollection.getServer().equals(collection.getServer())) {
                return false;
            }
        }

        return collection != null && collection.getTitle() != null && collection.getName() != null && collection.getServer() != null;

    }

    /**
     * Deletes the current collection.
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
            noteOverviewCtrl.updateCollectionMenu();
            refresh();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


}