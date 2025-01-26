package client.scenes;

import static org.mockito.Mockito.*;

import client.utils.LanguageManager;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class MainNotesCtrlTest {

    @Mock
    private Stage primaryStage;

    @Mock
    private Stage secondaryStage;

    @Mock
    private Stage tertiaryStage;

    @Mock
    private Stage informationStage;

    @Mock
    private CollectionOverviewCtrl collectionOverviewCtrl;

    @Mock
    private NoteOverviewCtrl noteOverviewCtrl;

    @Mock
    private InformationOverviewCtrl informationOverviewCtrl;

    @Mock
    private DeleteConfirmationCtrl deleteConfirmationCtrl;

    @Mock
    private Scene collectionOverview;

    @Mock
    private Scene overview;

    @Mock
    private Scene informationOverview;

    @Mock
    private Scene deleteConfirmation;

    @InjectMocks
    private MainNotesCtrl mainNotesCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void closeDeleteConfirmation_closesTertiaryStage() {
        mainNotesCtrl.closeDeleteConfirmation();
        verify(tertiaryStage).close();
    }
}