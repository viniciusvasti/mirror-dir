package com.vas.mirrordir.main;

import com.vas.mirrordir.MirrorThread;
import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Vinícius
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        runMirror();
    }
    
    public static void runMirror() {
        try {
            //MirrorThread mirrorThread = new MirrorThread(10, "C:\\Users\\Vinícius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination");
            MirrorThread mirrorThread = new MirrorThread(60, "C:\\Users\\Vinícius\\Desktop\\mirrorOrigin");
        } catch (NotADirectoryException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
