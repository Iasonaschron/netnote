package client;

import client.scenes.MainNotesCtrl;
import client.utils.ServerUtils;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.google.inject.Guice.createInjector;

public class MainNotes extends Application {
    private static final Injector INJECTOR = createInjector(new NotesModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) throws URISyntaxException, IOException {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        var serverUtils = INJECTOR.getInstance(ServerUtils.class);
        if (!serverUtils.isServerAvailable()) {
            var msg = "Server needs to be started before the client, but it does not seem to be available. Shutting down.";
            System.err.println(msg);
            return;
        }

        //var overview = FXML.load(NoteOverviewCtrl.class, "client", "scenes", "NoteOverview");

        var mainNotesCtrl = INJECTOR.getInstance(MainNotesCtrl.class);
        mainNotesCtrl.initialize(primaryStage);
    }
}
