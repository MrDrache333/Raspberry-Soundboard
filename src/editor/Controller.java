package editor;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static editor.Debugger.*;
import static editor.Raspberry.uploadFile;
import static editor.Scanner.getSubnet;

/**
 * The type Controller.
 */
public class Controller {

    /**
     * The constant logfile.
     */
//Logdateien fuer Debuginformationen
    static File logfile = new File("soundboard.log");
    /**
     * The Detaillog.
     */
    static File detaillog = new File("soundboard_detail.log");

    //Version
    //Hauptversion, Unterversion
    private static final String[] VERSION = {"00","09"};
    /**
     * The Version astext.
     */
    static final String VERSION_ASTEXT = VERSION[0] + "." + VERSION[1];
    /**
     * The Version asnumber.
     */
    static final long VERSION_ASNUMBER = Long.parseLong(VERSION[0] + "" + VERSION[1]);

    /**
     * The constant programSettings.
     */
//Variablen zum Speichern von vielen Daten
    //Programmeinstellungen und ButtonSounds
    static final Settings programSettings = new Settings(new File("config.properties"));
    private static Settings buttonSettings = new Settings(new File("buttons.properties"));
    //Verfuegbare Profile und Aktives Profil
    private Profile profiles[] = new Profile[3];
    private static int ACTIVEPROFILE = 0;
    private File PLACEHOLDER = new File("PLACEHOLDER.wav");
    private File lastPath = null;

    //Stage zum oeffnen vom FileChooser
    private Stage stage;
    
    //Raspberry
    private boolean pauseCheckPiStatus = false;
    private boolean CONNECTED = false;

    //FXML Objekte fuer GUI
    @FXML
    private Label l_LCD_LINE1, l_LCD_LINE2, l_LED1, l_LED2, l_LED3, l_LED4, l_raspiStatus,l_hover,l_version;
    @FXML
    private TextField tf_user, tf_ip;
    @FXML
    private PasswordField pf_pass;
    @FXML
    private RadioButton rb_test,rb_change;
    @FXML
    private javafx.scene.control.Button btn_more;
    
    @FXML
    private ImageView
            image_Taster1, image_Taster2, image_Taster3, image_Taster4, image_Taster5, image_Taster6,
            image_Taster7, image_Taster8, image_Taster9, image_Taster10, image_Taster11, image_Taster12,
            image_Taster13, image_Taster14, image_Taster15, image_Taster16, image_Taster17, image_Taster18,
            image_Taster19, image_Taster20, image_Taster21, image_Taster22, image_Taster23, image_Taster24,
            image_Taster25, image_Taster26, image_Taster27;
    /**
     * The constant Buttons.
     */
//Buttons als Array damit einfacher aenderungen uebernommen werden koennen
    static Button[] Buttons = new Button[27];
    
    @FXML
    private javafx.scene.control.Button btn_apply,btn_save,btn_find;
    
    private String PI_HOST,PI_USER,PI_PASS;

    /**
     * The Finder olist.
     */
//Finder
    static ObservableList<String> finder_olist = FXCollections.observableArrayList();
    @FXML
    private ListView<String> finder_list;
    @FXML
    private ProgressIndicator finder_progress;

    @FXML
    private ImageView image_BACKGROUND, image_LCD;

    /**
     * The constant BUTTONIMAGE.
     */
//Benoetigte Bilder
    static File BUTTONIMAGE = new File("images/BUTTONRELEASED.png");
    /**
     * The Buttonpressedimage.
     */
    static File BUTTONPRESSEDIMAGE = new File("images/BUTTONPRESSED.png");
    /**
     * The Buttonimage profile.
     */
    static File BUTTONIMAGE_PROFILE = new File("images/BUTTONRELEASED_PROFILE.png");
    /**
     * The Buttonpressedimage profile.
     */
    static File BUTTONPRESSEDIMAGE_PROFILE = new File("images/BUTTONPRESSED_PROFILE.png");
    /**
     * The Backgroundimage.
     */
    static File BACKGROUNDIMAGE = new File("images/BACKGROUND.jpg");
    private static File LCDIMAGE = new File("images/LCD.png");

