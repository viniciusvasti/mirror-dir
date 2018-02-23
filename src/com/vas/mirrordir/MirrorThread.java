package com.vas.mirrordir;

import com.vas.mirrordir.controllers.IMirror;
import com.vas.mirrordir.controllers.Mirror;
import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public class MirrorThread extends Thread {

    private final int reflectingFrequency;
    private final IMirror mirror;

    public MirrorThread(int reflectingFrequency, String pathOrigin, String pathDestination) throws NotADirectoryException, IOException {
        super("MirrorDir");
        this.mirror = new Mirror(pathOrigin, pathDestination);
        this.reflectingFrequency = reflectingFrequency;
        this.start();
    }

    @Override
    public void run() {
        try {
            boolean stop = false;
//            Scanner scanner = new Scanner(System.in);
//            stop = scanner.hasNext();
            while (!stop) {
                mirror.reflect();
                Thread.sleep(reflectingFrequency * 1000);
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MirrorThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MirrorThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NotADirectoryException ex) {
            Logger.getLogger(MirrorThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
