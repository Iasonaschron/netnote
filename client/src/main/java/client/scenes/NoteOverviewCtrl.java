package client.scenes;

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
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
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

    private List<Note> data;
    private ObservableList<Note> visibleNotes;

    private final ServerUtils server;

    private Note lastSelectedNote = null;

    private ScheduledExecutorService scheduler;
    private boolean isEditing = false;

    private boolean isSaveAction = false;

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
     * Refreshes the list of notes by fetching the latest data from the server.
     * Updates the ListView with the retrieved notes.
     */
    public void refresh() {
        if (isEditing) {
            return;
        }
        data = server.getNotes();

        updateList();

        listView.setItems(visibleNotes);
    }

    /**
     * Updates the list of notes based on the current filter string.
     * This method is called when the filter string changes or the data is
     * refreshed.
     * It updates the ListView with the filtered notes.
     */
    public void updateList() {
        visibleNotes = FXCollections.observableList(getVisibleNotes(searchBox.getText()));

        listView.setItems(visibleNotes);
    }

    /**
     * Updates the WebView with the currently selected note's content.
     * This method is called when the selected note changes.
     */
    public void updateWebView() {
        webEngine.loadContent(getNote().getHTML());
    }

    /**
     * Filters the list of notes based on the given filter string.
     * If the filter is empty, all notes are returned.
     * Otherwise, only notes with titles containing the filter string are returned.
     * 
     * @param filter the filter string to apply to the list of notes
     * @return a list of notes that match the filter string
     */
    public List<Note> getVisibleNotes(String filter) {
        if (filter.isBlank()) {
            return data;
        } else {
            return data.stream()
                    .filter(note -> note.getTitle().toLowerCase().contains(filter.toLowerCase()))
                    .toList();
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
    }

    /**
     * Clears the input fields for the title and content of a note.
     */
    private void clearFields() {
        title.clear();
        content.clear();
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
        webEngine = webview.getEngine();

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
                    AlertMethods.createWarning("Note not found: " + noteTitle);
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

        listView.getSelectionModel().selectedItemProperty().addListener(this::selectionChanged);

        startPolling();
    }

    private Note findNoteByTitle(String title) {
        return data.stream()
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
        try {
            server.saveNote(selectedNote.getId(), getNote());
            lastSelectedNote = server.getNoteById(selectedNote.getId());
        } catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        isEditing = false;
        refresh();
        title.setText(displayTitle);
        content.setText(displayContent);
        updateWebView();
        done.disableProperty().set(true);
    }

    /**
     * Checks whether the current title is valid, makes a few minor tweaks to the
     * text
     *
     * @return True if the input is valid, false otherwise
     */
    private boolean checkInput() {
        if (title.getText() == null || title.getText().isBlank()) {
            AlertMethods.createWarning("The note title cannot be empty.");
            return false;
        }

        title.setText(title.getText().trim());

        if (visibleNotes.stream().anyMatch(
                note -> note.getTitle().equalsIgnoreCase(title.getText()) && !note.equals(lastSelectedNote))) {
            AlertMethods.createWarning("A note with this title already exists.");
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
