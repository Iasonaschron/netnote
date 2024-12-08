package commons;

import javafx.scene.control.Alert;
import javafx.stage.Modality;

public class AlertMethods {
    /**
     * Create an error box with the given message
     *
     * @param message The error message
     */
    public static void createError(String message) {
        var alert = new Alert(Alert.AlertType.ERROR);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Create a warning box with the given message
     *
     * @param message The warning message
     */
    public static void createWarning(String message) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
