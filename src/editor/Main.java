package editor;

import com.sun.javafx.runtime.SystemProperties;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static editor.Debugger.Sout;
import static editor.Debugger.printDebug;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("Hauptfenster.fxml"));
        primaryStage.setTitle("Soundboard Editor");
        String OS = System.getProperty("os.name");
        String OSVersion = System.getProperty("os.version");
        printDebug("System","Running on " + OS + " " + OSVersion);
        if (OS.contains("Windows")){
            primaryStage.setScene(new Scene(root, 990, 590));
        }else
            primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(event -> {
            Controller.programSettings.saveProperties();
            System.exit(0);
        });
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