    //Initialisierungs Funktion, welche aufgerufen wird, bevor die GUI angezeigt wird
    @FXML
    private void initialize(){

        try {
            password.initialize();
        } catch (UnsupportedEncodingException e) {
            writeerror(e);
        } catch (NoSuchAlgorithmException e) {
            writeerror(e);
        }
        Settings.checkdefaults();   //Standardeinstellungen laden
        try {
            PI_HOST = programSettings.getProperty("lasthost");
            PI_USER = programSettings.getProperty("lastuser");
            PI_PASS = password.entlock(programSettings.getProperty("lastuserpass"));
            if (! Objects.equals(PI_HOST, ""))tf_ip.setText(PI_HOST);
            if (! Objects.equals(PI_USER, ""))tf_user.setText(PI_USER);
            if (! Objects.equals(PI_PASS, ""))pf_pass.setText(PI_PASS);
        }catch(Exception ignored){
        }
        //Versuchen die Bilder zu uebernehmen und die Buttons zu erstellen
        try{
            image_BACKGROUND.setImage(new Image(BACKGROUNDIMAGE.toURI().toURL().toString()));
            image_BACKGROUND.setOnMouseClicked(clearGui);
            image_LCD.setImage(new Image(LCDIMAGE.toURI().toURL().toString()));
            createButtonArray();
            applyButtons();
        }catch(Exception ignored){}

        //Profile anlegen
        for(int i = 0; i < profiles.length;i++){
            profiles[i] = new Profile();
        }
    
        image_BACKGROUND.setOnMouseMoved(hoverHandler);
        //Buttons aus properties laden und uebernehmen
        loadButtons();
        profiles[0].load();
        
        createPiCheckTimer();
        
        finder_list.setItems(finder_olist);
        finder_list.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        finder_list.setOnMouseClicked(event -> {
            if (!finder_progress.isVisible())
            try{
                String Host = finder_list.getSelectionModel().getSelectedItem();
                if (Host != null){
                    if (Host.contains("-")){
                        Host = Host.substring(0,Host.lastIndexOf("-") - 1);
                    }
                    tf_ip.setText(Host);
                    tf_user.setText("");
                    pf_pass.setText("");
                }
                finder_list.setVisible(false);
                finder_olist.remove(0,finder_olist.size());
            }catch(Exception ignored){}
        });
        
        //Version
        l_version.setText(VERSION_ASTEXT);

        if (!programSettings.getProperty("showedversioninfo").equals(VERSION_ASTEXT))AlertDialog.showVersion(Alert.AlertType.CONFIRMATION,common.VersionInfo,"Changelog für Version " + Controller.VERSION_ASTEXT);
        
    }

    @FXML
    private void showChangelog(){
        AlertDialog.showVersion(Alert.AlertType.CONFIRMATION,common.VersionInfo,"Changelog für Version " + Controller.VERSION_ASTEXT);
    }

    @FXML
    private void showProgramInfo(){
        AlertDialog.showAlert(Alert.AlertType.INFORMATION,common.programInfo,"Über Soundboard Pi");
    }

