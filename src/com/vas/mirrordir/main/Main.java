package com.vas.mirrordir.main;

import com.vas.mirrordir.MirrorThread;
import com.vas.mirrordir.exceptions.NotADirectoryException;
import com.vas.mirrordir.ftp.FTPServer;
import com.vas.mirrordir.models.Address;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Vinicius
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            String response = "";
            FTPServer ftpServer = new FTPServer();
            ftpServer.connect();
            ftpServer.sendCommand("PWD");
            response = ftpServer.receiveReply();
            System.out.println("Main: "+response);
            
            ftpServer.sendCommand("PASV");
            response = ftpServer.receiveReply();
            System.out.println("Main: "+response);
            
            Address address = ftpServer.getIPandPort(response);
            Socket data = new Socket(address.getIp(), address.getPort());
            ftpServer.sendCommand("NLST");
            response = ftpServer.receiveReply();
            System.out.println("Main: "+response);
            response = ftpServer.receiveReply(data.getInputStream());
            System.out.println("Main: "+response);
            //runMirror();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void runMirror() {
        try {
            //MirrorThread mirrorThread = new MirrorThread(10, "C:\\Users\\Vinicius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinicius\\Desktop\\mirrorDestination");
            MirrorThread mirrorThread = new MirrorThread(60, "C:\\Users\\Vinicius\\Desktop\\mirrorOrigin");
        } catch (NotADirectoryException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
