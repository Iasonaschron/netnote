package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import client.utils.StompClient;
import client.utils.UpdateListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import commons.AlertMethods;
import commons.FileData;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import commons.Collection;
import client.service.CollectionConfigService;
import javafx.util.Duration;
import org.controlsfx.control.CheckComboBox;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Note Overview scene.
 * Manages a list of notes and provides functionality for creating, viewing, and
 * editing notes.
 */
public class NoteOverviewCtrl implements Initializable, UpdateListener {

    private MainNotesCtrl mainNotes;

    @FXML
    private WebView webview;

    private WebEngine webEngine;

    @FXML
    private ListView<Note> listView;

    @FXML
    private TextField title;

    @FXML
    private TextArea content;

    @FXML
    private TextField searchBox;

    @FXML
    private Button done;

    @FXML
    private Button delete;

    @FXML
    private Button add;

    @FXML
    private CheckComboBox<String> tagsMenu;

    @FXML
    private ImageView languageIndicator;

    @FXML
    private ComboBox<String> languageSelectorCombo;

    @FXML
    private CheckBox searchByContentCheckBox;

    @FXML
    private ListView<FileData> fileDataListView;

    @FXML
    private Button clear;

    @FXML
    private Button collectionMenuButton;

    @FXML
    private Button information;

    @FXML
    private Button deleteAllFilesButton;

    @FXML
    private Button fileSelectButton;

    @FXML
    private Button deleteFilesButton;

    private List<Note> data;
    private ObservableList<Note> visibleNotes;
    private ObservableList<Note> tagNotes;

    private ObservableList<FileData> noteFiles;

    private final ServerUtils server;

    private Note lastSelectedNote = null;

    private ScheduledExecutorService scheduler;
    private boolean isEditing = false;
    private boolean hasSelectedTag = false;

    private boolean isSaveAction = false;
    private boolean justEdited = false;

    private List<String> tags = new ArrayList<>();

    private CollectionConfigService collectionConfigService;
    private Collection selectedCollection; // collection used for filtering

