package editor;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Modality;

/**
 * Created by keno on 12.06.17.
 */
public class AlertDialog {

    /**
     * Show version.
     *
     * @param type    the type
     * @param message the message
     * @param title   the title
     */
    static void showVersion(Alert.AlertType type, String message, String title){
        Alert alert = new Alert(type, "");
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setHeaderText(title);
        alert.showAndWait().filter(response -> response == ButtonType.OK).ifPresent(response -> {
            try{
                Controller.programSettings.getProperty("showedversioninfo").equals(null);
            }catch(Exception e){
                Controller.programSettings.addProperty("showedversioninfo",Controller.VERSION_ASTEXT);
            }
            Controller.programSettings.setProperty("showedversioninfo",Controller.VERSION_ASTEXT);
        });
    }

    /**
     * Show alert.
     *
     * @param type    the type
     * @param message the message
     * @param title   the title
     */
    static void showAlert(Alert.AlertType type, String message, String title){
        Alert alert = new Alert(type, "");
        alert.setResizable(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.getDialogPane().setContentText(message);
        alert.getDialogPane().setHeaderText(title);
        alert.show();
    }
}
