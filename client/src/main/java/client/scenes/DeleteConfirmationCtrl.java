package client.scenes;

import client.utils.ServerUtils;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class DeleteConfirmationCtrl implements Initializable {

    @FXML
    private Button deleteButton;

    @FXML
    private Button cancelButton;

    private MainNotesCtrl mainNotesCtrl;
    private NoteOverviewCtrl noteOverviewCtrl;
    private Scene overview;

    private final ServerUtils server;

    /**
     * Constructs a new DeleteConfirmationCtrl with the specified server utility.
     *
     * @param server the server utility used to communicate with the server
     */
    @Inject
    public DeleteConfirmationCtrl(ServerUtils server) {
        this.server = server;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * sets the reference to noteOverviewCtrl so the methods of that class can be used
     * @param noteOverviewCtrl the controller class of notes overview
     */
    public void setNoteOverviewCtrl(NoteOverviewCtrl noteOverviewCtrl) {
        this.noteOverviewCtrl = noteOverviewCtrl;
    }

    /**
     *sets the reference to MainNotesCtrl so the methods of the class can be used
     * @param mainNotesCtrl the main controller class of all the stage
     */
    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        this.mainNotesCtrl = mainNotesCtrl;
    }

    /**
     * Action of pressing the delete button. Deletes the currently selected note
     */
    public void deleteConfirmation() {
        noteOverviewCtrl.deleteNote();
        mainNotesCtrl.closeDeleteConfirmation();
    }

    /**
     * Cancels the deletion of the note
     */
    public void cancelDeletion() {
        mainNotesCtrl.closeDeleteConfirmation();
    }

}
