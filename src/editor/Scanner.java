package editor;

import javafx.application.Platform;
import jcifs.netbios.NbtAddress;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * Project: Soundboard
 * Package: editor
 * Created by keno on 02.06.17.
 */
public class Scanner implements Runnable{
    
    private int Port;
    private String Host;

    /**
     * Instantiates a new Scanner.
     *
     * @param host the host
     * @param port the port
     */
    Scanner(String host, int port){
        Host = host;
        Port = port;
    }
    
    @Override
    public void run() {
        try {
            Socket socket = new Socket();
            InetSocketAddress addr = new InetSocketAddress(Host, Port);
            socket.connect(addr, 5000);
            socket.close();
            String hostname = "";
            try {
                NbtAddress nbts[] = NbtAddress.getAllByAddress(Host);
                hostname = nbts[0].getHostName();
            }catch(Exception ignored){}
            if (hostname.length() == 0)
                Platform.runLater(() -> Controller.finder_olist.add(Host));
            else {
                String finalHostname = hostname;
                Platform.runLater(() -> Controller.finder_olist.add(Host + " - " + finalHostname));
            }
        } catch (Exception ignored) {}
    }


    /**
     * Gets subnet.
     *
     * @param currentIP the current ip
     * @return the subnet
     */
    static String getSubnet(String currentIP) {
        int firstSeparator = currentIP.lastIndexOf("/");
        int lastSeparator = currentIP.lastIndexOf(".");
        return currentIP.substring(firstSeparator+1, lastSeparator+1);
    }

    /**
     * Get ip adresses array list.
     *
     * @return the array list
     */
    static ArrayList<String> getIpAdresses(){
        ArrayList<String> out = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface i = interfaces.nextElement();
                if (i != null) {
                    Enumeration<InetAddress> addresses = i.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        String hostAddr = address.getHostAddress();
                        int count = 0;
                        for (int c = 0; c < hostAddr.length(); c++) {
                            if (hostAddr.charAt(c) == '.') count++;
                        }
                        if (count == 3)
                            out.add(hostAddr);
                    }
                }
            }
        }catch (Exception e){
            return new ArrayList<>();
        }
        return out;
    }
}
