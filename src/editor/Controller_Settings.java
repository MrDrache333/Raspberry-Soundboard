package editor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import static editor.Controller.BACKGROUNDIMAGE;
import static editor.Raspberry.remotecommand;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 31.05.17.
 */
public class Controller_Settings {
    
    @FXML
    private TextArea ta_main;
    @FXML
    private TextField tf_cmd;
    @FXML
    private ImageView image_BACKGROUND;
    @FXML
    private ProgressIndicator progress;
    
    private static String HOST;
    private static String USER;
    private static String PASS;
    
    //Initialisierungs Funktion, welche aufgerufen wird, bevor die GUI angezeigt wird
    @FXML
    private void initialize() {
        //Versuchen die Bilder zu uebernehmen und die Buttons zu erstellen
        try {
            image_BACKGROUND.setImage(new Image(BACKGROUNDIMAGE.toURI().toURL().toString()));

        } catch (Exception ignored) {}
        
        ta_main.textProperty().addListener((observable, oldValue, newValue) -> {
            ta_main.setScrollTop(Double.MIN_VALUE); //this will scroll to the bottom
            //use Double.MIN_VALUE to scroll to the top
        });
        
    }
    
    private void taAddText(String msg){
        Platform.runLater(() -> {
            ta_main.setText(ta_main.getText() + msg + "\n");
            ta_main.appendText("");
        });

    }
    
    @FXML
    private void actionSend(ActionEvent event){
        Platform.runLater(() -> progress.setVisible(true));
        boolean stop = false;
        String cmd = "";
        try{
            cmd = tf_cmd.getText();
        }catch(Exception e){
            stop = true;
        }
        if (!stop){
            Timer timer = new Timer();
            String finalCmd = cmd;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    String output[];
                    taAddText(USER + "@" + HOST + ": " + finalCmd);
                    output = remotecommand(HOST,USER,PASS, finalCmd);
                    for (String out:output)if (out != null)taAddText(out);
                    Platform.runLater(() -> tf_cmd.setText(""));
                    Platform.runLater(() -> progress.setVisible(false));
                    timer.cancel();
                }
            };
            timer.schedule(task,0);
        }

        
    }
    
    @FXML
    private void actionDelete(ActionEvent event){
        Platform.runLater(() -> {
            ta_main.setText("");
            tf_cmd.setText("");
            
        });
    }
    
    private void sendcmd(String cmd){
        Platform.runLater(() -> progress.setVisible(true));
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                String output[];
    
                taAddText(USER + "@" + HOST + ": " + cmd);
                output = remotecommand(HOST, USER, PASS, cmd);
                for (String out:output)if (out != null)taAddText(out);
                Platform.runLater(() -> tf_cmd.setText(""));
                Platform.runLater(() -> progress.setVisible(false));
                timer.cancel();
            }
        };
        timer.schedule(task,0);
    }
    
    @FXML
    private void actionRestart(ActionEvent event) {
        sendcmd("sudo reboot");
    
    }
    
    
    @FXML
    private void actionShutdown(ActionEvent event){
        sendcmd("sudo shutdown -h now");
    }
    
    @FXML
    private void actionUpdate(ActionEvent event){
        sendcmd("sudo apt-get update -y");
    }
    
    @FXML
    private void actionUpgrade(ActionEvent event){
        sendcmd("sudo apt-get upgrade -y");
    }
    
    @FXML
    private void actionAutostart(ActionEvent event){
    
    }

    /**
     * Set properties.
     *
     * @param host the host
     * @param user the user
     * @param pass the pass
     */
    static void setProperties(String host, String user, String pass){
        HOST =host;
        USER = user;
        PASS = pass;
    }
    
}