    //Buttons in Array speichern
    private void createButtonArray() throws MalformedURLException {
        Buttons[0] = new Button(image_Taster1);
        Buttons[1] = new Button(image_Taster2);
        Buttons[2] = new Button(image_Taster3);
        Buttons[3] = new Button(image_Taster4);
        Buttons[4] = new Button(image_Taster5);
        Buttons[5] = new Button(image_Taster6);
        Buttons[6] = new Button(image_Taster7);
        Buttons[7] = new Button(image_Taster8);
        Buttons[8] = new Button(image_Taster9);
        Buttons[9] = new Button(image_Taster10);
        Buttons[10] = new Button(image_Taster11);
        Buttons[11] = new Button(image_Taster12);
        Buttons[12] = new Button(image_Taster13);
        Buttons[13] = new Button(image_Taster14);
        Buttons[14] = new Button(image_Taster15);
        Buttons[15] = new Button(image_Taster16);
        Buttons[16] = new Button(image_Taster17);
        Buttons[17] = new Button(image_Taster18);
        Buttons[18] = new Button(image_Taster19);
        Buttons[19] = new Button(image_Taster20);
        Buttons[20] = new Button(image_Taster21);
        Buttons[21] = new Button(image_Taster22);
        Buttons[22] = new Button(image_Taster23);
        Buttons[23] = new Button(image_Taster24);
        Buttons[24] = new Button(image_Taster25);
        Buttons[25] = new Button(image_Taster26);
        Buttons[26] = new Button(image_Taster27);

        //Profile zuweisen
        Buttons[24].setProfileButton(true);
        Buttons[24].setProfileIndex(0);
        Buttons[25].setProfileButton(true);
        Buttons[25].setProfileIndex(1);
        Buttons[26].setProfileButton(true);
        Buttons[26].setProfileIndex(2);

        //Standardkonfiguration fuer Buttons uebernehmen
        for (Button button:Buttons){
            //Code ausfuehren wenn auf einen Button geklickt wird
            button.getImageView().setOnMouseClicked(clickHandler);
            button.getImageView().setOnMouseMoved(hoverHandler);
            button.setPressed(false);
        }
    }

    //Daten aus Button Array auf die Buttons uebertragen
    private void applyButtons(){
        image_Taster1 = Buttons[0].getImageView();
        image_Taster2 = Buttons[1].getImageView();
        image_Taster3 = Buttons[2].getImageView();
        image_Taster4 = Buttons[3].getImageView();
        image_Taster5 = Buttons[4].getImageView();
        image_Taster6 = Buttons[5].getImageView();
        image_Taster7 = Buttons[6].getImageView();
        image_Taster8 = Buttons[7].getImageView();
        image_Taster9 = Buttons[8].getImageView();
        image_Taster10 = Buttons[9].getImageView();
        image_Taster11 = Buttons[10].getImageView();
        image_Taster12 = Buttons[11].getImageView();
        image_Taster13 = Buttons[12].getImageView();
        image_Taster14 = Buttons[13].getImageView();
        image_Taster15 = Buttons[14].getImageView();
        image_Taster16 = Buttons[15].getImageView();
        image_Taster17 = Buttons[16].getImageView();
        image_Taster18 = Buttons[17].getImageView();
        image_Taster19 = Buttons[18].getImageView();
        image_Taster20 = Buttons[19].getImageView();
        image_Taster21 = Buttons[20].getImageView();
        image_Taster22 = Buttons[21].getImageView();
        image_Taster23 = Buttons[22].getImageView();
        image_Taster24 = Buttons[23].getImageView();
        image_Taster25 = Buttons[24].getImageView();
        image_Taster26 = Buttons[25].getImageView();
        image_Taster27 = Buttons[26].getImageView();
    }

