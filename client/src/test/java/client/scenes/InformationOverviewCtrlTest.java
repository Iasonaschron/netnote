package client.scenes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import client.utils.LanguageManager;
import client.utils.ServerUtils;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class InformationOverviewCtrlTest {

    @Mock
    private ServerUtils server;

    @Mock
    private MainNotesCtrl mainNotesCtrl;

    @Mock
    private Text shortcutsTitle;

    @Mock
    private Text shortcuts;

    @InjectMocks
    private InformationOverviewCtrl informationOverviewCtrl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void setMainNotesCtrl_setsMainNotesCtrl() {
        informationOverviewCtrl.setMainNotesCtrl(mainNotesCtrl);
        assertEquals(mainNotesCtrl, informationOverviewCtrl.getMainNotesCtrl());
    }
}