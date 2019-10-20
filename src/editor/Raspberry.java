package editor;

import com.jcraft.jsch.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import static editor.Debugger.printDebug;
import static editor.Debugger.writeerror;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 02.06.17.
 */
public class Raspberry {

    /**
     * Remotecommand string [ ].
     *
     * @param HOST    the host
     * @param USER    the user
     * @param PASS    the pass
     * @param command the command
     * @return the string [ ]
     */
    static String[] remotecommand(String HOST,String USER, String PASS, String command){

        Session session;
        String output[] = new String[1024];

        try{
            JSch jsch = new JSch();
            session = jsch.getSession(USER,HOST,22);
            session.setPassword(PASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(1500);
            session.connect();

            ChannelExec channel =(ChannelExec) session.openChannel("exec");
            BufferedReader in=new BufferedReader(new InputStreamReader(channel.getInputStream()));
            BufferedReader err=new BufferedReader(new InputStreamReader(channel.getErrStream()));
            channel.setCommand(command);
            channel.connect();

            String msg;
            int index = 0;
            while((msg=in.readLine())!=null && index < 1024){
                output[index] = msg;
                index++;
            }
            while((msg=err.readLine())!=null && index < 1024){
                output[index] = msg;
                index++;
            }

            channel.disconnect();
            session.disconnect();
        }catch(Exception ex){
            writeerror(ex);
            printDebug("ERROR","Failed to Execute Remote Command \"" + command + "\" on " + HOST + ":22 with User \"" + USER + "\"");
            return output;
        }
        return output;
    }

    /**
     * Upload file boolean.
     *
     * @param HOST     the host
     * @param USER     the user
     * @param PASS     the pass
     * @param FILE     the file
     * @param DESTPATH the destpath
     * @return the boolean
     */
    static boolean uploadFile(String HOST, String USER, String PASS, File FILE, String DESTPATH){
        int SFTPPORT = 22;
        Session session;
        Channel channel;
        ChannelSftp channelSftp;

        try{
            JSch jsch = new JSch();
            session = jsch.getSession(USER, HOST,SFTPPORT);
            session.setPassword(PASS);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            channel = session.openChannel("sftp");
            channel.connect();
            channelSftp = (ChannelSftp)channel;
            channelSftp.cd(DESTPATH);
            channelSftp.put(new FileInputStream(FILE), FILE.getName());
        }catch(Exception ex){
            writeerror(ex);
            printDebug("ERROR","Failed to Upload File \"" + FILE.getName() + "\" to " + HOST + "\"");
            return false;
        }
        return true;
    }

}