    //Funktion zum Waehlen von einer Datei in einem Seperatem Fenster
    private File chooseFile(){
        //Uebersetzten der Befehle sollte beim verstaendnis reichen
        FileChooser fileChooser = new FileChooser();
        File soundfolder = new File("sounds");
        if (lastPath != null) {
            if (lastPath.exists()) {
                soundfolder = new File(lastPath.getAbsolutePath().replace(lastPath.getName(),""));
            }
        }

        if (!soundfolder.exists()) //noinspection ResultOfMethodCallIgnored
            soundfolder.mkdirs();
        fileChooser.setInitialDirectory(soundfolder);
        fileChooser.setTitle("Wähle eine Sounddatei");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Wav Sounddatei", "*.wav")
        );
        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            try {
                lastPath = selectedFile;
                return selectedFile.getAbsoluteFile();
            } catch (Exception e) {
                writeerror(e);
            }
        }
    
        return null;
    }

    private EventHandler clearGui = event -> {
        if (finder_list.isVisible()){
            if (finder_progress.isVisible())finder_progress.setVisible(false);
            finder_olist.clear();
            finder_list.setVisible(false);
        }
    };

    @FXML
    private void findAction() throws InterruptedException {
        if (!finder_list.isVisible()) {
            Platform.runLater(() -> {
                finder_list.setVisible(true);
                btn_find.setDisable(true);
                finder_progress.setVisible(true);
            });
            ArrayList<String> Adresses = new ArrayList<>();
            for (String IP : Scanner.getIpAdresses()) {
                if (!IP.equals("127.0.0.1")) Adresses.add(getSubnet(IP));
            }
            for (String StartAddr : Adresses) {
                printDebug("INFO","Scanning Subnet: " + StartAddr + 0 + " for SSH Devices...");
                for (int host = 1; host < 255; host++) {
                    String Host = "";
                    if (host < 100)
                        Host += "0";
                    if (host < 10)
                        Host += "0";
                    Scanner curr = new Scanner(StartAddr + Host + host, 22);
                    Thread th = new Thread(curr);
                    th.start();
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        finder_progress.setVisible(false);
                        Collections.sort(finder_olist);
                        finder_list.setItems(finder_olist);
                        finder_list.refresh();
                        btn_find.setDisable(false);
                    });

                }
            };
            timer.schedule(task, 7000);
        }else {
            finder_list.setVisible(false);
            if (finder_progress.isVisible())finder_progress.setVisible(false);
            finder_olist.clear();
        }
    }

    //Wenn auf einen Button geklickt wird
    private EventHandler clickHandler  = new EventHandler() {
        @Override
        public void handle(Event event) {
            if (finder_list.isVisible()){
                if (finder_progress.isVisible())finder_progress.setVisible(false);
                finder_olist.clear();
                finder_list.setVisible(false);
            }
            //Herausfinden vom INDEX des Buttons
            String source = event.getSource().toString();
            if (source.contains("image_Taster")) {
                int stop = source.indexOf(",");
                int start = source.indexOf("image_Taster") + "image_Taster".length();
                String number = source.substring(start, stop);
                int ButtonIndex = Integer.parseInt(number) - 1;
                //Button als gedrückt festlegen
                try {
                    Buttons[ButtonIndex].setPressed(true);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                //Wenn Profil gewechselt wird
                if (Buttons[ButtonIndex].isProfileButton()) {
                    //Wenn Profil nicht aktives Profil
                    if (Buttons[ButtonIndex].getProfileIndex() != ACTIVEPROFILE) {
                        //Sytle der kleinen "Lampen" festlegen
                        if (ACTIVEPROFILE == 0) {
                            l_LED1.setStyle("-fx-background-color: GREEN; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                        } else if (ACTIVEPROFILE == 1) {
                            l_LED2.setStyle("-fx-background-color: GREEN; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                        } else if (ACTIVEPROFILE == 2)
                            l_LED3.setStyle("-fx-background-color: GREEN; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
    
                        if (Buttons[ButtonIndex].getProfileIndex() == 0) {
                            l_LED1.setStyle("-fx-background-color: LIME; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                        } else if (Buttons[ButtonIndex].getProfileIndex() == 1) {
                            l_LED2.setStyle("-fx-background-color: LIME; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                        } else if (Buttons[ButtonIndex].getProfileIndex() == 2) {
                            l_LED3.setStyle("-fx-background-color: LIME; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                        }
                        //Neues Profil laden und uebernehmen
                        profiles[Buttons[ButtonIndex].getProfileIndex()].load();
                        ACTIVEPROFILE = Buttons[ButtonIndex].getProfileIndex();
                    }
                } else {
                    if (rb_test.isSelected()) {
                        try {
                            if (Buttons[ButtonIndex].isplaying()) {
                                Buttons[ButtonIndex].stop();
                            } else
                                Buttons[ButtonIndex].play();
                        }catch(Exception ignored){}
                    } else if (rb_change.isSelected()) {
                        File newsoundfile = chooseFile();
                        if (newsoundfile != null)
                        if (newsoundfile.exists()) {
                            Buttons[ButtonIndex].setSound(
                                    new sound(newsoundfile, "sound")
                            );
                            profiles[ACTIVEPROFILE].setSound(
                                    new sound(newsoundfile,"sound"),
                                    ButtonIndex
                            );
                            printDebug("INFO", "Changed Soundfile to \"" + newsoundfile.getName() + "\" on Button " + (ButtonIndex + 1));
    
                        }
                    }
                }
            }
        }
    };
    
    private int lastIndex = 999;
    private EventHandler<MouseEvent> hoverHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent event) {
            //Herausfinden vom INDEX des Buttons
            String source = event.getSource().toString();
            if (source.contains("image_Taster")) {
                int stop = source.indexOf(",");
                int start = source.indexOf("image_Taster") + "image_Taster".length();
                String number = source.substring(start, stop);
                int ButtonIndex = Integer.parseInt(number) - 1;
    
    
                Platform.runLater(() -> {
                    if (lastIndex != ButtonIndex) {
                        if (! Buttons[ButtonIndex].isProfileButton()) {
                            try {
                                l_hover.setText(Buttons[ButtonIndex].getSound().getSoundfile().getName().replace(".wav","").replace("_"," "));
                            } catch (Exception e) {
                                l_hover.setText("Nicht belegt");
                            }
                        } else
                            l_hover.setText("");
                        l_hover.setPrefWidth(l_hover.getText().length() * 11);
                        double newX = event.getSceneX() - l_hover.getWidth() / 2;
                        if (newX < 5)newX = 5;
                        l_hover.setLayoutX(newX);
                        l_hover.setLayoutY(event.getSceneY() + 15);
                        l_hover.setVisible(true);
    
                    } else {
                        double newX = event.getSceneX() - l_hover.getWidth() / 2;
                        if (newX < 5)newX = 5;
                        l_hover.setLayoutX(newX);
                        l_hover.setLayoutY(event.getSceneY() + 15);
    
                    }
                });
    
    
            } else{
                lastIndex = 999;
                if (l_hover.isVisible())Platform.runLater(() -> l_hover.setVisible(false));
            }
        }
    };

    @FXML
    private void applyAction(ActionEvent event) throws IOException {
    
    
        String IP = "", USER = "", PASS = "";
        boolean stop = false;
        try {
            stop = ! checkpistatus();
        } catch (Exception e) {
            writeerror(e);
        }
        try {
            IP = tf_ip.getText();
            USER = tf_user.getText();
            PASS = pf_pass.getText();
        } catch (Exception e) {
            stop = true;
            Sout("");
        }
    
        if (! stop) {
            
            pauseCheckPiStatus = true;
            Platform.runLater(() -> btn_apply.setDisable(true));
            Platform.runLater(() -> l_raspiStatus.setText("Übertrage Daten"));
            l_LED4.setStyle("-fx-background-color: ORANGE; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
    
            Timer timer = new Timer();
            String finalIP = IP;
            String finalUSER = USER;
            String finalPASS = PASS;
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    int errorcount = 0;
                    for (int profile = 0; profile < profiles.length; profile++) {
                        for (int i = 0; i < profiles[profile].getSounds().length - 3; i++) {
                            String path;
                            try {
                                path = profiles[profile].getSound(i).getSoundfile().getAbsolutePath();
                            } catch (Exception e) {
                                path = null;
                            }
                            if (path != null) {
                                int finalProfile = profile;
                                int finalI = i;
                                Platform.runLater(() -> l_raspiStatus.setText("Übertrage Daten (" + ((finalI + 1) + (24 * finalProfile)) + " / " + (73) + ")"));
                                if (!uploadFile(finalIP, finalUSER, finalPASS, new File(path),"/home/pi/soundboard/sounds")) errorcount++;
                            }
                        }
                    }
    
                    String sounds;
    
                    StringBuilder soundnamesBuilder = new StringBuilder("soundnames = [\n");
                    StringBuilder soundsBuilder = new StringBuilder("sounds = [\n");
                    for (Profile profile1 : profiles) {
                        for (int i = 0; i < (profile1.getSounds().length - 3); i++) {
                            String tempname;
                            try {
                                tempname = profile1.getSound(i).getSoundfile().getName();
                            } catch (Exception e) {
                                tempname = null;
                            }
                            if (tempname != null) {
                                soundsBuilder.append("  pygame.mixer.Sound(\"").append(tempname).append("\"),\n");
                                soundnamesBuilder.append("  \"").append(makeLCDString(tempname.substring(0, tempname.length() - 4))).append("\",\n");
                            } else {
                                soundsBuilder.append("  pygame.mixer.Sound(\"").append(PLACEHOLDER.getName()).append("\"),\n");
                                soundnamesBuilder.append("  \"").append(makeLCDString(PLACEHOLDER.getName().substring(0, PLACEHOLDER.getName().length() - 4))).append("\",\n");
                            }
                        }
                    }
                    sounds = soundsBuilder.toString();
                    String soundnames = soundnamesBuilder.toString();
                    sounds = sounds.substring(0, sounds.length() - 2) + "\n]";
                    soundnames = soundnames.substring(0, soundnames.length() - 2) + "\n]";
                    try {
                        FileWriter fw = new FileWriter("soundboard.py");
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(common.python_header + "\n" + sounds + "\n" + soundnames + "\n" + common.python_main);
                        bw.close();
    
                        Platform.runLater(() -> l_raspiStatus.setText("Übertrage Daten (" + 73 + " / " + (73) + ")"));
                        uploadFile(finalIP, finalUSER, finalPASS, new File("soundboard.py"),"/home/pi/soundboard/sounds");
    
                    } catch (Exception e) {
                        errorcount++;
                    }
    
                    if (errorcount != 0) {
                        Platform.runLater(() -> l_raspiStatus.setText("FEHLER BEI ÜBERTRAGUNG!"));
                        l_LED4.setStyle("-fx-background-color: RED; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                    } else {
                        Platform.runLater(() -> l_raspiStatus.setText("Übertragung erfolgreich"));
                        Raspberry.remotecommand(finalIP,finalUSER,finalPASS,"sudo reboot");
                        l_LED4.setStyle("-fx-background-color: LIME; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Platform.runLater(() -> btn_apply.setDisable(false));
                    pauseCheckPiStatus = false;
                    timer.cancel();
                }
            };
            timer.schedule(task, 1000);
        }
    }
    private String makeLCDString(String string){
        int finalLength = 16;
        int placeholder = (finalLength - string.length()) / 2;
        while(placeholder > 0){
            string = " " + string;
            placeholder--;
        }
        return string;
    }
    
    @FXML
    private void opensettings(ActionEvent event){
            try {
                //Clicksound spielen
                Controller_Settings.setProperties(PI_HOST,PI_USER,PI_PASS);
                //Neues Fenster generieren und anzeigen <- Verarbeitung uebernimmt die Klasse Controller_settings (in fxml Datei festgelegt)
                stage = new Stage();
                Parent root;
                root = FXMLLoader.load(getClass().getResource("settings.fxml"));
                stage.setTitle("Einstellungen");
                Scene scene = new Scene(root, 680, 400);
                stage.setResizable(false);
                stage.setOnCloseRequest(event1 -> {
                    //Log schreiben
                    if (!programSettings.saveProperties()) {
                        Sout("DEBUG -> ERROR: Faild to Store Settings");
                    } else
                        Sout("DEBUG -> INFO: Settings stored in " + programSettings.getFile().getAbsolutePath());
                    stage.close();
                });
                scene.setOnKeyPressed(event2 -> {
                    if (event2.getCode() == KeyCode.TAB){

                    }
                });
                stage.initModality(Modality.APPLICATION_MODAL); //Fenser im Focus einfangen
                stage.setScene(scene);
                stage.show();
            }catch (Exception e){
                writeerror(e);
            }
    }
    
    @FXML
    private void saveAction(ActionEvent event){
        buttonSettings.resetProperties();
        int savecount = 0;
        for (int profile = 0; profile < profiles.length; profile++) {
            for (int i = 0; i < profiles[profile].getSounds().length; i++) {
                String path;
                try {
                    path = profiles[profile].getSound(i).getSoundfile().getAbsolutePath();
                } catch (Exception e) {
                    path = null;
                }
                if (path != null) {
                    buttonSettings.addProperty("btn" + i + "p" + profile + "sound", path);
                    savecount++;
                }
            }
        }
        if (buttonSettings.saveProperties()){
            printDebug("INFO", "Saved " + savecount + " Button sounds in \"" + buttonSettings.getFile().getName() + "\"");
        }else
            printDebug("ERROR", "Could not save Button sounds!");
    }
    private boolean loadButtons(){
        try{
            buttonSettings.loadProperties();
            int loadcount = 0;

            for (int profile = 0; profile < profiles.length; profile++) {
                for (int i = 0; i < profiles[profile].getSounds().length; i++) {
                    String path;
                    try {
                        path = buttonSettings.getProperty("btn" + i + "p" + profile + "sound");
                    } catch (Exception e) {
                        path = null;
                    }
                    if (path != null) if (new File(path).exists()) {
                        profiles[profile].setSound(new sound(new File(path), "sound"),i);
                        loadcount++;
                    } else
                        printDebug("WARNING", "Could not Load File \"" + new File(path).getName() + "\" from " + new File(path).getAbsolutePath());
                }
            }
            printDebug("INFO","Loaded " + loadcount + " Sounds");
        }catch(Exception e){
            return false;
        }
        return true;
    }
    
    private boolean checkpistatus() throws Exception {
        String IP = "",USER = "",PASS = "";
        boolean stop = false;
        try{
            IP = tf_ip.getText();
            USER = tf_user.getText();
            PASS = pf_pass.getText();
        }catch(Exception e){
            stop = true;
        }
        stop = pauseCheckPiStatus || stop;
        Session session = null;
        if (stop && !CONNECTED)if (stage != null)
            if (stage.isShowing())Platform.runLater(() -> stage.close());
        
        if (!stop)
        try{
            if (!CONNECTED && USER.length() >= 2 && PASS.length() >= 2) {
                l_LED4.setStyle("-fx-background-color: ORANGE; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
                Platform.runLater(() -> l_raspiStatus.setText("Verbinde..."));
            }
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");

            JSch jsch = new JSch();
    
            session = jsch.getSession(USER, IP, 22);
            session.setPassword(PASS);
            session.setConfig(config);
            session.setTimeout(1500);
            session.connect();
            
        }catch(Exception e){
            l_LED4.setStyle("-fx-background-color: RED; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
            Platform.runLater(() -> l_raspiStatus.setText("Nicht Verbunden"));
            CONNECTED = false;
            btn_find.setDisable(false);
            btn_more.setDisable(true);
            stop = true;
        }finally {
            assert session != null;
            session.disconnect();
        }
        if (!stop){
            l_LED4.setStyle("-fx-background-color: LIME; -fx-border-color: BLACK; -fx-border-radius: 90; -fx-background-radius: 90;");
            Platform.runLater(() -> l_raspiStatus.setText("Verbunden"));
            CONNECTED = true;
            btn_find.setDisable(true);
            if (! Objects.equals(PI_HOST, IP) || ! Objects.equals(PI_USER, USER) || ! Objects.equals(PI_PASS, PASS)) {
                PI_HOST = IP;
                PI_USER = USER;
                PI_PASS = PASS;
                programSettings.setProperty("lasthost",PI_HOST);
                programSettings.setProperty("lastuser",PI_USER);
                programSettings.setProperty("lastuserpass",password.lock(PI_PASS));
            }
            btn_more.setDisable(false);
            return true;
        }else
            return false;
    }
    
    private void createPiCheckTimer(){
        
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    checkpistatus();
                } catch (Exception e) {
                    writeerror(e);
                }
            }
        };
        timer.schedule(task,0,2000);

        
    }
    
    @FXML
    private void mode_changeAction(ActionEvent event) {
    }
    
    @FXML
    private void mode_testAction(ActionEvent event) {
    }

}
