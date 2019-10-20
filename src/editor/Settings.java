package editor;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import static editor.Controller.programSettings;
import static editor.Debugger.printDebug;
import static editor.Debugger.writeerror;

/**
 * Project: Mill
 * Package: sample
 * Created by keno on 14.11.16.
 */
class Settings {

    private static ArrayList<String[]> liste = new ArrayList<>();

    private Properties props;   //Eigenschaften
    private File Savefile;  //Datei zum speichern und laden

    //Constructor
    Settings(File Saveto) {
        this.props = new Properties();
        this.Savefile = Saveto;
    }

    //Einstellungen zuruecksetzen
    void resetProperties() {
        this.props.clear();
    }

    //Einstellungsparameter zurueckgeben
    String getProperty(String key) {
        String out = null;
        try {
            out = password.entlock(this.props.getProperty(password.lock(key)));
        } catch (Exception e) {
            writeerror(e);
        }
        if (out.equals("null")) return "";
        else
            return out;
    }

    //Einstellungs Parameter setzen
    void setProperty(String key, String value) {
        if (value.equals("")) value = "null";
        try {
            this.props.setProperty(password.lock(key), password.lock(value));
        } catch (Exception e) {
            writeerror(e);
        }
    }

    //Einstellungs Parameter hinzufuegen
    void addProperty(String key, String value) {
        if (value.equals("")) value = "null";
        try {
            this.props.put(password.lock(key), password.lock(value));
        } catch (Exception e) {
            writeerror(e);
        }
    }

    //Versuchen, die Einstellungen zu laden und je nach erfolg boolean zurueckgeben
    Boolean loadProperties() {
        Boolean loaded;
        try {
            if (this.Savefile.exists()) {   //Wenn die Datei existiert -> Mithilfe von Properties Einstellungen laden
                InputStream fis = new FileInputStream(this.Savefile);
                this.props.loadFromXML(fis);
                fis.close();
                loaded = true;
            } else
                loaded = false;
        } catch (Exception e) {
            loaded = false;
        }
        //Debugging
        if (!loaded) printDebug("ERROR", "Faild to Load Settings in " + this.Savefile.getAbsolutePath());
        return loaded;
    }

    //Einstellungen speichern
    boolean saveProperties() {
        try {   //Versuchen, einstellungen zu speichern und dementsprechend boolean zurueckgeben
            OutputStream fos = new FileOutputStream(this.Savefile);
            this.props.storeToXML(fos, "");
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //Einstellungen speicherDatei zurueckgeben
    File getFile() {
        return this.Savefile;
    }

//STATIC FUNKTIONS

    //Programeinstellungen auf Fehler ueberpruefen
    static void checkdefaults() {

        try {
            programSettings.loadProperties();
        } catch (Exception ignored) {
        }

        //Standardeinstellungen
        liste.add(new String[]{"sounds", "true"});
        liste.add(new String[]{"music", "true"});
        liste.add(new String[]{"autoload", "true"});
        liste.add(new String[]{"lasthost","null"});
        liste.add(new String[]{"lastuser","null"});
        liste.add(new String[]{"lastuserpass","null"});
        liste.add(new String[]{"showedversioninfo","null"});

        for (String[] string : liste) {
            if (string.length == 2)
                try {
                    programSettings.getProperty(string[0]).equals(null);
                    printDebug("INFO", "Settings: Property \"" + string[0] + "\" checked");
                } catch (Exception e) {
                    programSettings.addProperty(string[0],string[1]);
                    printDebug("INFO", "Settings: Property \"" + string[0] + "\" created!");
                }
        }
    }

    //Debugfunktion
    String tostring() {
        String string = "";
        try {
            FileReader fr = new FileReader(this.Savefile);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while ((line = br.readLine()) != null) {
                string += line + "\n";
            }
        } catch (Exception e) {
            string = "null";
        }
        return string;
    }
}