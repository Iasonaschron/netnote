package client.scenes;

import java.net.URL;
import java.util.ResourceBundle;

import client.utils.ServerUtils;
import javafx.fxml.Initializable;
import com.google.inject.Inject;

public class CollectionOverviewCtrl implements Initializable {
    private MainNotesCtrl mainNotesCtrl;

    private final ServerUtils server;

    /**
     * Constructs a new CollectionOverviewCtrl with the specified server utility.
     * 
     * @param server the server utility used to communicate with the server
     */
    @Inject
    public CollectionOverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        this.mainNotesCtrl = mainNotesCtrl;
    }

    public void refresh() {

    }

}