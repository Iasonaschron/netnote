package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;
import commons.AlertMethods;
import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controller for the Note Overview scene.
 * Manages a list of notes and provides functionality for creating, viewing, and
 * editing notes.
 */
public class NoteOverviewCtrl implements Initializable {

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
    private ChoiceBox<String> tagsmenu;

    private long selectedCollectionId;
    private List<Note> data;
    private ObservableList<Note> visibleNotes;
    private ObservableList<Note> tagNotes;

    private final ServerUtils server;

    private Note lastSelectedNote = null;

    private ScheduledExecutorService scheduler;
    private boolean isEditing = false;

    private boolean isSaveAction = false;
    private boolean justEdited = false;

    private List<String> tags = new ArrayList<>();

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
     * Sets the collection id and updates dataCollection
     *
     * @param selectedCollectionId The new selection ID
     */
    public void setSelectedCollectionId(long selectedCollectionId) {
        this.selectedCollectionId = selectedCollectionId;
    }

    /**
     * Updates the dataCollection list according to the currently selected collection
     */
    private List<Note> getNotesBySelectedCollection() {
        return data.stream().filter(note -> note.getCollectionId() == selectedCollectionId).toList();
    }

    /**
     * Refreshes the list of notes by fetching the latest data from the server.
     * Updates the ListView with the retrieved notes.
     */
    public void refresh() {
        if (isEditing) {
            return;
        }
        data = server.getNotes();

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
        filterTagList();

        if (tagsmenu.getValue() != null && justEdited) {
            // Filter notes based on the selected tag
            tagNotes = FXCollections.observableList(filterNotesByTag(tagsmenu.getValue()));
            listView.setItems(tagNotes);
        } else {
            // No selection; show all visible notes
            listView.setItems(visibleNotes);
        }


        if (lastSelectedNote == null || !visibleNotes.contains(lastSelectedNote))
            clearFields();
    }

    /**
     * Updates the dropdown menu of available tags to filter by
     */
    public void filterTagList(){
        tags = visibleNotes.stream().
                flatMap(note -> note.getTags().stream()).distinct().
                toList();

        tagsmenu.setItems(FXCollections.observableArrayList(tags));
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
        if (filter.isBlank() || filter.equals("$")) {
            return data;
        } else {
            if (filter.charAt(0) == '$') {
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
        listView.getSelectionModel().clearSelection();
        add.disableProperty().set(true);
        delete.disableProperty().set(true);
        done.disableProperty().set(false);
        done.setOnAction(_ -> create());
        isSaveAction = false;
        lastSelectedNote = null;
        if (tagsmenu.getValue() != null) {
            listView.setItems(visibleNotes);
        }
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
        setSelectedCollectionId(1001); //TODO: There is probably a better way to initialize the default id
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
            }
        });

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

        tagsmenu.setOnAction(this::tagMenuSelect);
        listView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        startPolling();
    }

    private void tagMenuSelect(javafx.event.ActionEvent actionEvent) {
        tagNotes = FXCollections.observableList(filterNotesByTag(tagsmenu.getValue()));
        listView.setItems(tagNotes);
        listView.getSelectionModel().select(0);
    }

    /**
     * Filters notes based on the selected tag from the dropdown menu
     * @param tag tag selected
     * @return The filtered list of notes
     */
    public List<Note> filterNotesByTag(String tag){
        return visibleNotes.stream().
                filter(note -> note.getTags().contains(tag)).
                toList();

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
            server.addNote(getNote());
        } catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        isEditing = false;
        refresh();
        clearFields();
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
            server.saveNote(selectedNote.getId(), getNote());
            lastSelectedNote = server.getNoteById(selectedNote.getId());
        } catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        isEditing = false;
        justEdited = true;
        refresh();
        justEdited = false;
        title.setText(displayTitle);
        content.setText(displayContent);
        updateWebView();
        done.disableProperty().set(true);
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
                    server.saveNote(note.getId(), note);
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

        Note temporary = new Note(t, c);
        temporary.renderRawText();
        return temporary;
    }

    /**
     * Retrieves the currently selected note from the ListView.
     *
     * @return Currently selected note object
     */
    public Note getSelectedNote() {
        return listView.getSelectionModel().getSelectedItems().getFirst();
    }

    /**
     * Deletes the currently selected note from the ListView.
     */
    public void deleteNote() {
        Note selectedNote = getSelectedNote();
        clearFields();
        server.deleteNoteById(selectedNote.getId());
        isEditing = false;
        refresh();
        done.setOnAction(_ -> create());
        isSaveAction = false;
        delete.disableProperty().set(true);
    }

    /**
     * Updates the contents of a note in the database
     */
    public void updateNote() {
        isEditing = true;

        add.disableProperty().set(true);
        delete.disableProperty().set(true);

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
        scheduler.scheduleAtFixedRate(() -> {
            Platform.runLater(this::refresh);
        }, 0, 5, TimeUnit.SECONDS);
    }
}
