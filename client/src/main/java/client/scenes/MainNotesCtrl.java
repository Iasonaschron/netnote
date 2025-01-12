package client.scenes;

import client.utils.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * The main controller class responsible for managing the primary stage and
 * navigating between scenes
 * in the Notes application.
 */
public class MainNotesCtrl {
    private Stage primaryStage;
    private Stage secondaryStage;

    private CollectionOverviewCtrl collectionOverviewCtrl;
    private Scene collectionOverview;

    private NoteOverviewCtrl noteOverviewCtrl;
    private Scene overview;

    /**
     * Initializes the main application window and sets up the overview scene.
     *
     * @param primaryStage the primary stage of the application
     * @param overview     a Pair containing the note overview controller and the
     *                     Parent node representing the overview scene layout
     */
    public void initialize(Stage primaryStage, Pair<NoteOverviewCtrl, Parent> overview,
            Pair<CollectionOverviewCtrl, Parent> collectionOverview) {

        this.primaryStage = primaryStage;
        this.secondaryStage = new Stage();

        this.collectionOverviewCtrl = collectionOverview.getKey();
        this.collectionOverview = new Scene(collectionOverview.getValue());

        this.noteOverviewCtrl = overview.getKey();
        this.overview = new Scene(overview.getValue());

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
        secondaryStage.setScene(collectionOverview);
        collectionOverviewCtrl.refresh();

        secondaryStage.show();
    }

    /**
     * Displays the overview scene in the primary stage.
     * Sets the stage title and ensures the notes are refreshed.
     */
    public void showOverview() {
        primaryStage.setTitle(LanguageManager.getString("overview_title"));
        primaryStage.setScene(overview);
        noteOverviewCtrl.refresh();
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
