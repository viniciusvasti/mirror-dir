package com.vas.mirrordir.main;

import com.vas.mirrordir.MirrorThread;
import com.vas.mirrordir.exceptions.NotADirectoryException;
import com.vas.mirrordir.models.FTPCredentials;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinicius
 */
public class Main {

    private static String originPath;
    private static String domain;
    private static String user;
    private static String password;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("### MirrorDir config ###");
        System.out.println("Local directory (e.g. C:\\Vinicius\\Documents\\DirectoryToMirror): ");
        originPath = scanner.next();
        System.out.println("FTP domain (e.g. ftp.drivehq.com): ");
        domain = scanner.next();
        System.out.println("FTP user: ");
        user = scanner.next();
        System.out.println("FTP password: ");
        password = scanner.next();
        System.out.println("### MirrorDir config ###");
        runMirror();
    }

    public static void runMirror() {
        try {
            //MirrorThread mirrorThread = new MirrorThread(10, "C:\\Users\\Vinicius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinicius\\Desktop\\mirrorDestination");
            MirrorThread mirrorThread = new MirrorThread(
                    60,
                    originPath,
                    new FTPCredentials(domain, user, password)
            );
        } catch (NotADirectoryException | IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
