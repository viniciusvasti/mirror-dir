package com.vas.mirrordir;

import com.vas.mirrordir.controllers.AbstractMirror;
import com.vas.mirrordir.controllers.LocalMirror;
import com.vas.mirrordir.controllers.RemoteMirror;
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
    private final AbstractMirror mirror;

    public MirrorThread(int reflectingFrequency, String pathOrigin) throws NotADirectoryException, IOException {
        super("MirrorDir");
        this.mirror = new RemoteMirror(pathOrigin);
        this.reflectingFrequency = reflectingFrequency;
        this.start();
    }

    public MirrorThread(int reflectingFrequency, String pathOrigin, String pathDestination) throws NotADirectoryException, IOException {
        super("MirrorDir");
        this.mirror = new LocalMirror(pathOrigin, pathDestination);
        this.reflectingFrequency = reflectingFrequency;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!mirror.isRunning()) {
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
