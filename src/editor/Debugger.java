package editor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import static editor.Controller.detaillog;
import static editor.Controller.logfile;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 02.06.17.
 */
public class Debugger {

    /**
     * Sout boolean.
     *
     * @param output the output
     * @return the boolean
     */
//Gibt Informationen in der Console aus und schreibt diese zusaetzlich in einen Detaillierten Log -> Fuer Offline Debugging
    static boolean Sout(String output){
        System.out.println(output);
        boolean success = true;
        try {
            FileWriter fw = new FileWriter(detaillog , true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss - ").format(new Date().getTime()) + output + "\n");
            bw.close();
        }catch(Exception ignored){
            success = false;
        }
        return success;
    }

    /**
     * Print debug boolean.
     *
     * @param type    the type
     * @param message the message
     * @return the boolean
     */
    static boolean printDebug(String type, String message){
        return Sout("DEBUG -> " + type + ": " + message);
    }

    //Schreibt eine Logdatei, welche Informationen zu evtl. auftretenden Fehlern enthaelt
    private static boolean writetolog(String output){
        Sout(output);   //Consolenausgabe
        boolean success = true;
        //Versuchen die Informationen in die Logdatei zu schreiben + Datum und Uhrzeit
        try {
            FileWriter fw = new FileWriter(logfile, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new SimpleDateFormat("YYYY-MM-dd_HH:mm:ss - ").format(new Date().getTime()) + output + "\n");
            bw.close();
        }catch(Exception ignored){
            success = false;
        }
        return success;
    }

    /**
     * Writeerror.
     *
     * @param e the e
     */
//Fehlermeldungen in Log schreiben und ggf. Report Senden
    static void writeerror(Exception e){
        //Entstandene Fehler abfangen und in LogDatei schreiben
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        writetolog(sw.toString());

    }
}
