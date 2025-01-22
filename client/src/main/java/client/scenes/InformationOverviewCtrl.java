package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;

public class InformationOverviewCtrl implements Initializable {

    @FXML
    private Text shortcutsTitle;

    @FXML
    private Text shortcuts;


    private MainNotesCtrl mainNotesCtrl;

    private final ServerUtils server;

    /**
     * Constructs a new InformationOverviewCtrl with the specified server utility.
     *
     * @param server the server utility used to communicate with the server
     */
    @Inject
    public InformationOverviewCtrl(ServerUtils server) {
        this.server = server;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        this.mainNotesCtrl = mainNotesCtrl;
    }

    public void refreshLanguage() {
        shortcutsTitle.setText(LanguageManager.getString("shortcuts_title"));
        shortcuts.setText(LanguageManager.getBundle().getString("shortcuts"));
    }

}
