package client;

import client.scenes.MainNotesCtrl;
import client.scenes.NoteOverviewCtrl;
import client.scenes.CollectionOverviewCtrl;
import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import static com.google.inject.Guice.createInjector;

public class MainNotes extends Application {
    private static final Injector INJECTOR = createInjector(new NotesModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    /**
     * launches application
     *
     * @param args the command-line arguments
     * @throws IOException        if an I/O error occurs
     */
    public static void main(String[] args) throws IOException {
        launch(args);
    }

    /**
     * Starts the application by checking the server and setting up the main scene.
     *
     * @param primaryStage the main stage of the application
     * @throws Exception if something goes wrong while loading the FXML
     */
    @Override
    public void start(Stage primaryStage) throws Exception {

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable("http://localhost:8080/")) {
            var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        var overview = FXML.load(NoteOverviewCtrl.class, LanguageManager.getBundle(), "client", "scenes",
                "NoteOverview.fxml");
        var collectionOverview = FXML.load(CollectionOverviewCtrl.class, LanguageManager.getBundle(), "client",
                "scenes", "CollectionOverview.fxml");

        var mainNotesCtrl = INJECTOR.getInstance(MainNotesCtrl.class);
        mainNotesCtrl.initialize(primaryStage, overview, collectionOverview);
    }
}