    private StompClient stompClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Constructor for the NoteOverviewCtrl.
     *
     * @param server the server utility to interact with backend services
     */
    @Inject
    public NoteOverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    /**
     * Retrieves the current collection based on the selected note's collection ID,
     * or the default collection if no note is selected.
     *
     * @return The current or default collection.
     * @throws RuntimeException if an error occurs while getting or creating the
     *                          default collection.
     */
    public Collection getCurrentCollection() {
        if (getSelectedNote() == null) {
            try {
                return collectionConfigService.getOrCreateDefaultCollection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return collectionConfigService.getCollectionByTitle(getSelectedNote().getCollectionTitle());
    }

    /**
     * Deletes all files related with the current selected note
     */
    public void deleteFilesNoteID() {
        try {
            server.deleteFile(lastSelectedNote.getId(), null);
            noteFiles.setAll(server.fetchFileNames(lastSelectedNote.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens a file explorer window for the user to select a file, and then uploads
     * that file to the server
     */
    public void selectAndUploadFile() {
        try {
            FileChooser fc = new FileChooser();
            Stage stage = new Stage();
            fc.setTitle("Select a file");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Files", "*.*"),
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
            File selectedFile = fc.showOpenDialog(stage);
            if (selectedFile != null) {
                server.uploadFile(selectedFile, lastSelectedNote.getId());
                System.out.println("File uploaded");
            } else {
                System.out.println("no file selected");
            }
            noteFiles.setAll(server.fetchFileNames(lastSelectedNote.getId()));
        } catch (Exception e) {
            System.out.println("Error uploading file");
            e.printStackTrace();
        }
    }

    /**
     * Deletes all files in the server
     */
    public void deleteAllFiles() {
        try {
            server.deleteAllFiles();
            noteFiles.setAll(server.fetchFileNames(lastSelectedNote.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the dataCollection list according to the currently selected
     * collection
     */
    private List<Note> getNotesBySelectedCollection() {
        return data.stream().filter(note -> Objects.equals(note.getCollectionTitle(), getCurrentCollection().getTitle()))
                .toList();
    }

    /**
     * Refreshes the list of notes by fetching the latest data from the server.
     * Updates the ListView with the retrieved notes.
     */
    public void refresh() {
        if (isEditing) {
            return;
        }

        data = server.getNotes(selectedCollection.getServer());

        if (hasSelectedTag) {
            tagUpdateList();
            return;
        }

        updateList();
    }

    /**
     * Updates the list of notes based on the current filter string.
     * This method is called when the filter string changes or the data is
     * refreshed.
     * It updates the ListView with the filtered notes.
     */
    public void updateList() {
        visibleNotes = FXCollections.observableList(getVisibleNotes(searchBox.getText()));
        tagsMenu.getCheckModel().clearChecks();
        hasSelectedTag = false;
        filterTagList();
        listView.setItems(visibleNotes);

        if (lastSelectedNote == null || !visibleNotes.contains(lastSelectedNote))
            clearFields();
    }

    /**
     * Updates the list of notes based on the current tag selected and the current
     * filter string.
     * This method is called instead of updateList() only when the user has selected
     * a tag.
     * It updates the ListView with the filtered notes.
     *
     */
    public void tagUpdateList() {
        visibleNotes = FXCollections.observableList(getVisibleNotes(searchBox.getText()));
        tagNotes = FXCollections.observableList(filterNotesByTag(tagsMenu.getCheckModel().getCheckedItems()));
        if (!tagsMenu.isFocused()) {
            filterTagList();
        }
        listView.setItems(tagNotes);

        if (lastSelectedNote == null || !tagNotes.contains(lastSelectedNote))
            clearFields();

        if (tagNotes.isEmpty()) {
            createNote();
        }

    }

    /**
     * Updates the dropdown menu of available tags to filter by
     */
    public void filterTagList() {
        List<Integer> indexes = tagsMenu.getCheckModel().getCheckedIndices();

        visibleNotes = FXCollections.observableList(getVisibleNotes(searchBox.getText()));

        tags = visibleNotes.stream().flatMap(note -> note.getTags().stream()).distinct().toList();
        tagsMenu.getItems().setAll(tags);


        for (Integer i: indexes) {
            tagsMenu.getCheckModel().check(i);
        }

    }

    /**
     * Updates the WebView with the currently selected note's content.
     * This method is called when the selected note changes.
     */
    public void updateWebView() {
        String htmlContent = "<!DOCTYPE html><html><head><link rel=\"stylesheet\" type=\"text/css\" href=\"notes.css\"></head><body>"
                + updateNoteLinksHTML(getNote().getHTML()) + "</body></html>";
        webEngine.loadContent(htmlContent);
    }

    /**
     * Filters the list of notes based on the given filter string.
     * If the filter is empty, all notes are returned.
     * Otherwise, only notes with titles or content containing the filter string are
     * returned.
     *
     * @param filter the filter string to apply to the list of notes
     * @return a list of notes that match the filter string
     */
    public List<Note> getVisibleNotes(String filter) {
        if (filter.isBlank()) {
            return data;
        } else {
            if (searchByContentCheckBox.isSelected()) {
                // Filter based on content
                final String contentFilter = filter.substring(1);
                return data.stream()
                        .filter(note -> note.getContent().toLowerCase().contains(contentFilter.toLowerCase()))
                        .toList();
            } else {
                // Filter based on title
                return data.stream()
                        .filter(note -> (note.getTitle().toLowerCase().contains(filter.toLowerCase())))
                        .toList();
            }
        }
    }

    /**
     * Prepares the scene for creating a new note by clearing all input fields
     * and making the "Done" button visible.
     */
    public void createNote() {
        clearFields();
        tagsMenu.getCheckModel().clearChecks();
        hasSelectedTag = false;
        clear.disableProperty().set(true);

        listView.getSelectionModel().clearSelection();
        add.disableProperty().set(true);
        delete.disableProperty().set(false);
        done.disableProperty().set(false);
        done.setOnAction(_ -> create());
        isSaveAction = false;
        isEditing = true;
        lastSelectedNote = null;
        listView.setItems(visibleNotes);
        title.requestFocus();
    }

    /**
     * Clears the input fields for the title and content of a note.
     */
    private void clearFields() {
        title.clear();
        content.clear();
        webEngine.loadContent("");
    }

    /**
     * Clears the selected tag from the menu
     */
    public void clearTags() {
        tagsMenu.getCheckModel().clearChecks();
        clearFields();
        hasSelectedTag = false;
        clear.disableProperty().set(true);
        isEditing = false;
        refresh();
    }

    /**
     * Initializes the ListView and sets up a cell factory to display note titles.
     * Also attaches a listener to update the input fields when a note is selected.
     *
     * @param location  the location used to resolve relative paths for the root
     *                  object, or null if none
     * @param resources the resources used to localize the root object, or null if
     *                  none
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        collectionConfigService = new CollectionConfigService();
        try {
            selectedCollection = collectionConfigService.getOrCreateDefaultCollection();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        webInitialisation();

        fileInitialisation();

        noteFiles = FXCollections.observableArrayList(new ArrayList<FileData>());
        fileDataListView.setItems(noteFiles);

        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(Note item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getTitle());
                }
            }
        });

        delete.setOnAction(_ -> {
            if (isSaveAction) {
                deleteConfirm();
            } else {
                clearFields();
            }
        });

        tagsMenu.getCheckModel().getCheckedItems().addListener((javafx.collections.ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (String addedItem : change.getAddedSubList()) {
                        // Handle item checked
                        tagMenuSelect();
                    }
                }
                if (change.wasRemoved()) {
                    for (String removedItem : change.getRemoved()) {
                        // Handle item unchecked
                        tagMenuSelect();
                    }
                }
            }
        });

        searchByContentCheckBox.selectedProperty().addListener(_ -> updateList());

        languageInitialise();

        content.textProperty().addListener((observable, oldValue, newValue) -> {
            tagsMenu.hide();
            isEditing = true;
            done.disableProperty().set(false);
        });

        title.textProperty().addListener((observable, oldValue, newValue) -> {
            tagsMenu.hide();
            isEditing = true;
            done.disableProperty().set(false);
        });

        listView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        listView.setOnKeyPressed(this::keyPressed);
        content.setOnKeyPressed(this::keyPressed);
        title.setOnKeyPressed(this::keyPressed);
        searchBox.setOnKeyPressed(this::keyPressed);
        webview.setOnKeyPressed(this::keyPressed);
        fileDataListView.setOnKeyPressed(this::keyPressed);

        try {
            stompClient = new StompClient(new URI("ws://localhost:8080/ws"), this);
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        stompClient.connect();

        startPolling();

        updateLanguage();

        Platform.runLater(() -> isEditing = true);
        title.requestFocus();
    }

    /**
     * Initialises WebEngine and Webview related processes
     */
    public void webInitialisation(){
        webEngine = webview.getEngine();
        URL stylesheet = getClass().getResource("/client/styles/notes.css");
        if (stylesheet != null) {
            webEngine.setUserStyleSheetLocation(stylesheet.toExternalForm());
        } else {
            System.err.println("Stylesheet not found: /client/styles/notes.css");
        }

        webEngine.setOnAlert(event -> {
            String url = event.getData();
            System.out.println("Triggered: " + url);
            if (url.startsWith("note://")) {
                String noteTitle = url.substring(7);
                Note linkedNote = findNoteByTitle(noteTitle);
                if (linkedNote != null) {
                    selectionChanged(null, lastSelectedNote, linkedNote);
                    listView.getSelectionModel().select(linkedNote);
                    listView.requestFocus();
                } else {
                    AlertMethods.createWarning(LanguageManager.getString("note_not_found") + " " + noteTitle);
                }
            } else if (url.startsWith("tag://")) {
                String tagName = url.substring(6); // Extract the tag name after "tag://"
                System.out.println("Tag clicked: " + tagName); // Debug: Print the tag name
                if (tagsMenu.getItems().contains(tagName)) {
                    tagsMenu.getCheckModel().check(tagName);
                    List<String> tag = new ArrayList<>();
                    tag.add(tagName);
                    tagNotes = FXCollections.observableList(filterNotesByTag(tag));
                    listView.setItems(tagNotes);
                    listView.getSelectionModel().select(0);
                    isSaveAction = true;
                    hasSelectedTag = true;
                    clear.disableProperty().set(false);
                }
            }
        });
    }

    /**
     * Initialises file related processes
     */
    public void fileInitialisation(){
        fileDataListView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(FileData fileData, boolean empty) {
                super.updateItem(fileData, empty);

                if (empty || fileData == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Button deleteB = new Button("Delete");
                    Button renameB = new Button("Rename");
                    Button downloadB = new Button("Download");

                    deleteB.setOnAction(event -> {
                        if (server.deleteFile(lastSelectedNote.getId(), fileData.getFileName())) {
                            getListView().getItems().remove(fileData);
                        }
                    });

                    renameB.setOnAction(event -> {
                        TextInputDialog dialog = new TextInputDialog(fileData.getFileName());
                        dialog.setTitle("Rename file");
                        dialog.setHeaderText(null);
                        dialog.setContentText("New file name: ");
                        Optional<String> result = dialog.showAndWait();
                        result.ifPresent(newName -> {
                            if (server.changeFileName(fileData.getRelatedNoteId(), fileData.getFileName(), newName)) {
                                fileData.setFileName(newName);
                                getListView().refresh();
                            }
                        });
                    });

                    downloadB.setOnAction(event -> {
                        try (InputStream is = server.downloadFile(fileData.getRelatedNoteId(), fileData.getFileName())) {
                            FileChooser fileChooser = new FileChooser();
                            fileChooser.setTitle("Save File");
                            fileChooser.setInitialFileName(fileData.getFileName());
                            File saveFile = fileChooser.showSaveDialog(null);

                            if(saveFile != null){
                                Files.copy(is, saveFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("File downloaded successfully");
                            }
                            else{
                                System.out.println("Download cancelled");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });


                    VBox cellLayout = new VBox(10);
                    cellLayout.getChildren().addAll(deleteB, renameB, downloadB);
                    setText(fileData.getFileName());
                    setGraphic(cellLayout);
                }
            }
        });
    }

    /**
     * Initialises language related processes
     */
    public void languageInitialise(){
        languageSelectorCombo.setItems(FXCollections.observableArrayList("EN", "NL", "RO", "EL"));

        languageSelectorCombo.setCellFactory(cb -> new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String languageCode, boolean empty) {
                super.updateItem(languageCode, empty);
                if (empty || languageCode == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    String imagePath = switch (languageCode) {
                        case "EN" -> "/client/img/UK.png";
                        case "NL" -> "/client/img/NL.png";
                        case "RO" -> "/client/img/RO.png";
                        case "EL" -> "/client/img/EL.png";
                        default -> null;
                    };
                    imageView.setImage(new Image(getClass().getResource(imagePath).toExternalForm()));
                    imageView.setFitWidth(22);
                    imageView.setFitHeight(16);
                    setGraphic(imageView);
                    setText(languageCode);
                }
            }
        });

        languageSelectorCombo.setButtonCell(new ListCell<>() {
            private final ImageView imageView = new ImageView();

            @Override
            protected void updateItem(String languageCode, boolean empty) {
                super.updateItem(languageCode, empty);
                imageView.setImage(new Image(getClass().getResource("/client/img/icon.png").toExternalForm()));
                imageView.setFitWidth(22);
                imageView.setFitHeight(22);
                setGraphic(imageView);
                setText(null);
            }
        });

        languageSelectorCombo.getSelectionModel().select(LanguageManager.getCurrentLanguageCode().toUpperCase());

        languageSelectorCombo.setOnAction(_ -> {
            String selectedLanguage = languageSelectorCombo.getValue().toUpperCase();
            LanguageManager.loadLocale(selectedLanguage);
            updateLanguage();
        });
    }

    /**
     *Implements keyboard shortcuts in the app
     * S+Control: Saves or adds note (acts like the done button)
     * ESCAPE: Selects the searching search field
     * Up+Alt: Selects the previous note on the list view
     * Down+Alt: Selects the next note on the list view
     * (Both are usable without having any note selected, automatically selecting the first note)
     * T+Control: Selects Title text field
     * C+Control: Selects Content text field
     * D+Control: Deletes note, or clears note about to be made
     * N+Control: New note creation
     * M+Control: Shoes tag menu
     * X+Control: Clears applied filters
     * B+Control: Checks or unchecks the search content checkbox
     * L+Control: Opens languages menu
     * R+Control: Refreshes
     * C+Alt: Opens Collections menu
     * I+Ctrl: Opens Information menu
     *
     *
     * @param e key pressed
     */
    public void keyPressed(KeyEvent e) {
        switch (e.getCode()) {
            case S:
                keyPressedS(e);
                e.consume();
                break;
            case ESCAPE:
                searchBox.requestFocus();
                e.consume();
                break;
            case UP:
                keyPressedUP(e);
                e.consume();
                break;
            case DOWN:
                keyPressedDOWN(e);
                e.consume();
                break;
            case T:
                keyPressedT(e);
                e.consume();
                break;
            case C:
                keyPressedC(e);
                e.consume();
                break;
            case D:
                keyPressedD(e);
                e.consume();
                break;
            case N:
                if (e.isControlDown()) {
                    createNote();
                }
                e.consume();
                break;
            case M:
                if (e.isControlDown()) {
                    tagsMenu.show();
                }
                e.consume();
                break;
            case X:
                if (e.isControlDown()) {
                    clearTags();
                }
                e.consume();
                break;
            case B:
                keyPressedB(e);
                e.consume();
                break;
            case R:
                if (e.isControlDown()) {
                    refresh();
                }
                e.consume();
                break;
            case L:
                if (e.isControlDown()) {
                    languageSelectorCombo.show();
                }
                e.consume();
                break;
            case I:
                if (e.isControlDown()) {
                    openInformation();
                }
                e.consume();
                break;
            default:
                break;
        }
    }

    /**
     * Treats the key pressed event for the UP key
     * @param e key pressed
     */
    public void keyPressedUP(KeyEvent e){
        if(e.isAltDown()){
            if (listView.getSelectionModel().isEmpty()) {
                listView.getSelectionModel().select(0);
                e.consume();
            } else if (listView.getSelectionModel().getSelectedIndex() <= 0) {
                e.consume();
            } else {
                listView.getSelectionModel().selectPrevious();
                title.requestFocus();
                title.positionCaret(title.getText().length());
            }
            e.consume();
        }
    }

    /**
     * Treats the key pressed event for the DOWN key
     * @param e key pressed
     */
    public void keyPressedDOWN(KeyEvent e){
        if (e.isAltDown()) {
            if (listView.getSelectionModel().isEmpty()) {
                listView.getSelectionModel().select(0);
                e.consume();
            } else {
                listView.getSelectionModel().selectNext();
                title.requestFocus();
                title.positionCaret(title.getText().length());
            }
            e.consume();
        }
    }

    /**
     * Treats the key pressed event for the C key
     * @param e key pressed
     */
    public void keyPressedC(KeyEvent e){
        if (e.isControlDown()) {
            content.requestFocus();
            updateNote();
            if (content.getText() != null) {
                content.positionCaret(content.getText().length());
            } else {
                content.positionCaret(0);
            }
        } else if (e.isAltDown()) {
            openCollectionMenu();
        }
    }

    /**
     * Treats the key pressed event for the B key
     * @param e key pressed
     */
    public void keyPressedB(KeyEvent e){
        if (e.isControlDown()) {
            if (searchByContentCheckBox.isSelected()) {
                searchByContentCheckBox.selectedProperty().set(false);
            } else {
                searchByContentCheckBox.selectedProperty().set(true);
            }
        }
    }

    /**
     * Treats the key pressed event for the T key
     * @param e
     */
    public void keyPressedT(KeyEvent e){
        if (e.isControlDown()) {
            title.requestFocus();
            updateNote();
            title.positionCaret(title.getText().length());
        }
    }

    /**
     * Treats the key pressed event for the S key
     * @param e key pressed
     */
    public void keyPressedS(KeyEvent e){
        if (e.isControlDown()) {
            if (isSaveAction) {
                save();
            } else {
                create();
            }
            e.consume();
        }
    }

    /**
     * Treats the key pressed event for the D key
     * @param e key pressed
     */
    public void keyPressedD(KeyEvent e){
        if (e.isControlDown()) {
            if (isSaveAction) {
                deleteConfirm();
                updateNote();
            } else {
                clearFields();
            }
        }
    }

    /**
     * Is called when the user selects a tag from the dropdown menu
     *
     */
    public void tagMenuSelect() {
        tagNotes = FXCollections.observableList(filterNotesByTag(tagsMenu.getCheckModel().getCheckedItems()));
        if (tagNotes.isEmpty()) {
            hasSelectedTag = false;
            isSaveAction = false;
            isEditing = false;
            refresh();
            return;
        }
        listView.setItems(tagNotes);
        listView.getSelectionModel().select(0);
        isSaveAction = true;
        hasSelectedTag = true;
        clear.disableProperty().set(false);
    }

    /**
     * Filters notes based on the selected tag from the dropdown menu
     * 
     * @param tags tags selected
     * @return The filtered list of notes
     */
    public List<Note> filterNotesByTag(List<String> tags) {
        return visibleNotes.stream()
                .filter(note -> note.getTags().stream().anyMatch(tags::contains))
                .toList();
    }


    /**
     * Parses the current HTML and replaces notes references with links, checking if
     * they are valid
     *
     * @param htmlContent The HTML contents of the current note
     * @return The updated HTML contents
     */
    private String updateNoteLinksHTML(String htmlContent) {
        StringBuilder updatedHtml = new StringBuilder();
        int lastIndex = 0;

        String regex = "\\[\\[(.+?)]]";
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(regex).matcher(htmlContent);

        while (matcher.find()) {
            updatedHtml.append(htmlContent, lastIndex, matcher.start());

            String noteTitle = matcher.group(1);
            Note linkedNote = findNoteByTitle(noteTitle);

            updatedHtml.append("<a href=\"note://")
                    .append(noteTitle)
                    .append("\" onclick=\"alert('note://")
                    .append(noteTitle)
                    .append("'); return false;\"");
            if (linkedNote == null) {
                updatedHtml.append("style=\"color:red; font-weight:bold;\"");
            }
            updatedHtml.append(">")
                    .append(noteTitle)
                    .append("</a> ");

            lastIndex = matcher.end();
        }
        updatedHtml.append(htmlContent.substring(lastIndex));

        return updatedHtml.toString();
    }

    /**
     * Returns a note matching the given title
     *
     * @param title The title of the requested note
     * @return The note with the given title, null if not found
     */
    public Note findNoteByTitle(String title) {
        return getNotesBySelectedCollection().stream()
                .filter(note -> note.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    /**
     * Updates the input fields when the selected note in the ListView changes.
     *
     * @param observable the observable value that represents the selected item
     * @param oldValue   the previously selected note
     * @param newValue   the newly selected note
     */
    private void selectionChanged(ObservableValue<? extends Note> observable, Note oldValue, Note newValue) {
        if (Objects.equals(oldValue, newValue)) {
            return;
        }

        if (newValue == null) {
            title.setText(null);
            content.setText(null);
            return;
        }
        title.setText(newValue.getTitle());
        content.setText(newValue.getContent());

        updateWebView();

        noteFiles.setAll(server.fetchFileNames(newValue.getId()));

        lastSelectedNote = newValue;
        delete.disableProperty().set(false);
        add.disableProperty().set(false);
        done.disableProperty().set(true);
        done.setOnAction(_ -> save());
        isSaveAction = true;
    }

    /**
     * Saves the currently entered note by sending it to the server.
     * Displays an error alert if the server operation fails.
     * Refreshes the list of notes after successfully adding a note.
     */
    public void create() {
        if (!checkInput()) {
            return;
        }
        try {
            server.addNote(getNote(), getCurrentCollection().getServer());
        } catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        try {
            stompClient.send("SEND\n" + "destination:/app/note-updates\n\n" + objectMapper.writeValueAsString(getNote()) + "\0");
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        filterTagList();
        clearFields();
        isEditing = false;
        refresh();
        title.requestFocus();
        isEditing = true;
        done.setText(LanguageManager.getString("added_prompt"));
        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(event -> done.setText(LanguageManager.getString("done")));
        pause.play();

    }

    /**
     * Updates the currently selected note by sending it to the server.
     * Displays an error alert if the server operation fails.
     * Refreshes the list of notes after successfully updating the note.
     */
    public void save() {
        if (!checkInput()) {
            return;
        }

        Note selectedNote = lastSelectedNote;
        String displayTitle = title.getText();
        String displayContent = content.getText();

        if (!selectedNote.getTitle().equalsIgnoreCase(displayTitle)) {
            updateNoteReferences(selectedNote.getTitle(), displayTitle);
        }

        try {
            server.saveNote(selectedNote.getId(), getNote(), getCurrentCollection().getServer());
            lastSelectedNote = server.getNoteById(selectedNote.getId(), getCurrentCollection().getServer());
        } catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        selectedNote.setTitle(getNote().getTitle());
        selectedNote.setContent(getNote().getContent());
        selectedNote.renderRawText(selectedNote.getId());
        selectedNote.extractTagsFromContent();

        try {
            stompClient.send("SEND\n" + "destination:/app/note-updates\n\n" + objectMapper.writeValueAsString(selectedNote) + "\0");
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        isEditing = false;
        justEdited = true;
        int lastSelectedIndex = listView.getSelectionModel().getSelectedIndex();
        refresh();
        filterTagList();
        isEditing = true;
        justEdited = false;
        title.setText(displayTitle);
        content.setText(displayContent);
        updateWebView();
        listView.getSelectionModel().select(lastSelectedIndex);
        done.disableProperty().set(false);
        content.requestFocus();
        content.positionCaret(content.getText().length());
        done.setText(LanguageManager.getString("saved_prompt"));
        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(event -> done.setText(LanguageManager.getString("done")));
        pause.play();
    }

    /**
     * Updates all note references when a title is changed
     *
     * @param oldTitle The old note title
     * @param newTitle The new note title
     */
    private void updateNoteReferences(String oldTitle, String newTitle) {
        for (Note note : getNotesBySelectedCollection()) {
            if (note.getTitle().equalsIgnoreCase(oldTitle))
                continue;

            String newContent = note.getContent().replaceAll("(?i)\\[\\[" + oldTitle + "]]",
                    "\\[\\[" + newTitle + "]]");

            if (!newContent.equals(note.getContent())) {
                note.setContent(newContent);
                try {
                    server.saveNote(note.getId(), note, getCurrentCollection().getServer());
                } catch (NullPointerException | WebApplicationException e) {
                    AlertMethods.createError(e.getMessage());
                }
            }
        }
    }

    /**
     * Checks whether the current title is valid, makes a few minor tweaks to the
     * text
     *
     * @return True if the input is valid, false otherwise
     */
    private boolean checkInput() {
        if (title.getText() == null || title.getText().isBlank()) {
            AlertMethods.createWarning(LanguageManager.getString("note_title_empty"));
            return false;
        }

        title.setText(title.getText().trim());
        if (getNotesBySelectedCollection().stream().anyMatch(
                note -> note.getTitle().equalsIgnoreCase(title.getText()) && !note.equals(lastSelectedNote))) {
            AlertMethods.createWarning(LanguageManager.getString("note_title_duplicate"));
            return false;
        }

        if (content.getText() == null) {
            content.setText("");
        }
        return true;
    }

    /**
     * Creates a new Note object using the current input field values.
     *
     * @return a new Note object with the entered title and content
     */
    private Note getNote() {
        String t = null;
        if (title.getText() != null && !title.getText().isBlank()) {
            t = title.getText();
        }

        var c = content.getText();

        Note temporary = new Note(t, c, selectedCollection.getTitle());
        if (lastSelectedNote != null) {
            temporary.renderRawText(lastSelectedNote.getId());
        }
        return temporary;
    }

    /**
     * Retrieves the currently selected note from the ListView.
     *
     * @return Currently selected note object
     */
    public Note getSelectedNote() {
        return listView.getSelectionModel().getSelectedItems().isEmpty()
                ? null
                : listView.getSelectionModel().getSelectedItems().getFirst();
    }


    /**
     * Shows deletion confirmation menu
     */
    public void deleteConfirm() {
        mainNotes.showDeleteConfirmation();
    }

    /**
     * Deletes the currently selected note from the ListView.
     */
    public void deleteNote() {
        Note selectedNote = getSelectedNote();
        Long selectedNoteId = selectedNote.getId();
        clearFields();
        server.deleteNoteById(selectedNote.getId(), getCurrentCollection().getServer());
        server.deleteFile(selectedNote.getId(), null);
        noteFiles.setAll(server.fetchFileNames(selectedNote.getId()));
        try {
            System.out.println("Sending delete message for note with ID: " + selectedNoteId);
            stompClient.send("SEND\n" + "destination:/app/note-deletions\n\n" + objectMapper.writeValueAsString(selectedNote) + "\0");
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        isEditing = false;
        refresh();
        done.setOnAction(_ -> create());
        isSaveAction = false;
        if (hasSelectedTag) {
            isSaveAction = true;
            listView.getSelectionModel().select(0);
        }
        delete.disableProperty().set(true);
        done.setText(LanguageManager.getString("deleted_prompt"));
        PauseTransition pause = new PauseTransition(Duration.seconds(1.3));
        pause.setOnFinished(event -> done.setText(LanguageManager.getString("done")));
        pause.play();
    }

    /**
     * Updates the contents of a note in the database
     */
    public void updateNote() {
        isEditing = true;

        add.disableProperty().set(true);
        delete.disableProperty().set(false);

        if (isSaveAction && title.getText() != null) {
            done.disableProperty().set(false);
            done.setOnAction(_ -> save());
            isSaveAction = true;
        } else {
            done.disableProperty().set(false);
            done.setOnAction(_ -> create());
            isSaveAction = false;
        }
    }

    /**
     * Starts a periodic polling task to refresh the notes every 5 seconds.
     */
    private void startPolling() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(this::refresh), 0, 5, TimeUnit.SECONDS);
    }

    /**
     * refreshes the language of the scene to the current language selected in the
     * language menu.
     */
    private void updateLanguage() {
        done.setText(LanguageManager.getString("done"));
        delete.setText(LanguageManager.getString("delete"));
        add.setText(LanguageManager.getString("add"));
        searchBox.setPromptText(LanguageManager.getString("search_prompt"));
        searchByContentCheckBox.setText(LanguageManager.getString("content_search"));
        content.setPromptText(LanguageManager.getString("content_prompt"));
        title.setPromptText(LanguageManager.getString("title_prompt"));
        deleteAllFilesButton.setText(LanguageManager.getString("delete_all_files"));
        fileSelectButton.setText(LanguageManager.getString("add_file"));
        deleteFilesButton.setText(LanguageManager.getString("delete_files"));


        switch (LanguageManager.getCurrentLanguageCode().toUpperCase()) {
            case "EN":
                languageIndicator.setImage(
                        new ImageView(getClass().getResource("/client/img/UK.png").toExternalForm()).getImage());
                break;
            case "NL":
                languageIndicator.setImage(
                        new ImageView(getClass().getResource("/client/img/NL.png").toExternalForm()).getImage());
                break;
            case "RO":
                languageIndicator.setImage(
                        new ImageView(getClass().getResource("/client/img/RO.png").toExternalForm()).getImage());
                break;
            case "EL":
                languageIndicator.setImage(
                        new ImageView(getClass().getResource("/client/img/EL.png").toExternalForm()).getImage());
                break;
        }

        if (mainNotes != null) {
            ((Stage) mainNotes.getPrimaryStage()).setTitle(LanguageManager.getString("overview_title"));
        }

        refresh();
    }

    /**
     * Setter for mainNotesCtrl
     *
     * @param mainNotesCtrl The object being set
     */
    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        mainNotes = mainNotesCtrl;
    }

    /**
     * Opens the Collection menu
     */
    public void openCollectionMenu() {
        mainNotes.showCollectionOverview();
    }

    /**
     * Sets the selected note, updating its title, content, and rendering the raw
     * text.
     * If a tag is selected, the tag update list is refreshed; otherwise, the
     * regular list is updated.
     *
     * @param newNote The new note to set as selected.
     */
    public void setSelectedNote(Note newNote) {
        lastSelectedNote.setTitle(newNote.getTitle());
        lastSelectedNote.setContent(newNote.getContent());
        lastSelectedNote.renderRawText(lastSelectedNote.getId());
        lastSelectedNote.extractTagsFromContent();
        updateWebView();
        if (hasSelectedTag) {
            tagUpdateList();
            return;
        }
        updateList();
    }

    /**
     *
     * Opens information window
     *
     *
     */
    public void openInformation() {
        mainNotes.showInformationOverview();}


     /** Getter for the selected collection ID
     *
     * @return The title of the selected collection
     */
    public String getSelectedCollectionID() {
        return selectedCollection.getTitle();
    }

    /**
     * Getter for hasSelectedTag
     *
     * @return True if a tag is selected, false otherwise
     */
    public boolean getHasSelectedTag() {
        return hasSelectedTag;
    }

    /**
     * Adds new note to the data list when received from the server through WS
     *
     * @param newNote The new note to add
     */
    public void addNoteToData(Note newNote){
        data.add(newNote);
    }

    /**
     * Deals with note updates received through WebSocket
     * @param updatedNote the target note
     */
    @Override
    public void handleNoteUpdate(Note updatedNote){
        try {
            if (getSelectedNote() != null && updatedNote.getId() == getSelectedNote().getId()) {
                setSelectedNote(updatedNote);
            }
            else if (updatedNote.getCollectionTitle().equals(getCurrentCollection().getTitle())) {
                for (Note note : data) {
                    if (note.getId() == updatedNote.getId()) {
                        note.setTitle(updatedNote.getTitle());
                        note.setContent(updatedNote.getContent());
                        note.setTags(updatedNote.getTags());
                        note.renderRawText(updatedNote.getId());
                        if (getHasSelectedTag()) {
                            tagUpdateList();
                            return;
                        }
                        updateList();
                        return;
                    }
                }
            }
            else if(updatedNote.getCollectionTitle().equals(getCurrentCollection().getTitle())){
                addNoteToData(updatedNote);
                if (getHasSelectedTag()) {
                    tagUpdateList();
                    return;
                }
                updateList();
            }
        }
        catch (Exception e) {
            System.err.println("Error processing WebSocket update: " + e.getMessage());
        }
    }

    /**
     * Deals with note deletions received through WebSocket
     * @param deletedNoteID target note ID
     */
    @Override
    public void handleNoteDeletion(Long deletedNoteID){
        for (Note note : data) {
            if (note.getId() == deletedNoteID) {
                data.remove(note);
                break;
            }
        }
        if (getHasSelectedTag()) {
            tagUpdateList();
            return;
        }
        updateList();
    }
}
