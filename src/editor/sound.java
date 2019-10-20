package editor;

import javafx.application.Platform;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;

import static editor.Controller.programSettings;
import static editor.Debugger.printDebug;
import static editor.Debugger.writeerror;

/**
 *
 * FUNKTION DIESER KLASSE:
 * Diese Klasse ist fuer das Verarbeiten von Sounddateien verantwortlich
 *
 */

class sound {

    private File soundfile;
    private Clip clip;
    private boolean started = false;
    private static File tempfile;
    private String Type;

    //Constuctor <- Neue Sounddatei kreieren
	sound(File Soundfile,String type){
        this.soundfile = Soundfile;
        this.Type = type;
        this.started = false;
    }

//FUNKTIONEN

    //Sounddatei abspielen
    void play() {
        tempfile = this.soundfile;
        try {
            //Wenn Abspielen des Typs erlaubt durch Einstellungen
            if ((this.Type.equals("music") && programSettings.getProperty("music").equals("true"))
                    || (this.Type.equals("sound") && programSettings.getProperty("sounds").equals("true"))
                    ) {

                Platform.runLater(() -> {
                    try {

                        //Sound laden
                        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(tempfile);  //Sound als Stream oeffnen
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(audioInputStream);    //Stream Buffer erstellen
                        AudioFormat af = audioInputStream.getFormat();  //AudioFormat laden
                        int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());   //Sounddatenlaenge bestimmen
                        byte[] audio = new byte[size];  //Variable zum speichern der Sounddatei erstellen
                        DataLine.Info info = new DataLine.Info(Clip.class, af, size);   //Soundinformationen zwischenspeichern
                        bufferedInputStream.read(audio, 0, size);   //Sounddatei gebuffert einlesen
                        clip = (Clip) AudioSystem.getLine(info);    //Eingelesene Sounddatei als "Clip" speichern
                        clip.open(af, audio, 0, size);  //Clip oeffnen mit gegebenen Informationen

                        //Sounddatei abspielen
                        started = true;
                        if (this.Type.equals("music")) clip.loop(-1);    //Wenn Hintergrundmusic -> Unendlich Loopen
                        clip.start();
                        printDebug("INFO", "Start playing \"" + tempfile.getName() + "\"");
                    } catch (Exception e) {
                        //Aus Error Reagieren
                        writeerror(e);
                        printDebug("WARNING", "Soundfile \"" + tempfile.getPath() + "\" cant be played!");
                    }
                });
            }
        }catch(NullPointerException e){
            printDebug("ERROR","Failed to play Sound \"" + tempfile.getName() + "\"!");
            writeerror(e);
        }
    }

    //Abspielen Stoppen
    @SuppressWarnings("unused")
    void stop(){
        //Wenn Datei abgespielt wird, stoppen
        if (this.started) {
            printDebug("INFO", "Stoped playing \"" + this.soundfile.getName() + "\"");
            this.clip.stop();
            this.started = false;
        }
    }

//GETTER UND SETTER (Selbsterklaerend)

    //SoundDatei zurueckgeben
    public File getSoundfile() {
        return this.soundfile;
    }

    //Zurueckgeben ob Sounddatei nach abgespielt wird
    @SuppressWarnings("unused")
    boolean isPlaying(){
        if (this.started) return this.clip.isActive(); else return false;
    }

    //Sounddatei setzen bzw. aendern
    @SuppressWarnings("unused")
    void setSoundfile(File Soundfile) {
        this.soundfile = Soundfile;
    }
    
    
}