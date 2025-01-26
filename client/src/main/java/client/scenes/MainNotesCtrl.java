package client.scenes;

import client.utils.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.springframework.stereotype.Controller;

/**
 * The main controller class responsible for managing the primary stage and
 * navigating between scenes
 * in the Notes application.
 */
@Controller
public class MainNotesCtrl {
    private Stage primaryStage;
    private Stage secondaryStage;
    private Stage tertiaryStage;
    private Stage informationStage;

    private CollectionOverviewCtrl collectionOverviewCtrl;
    private Scene collectionOverview;

    private NoteOverviewCtrl noteOverviewCtrl;
    private Scene overview;

    private InformationOverviewCtrl informationOverviewCtrl;
    private Scene informationOverview;

    private DeleteConfirmationCtrl deleteConfirmationCtrl;
    private Scene deleteConfirmation;

    /**
     * Initializes the main application window and sets up the overview scene.
     *
     * @param primaryStage       the primary stage of the application
     * @param overview           a Pair containing the note overview controller and
     *                           the Parent node representing the overview scene
     *                           layout
     * @param collectionOverview a Pair containing the collection overview
     *                           controller and the Parent node representing the
     *                           collection overview scene layout
     * @param informationOverview a Pair containing the information overview
     *                            controller and the Parent node representing the
     *                            information overview scene layout
     * @param deleteConfirmation a Pair containing the delete confirmation
     *                           controller and the Parent node representing the
     *                           delete confirmation scene layout
     */
    public void initialize(Stage primaryStage, Pair<NoteOverviewCtrl, Parent> overview,
            Pair<CollectionOverviewCtrl, Parent> collectionOverview,
                           Pair<InformationOverviewCtrl, Parent> informationOverview,
                           Pair<DeleteConfirmationCtrl, Parent> deleteConfirmation) {

        this.primaryStage = primaryStage;
        this.secondaryStage = new Stage();
        this.tertiaryStage = new Stage();
        this.informationStage = new Stage();

        this.collectionOverviewCtrl = collectionOverview.getKey();
        this.collectionOverview = new Scene(collectionOverview.getValue());

        this.informationOverviewCtrl = informationOverview.getKey();
        this.informationOverview = new Scene(informationOverview.getValue());

        this.deleteConfirmationCtrl = deleteConfirmation.getKey();
        this.deleteConfirmation = new Scene(deleteConfirmation.getValue());

        this.noteOverviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

        tertiaryStage.initOwner(primaryStage);
        tertiaryStage.initModality(Modality.WINDOW_MODAL);

        noteOverviewCtrl.setMainNotesCtrl(this);

        showOverview();
        primaryStage.show();
    }

    /**
     * Displays the collection overview scene in the secondary stage.
     * Sets the stage title and ensures the collections are refreshed.
     */
    public void showCollectionOverview() {
        secondaryStage.setTitle(LanguageManager.getString("collection_overview_title"));
        collectionOverviewCtrl.refreshLanguage();
        secondaryStage.setScene(collectionOverview);
        collectionOverviewCtrl.refresh();

        secondaryStage.show();
    }


    /**
     * Displays the information overview scene in the secondary stage.
     * Sets the stage title.
     */
    public void showInformationOverview() {
        informationStage.setTitle(LanguageManager.getBundle().getString("information_title"));
        informationOverviewCtrl.refreshLanguage();
        informationStage.setScene(informationOverview);
        informationStage.setResizable(false);


        informationStage.show();
    }

    /**
     * Displays the deletion confirmation menu in the tertiary stage.
     * Sets the stage title.
     */
    public void showDeleteConfirmation() {
        tertiaryStage.setTitle(LanguageManager.getBundle().getString("delete_confirmation_title"));
        tertiaryStage.setScene(deleteConfirmation);
        tertiaryStage.setResizable(false);

        deleteConfirmationCtrl.setNoteOverviewCtrl(noteOverviewCtrl);
        deleteConfirmationCtrl.setMainNotesCtrl(this);
        deleteConfirmationCtrl.refreshLanguage();

        tertiaryStage.showAndWait();
    }

    /**
     * Closes the deletion confirmation menu
     */
    public void closeDeleteConfirmation(){
        tertiaryStage.close();
    }

    /**
     * Displays the overview scene in the primary stage.
     * Sets the stage title and ensures the notes are refreshed.
     */
    public void showOverview() {
        primaryStage.setTitle(LanguageManager.getString("overview_title"));
        primaryStage.setScene(overview);
        noteOverviewCtrl.refresh();
        overview.setOnKeyPressed(e -> noteOverviewCtrl.keyPressed(e));

    }

    /**
     * Returns the NoteOverviewCtrl instance associated with the main controller.
     *
     * @return the NoteOverviewCtrl instance
     */
    public Object getNoteOverviewCtrl() {
        return noteOverviewCtrl;
    }

    /**
     * Returns the CollectionOverviewCtrl instance associated with the main
     * controller.
     * 
     * @return the CollectionOverviewCtrl instance
     */
    public Object getCollectionOverviewCtrl() {
        return collectionOverviewCtrl;
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return the primary stage
     */
    public Object getPrimaryStage() {
        return primaryStage;
    }
}
