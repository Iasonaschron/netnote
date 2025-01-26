package client.scenes;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import com.google.inject.Inject;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.Text;
import org.springframework.stereotype.Controller;

@Controller
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

    /**
     * Initializes the controller class.
     * @param location the location of the FXML file
     * @param resources the resources used in the FXML file
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * sets the reference to mainNotesCtrl so the methods of that class can be used
     * @param mainNotesCtrl the controller class of main notes
     */
    public void setMainNotesCtrl(MainNotesCtrl mainNotesCtrl) {
        this.mainNotesCtrl = mainNotesCtrl;
    }

    /**
     * Refreshes the language of the shortcuts
     */
    public void refreshLanguage() {
        shortcutsTitle.setText(LanguageManager.getString("shortcuts_title"));
        shortcuts.setText(LanguageManager.getBundle().getString("shortcuts"));
    }

    /**
     * Returns the mainNotesCtrl
     * @return the mainNotesCtrl
     */
    public MainNotesCtrl getMainNotesCtrl() {
        return mainNotesCtrl;
    }
}
