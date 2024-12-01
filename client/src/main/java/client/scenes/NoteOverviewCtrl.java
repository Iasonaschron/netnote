package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;

import commons.Note;
import jakarta.ws.rs.WebApplicationException;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Modality;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

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

    private ObservableList<Note> data;

    private final ServerUtils server;

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
        done.visibleProperty().set(true);
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
        done.visibleProperty().set(false);
    }

    /**
     * Saves the currently entered note by sending it to the server.
     * Displays an error alert if the server operation fails.
     * Refreshes the list of notes after successfully adding a note.
     */
    public void done() {
        try {
            server.addNote(getNote());
        } catch (WebApplicationException e) {

            var alert = new Alert(Alert.AlertType.ERROR);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            return;
        }

        refresh();
        clearFields();
    }

    /**
     * Creates a new Note object using the current input field values.
     *
     * @return a new Note object with the entered title and content
     */
    private Note getNote() {
        var t = title.getText();
        var c = content.getText();

        return new Note(t, c);
    }

    /**
     * Retrieves the currently selected note from the ListView.
     * Prints the note to the console for debugging purposes.
     */
    public void getSelectedNote() {
        Note selectedNote = listView.getSelectionModel().getSelectedItems().getFirst();
        System.out.println(selectedNote);
    }

    /**
     * Deletes the currently selected note from the ListView.
     */
    public void deleteNote() {
        Note selectedNote = listView.getSelectionModel().getSelectedItems().getFirst();
        server.deleteNoteById(selectedNote.getId());
        refresh();
    }
}
