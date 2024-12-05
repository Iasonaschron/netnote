package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.Note;
import commons.AlertMethods;
import jakarta.ws.rs.WebApplicationException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
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
    private ListView<Note> listView;

    @FXML
    private TextField title;

    @FXML
    private TextArea content;

    @FXML
    private Button done;

    @FXML
    private Button delete;

    @FXML
    private Button add;

    private ObservableList<Note> data;

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
        if(isEditing) {
            return;
        }
        var notes = server.getNotes();
        data = FXCollections.observableList(notes);
        listView.setItems(data);
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
        done.setOnAction(event -> create());
        isSaveAction = false;
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
        lastSelectedNote = newValue;

        delete.disableProperty().set(false);
        add.disableProperty().set(false);
        done.disableProperty().set(true);
        done.setOnAction(event -> save());
        isSaveAction = true;
    }

    /**
     * Saves the currently entered note by sending it to the server.
     * Displays an error alert if the server operation fails.
     * Refreshes the list of notes after successfully adding a note.
     */
    public void create() {
        if (!checkInput()) {
            AlertMethods.createWarning("The note title cannot be empty.");
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
            AlertMethods.createWarning("The note title cannot be empty.");
            return;
        }

        Note selectedNote = lastSelectedNote;
        String displayTitle = title.getText();
        String displayContent = content.getText();
        try {
            server.saveNote(selectedNote.getId(), getNote());
        }
        catch (NullPointerException | WebApplicationException e) {
            AlertMethods.createError(e.getMessage());
            return;
        }

        isEditing = false;
        refresh();
        title.setText(displayTitle);
        content.setText(displayContent);
        done.setOnAction(event -> save());
        isSaveAction = true;
    }

    /**
     * Checks whether the current title is valid, makes a few minor tweaks to the text
     *
     * @return True if the input is valid, false otherwise
     */
    private boolean checkInput() {
        if (title.getText() == null || title.getText().isBlank()) {
            return false;
        }

        if (content.getText() == null) {
            content.setText("");
        }

        title.setText(title.getText().trim());
        return true;
    }

    /**
     * Creates a new Note object using the current input field values.
     *
     * @return a new Note object with the entered title and content
     */
    private Note getNote() {
        String t = null;
        if(title.getText() != null && !title.getText().isBlank()) {
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
        done.setOnAction(event -> create());
        isSaveAction = false;
        delete.disableProperty().set(true);
    }

    /**
     * Updates the contents of a note in the database
     */
    public void updateNote(){
        isEditing = true;

        add.disableProperty().set(true);
        delete.disableProperty().set(true);


        if(isSaveAction && title.getText() != null){
            done.disableProperty().set(false);
            done.setOnAction(event -> save());
            isSaveAction = true;
        }
        else{
            done.disableProperty().set(false);
            done.setOnAction(event -> create());
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
