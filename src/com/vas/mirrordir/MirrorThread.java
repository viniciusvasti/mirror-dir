package com.vas.mirrordir;

import com.vas.mirrordir.controllers.IMirror;
import com.vas.mirrordir.controllers.LocalMirror;
import com.vas.mirrordir.controllers.RemoteMirror;
import com.vas.mirrordir.exceptions.NotADirectoryException;
import com.vas.mirrordir.models.FTPCredentials;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public class MirrorThread extends Thread {

    private final int reflectingFrequency;
    private final IMirror mirror;

    /**
     * Instanciates and starts mirroring specified local directory
     *
     * @param reflectingFrequency indicates the frequence in seconds which the
     * local directory will be compared with the remote one and reflected to it
     * @param pathOrigin indicates the local directory path
     * @param credentials credentials to stablish connection to FTP server
     * @throws NotADirectoryException
     * @throws IOException
     */
    public MirrorThread(int reflectingFrequency, String pathOrigin, FTPCredentials credentials) throws NotADirectoryException, IOException {
        super("MirrorDir");
        this.mirror = new RemoteMirror(pathOrigin, credentials);
        this.reflectingFrequency = reflectingFrequency;
        this.start();
    }

    /**
     * Instanciates and starts mirroring specified local directory
     *
     * @param reflectingFrequency indicates the frequence in seconds which the
     * local directory will be compared with the remote one and reflected to it
     * @param pathOrigin indicates the local origin directory path
     * @param pathDestination indicates the local destination directory path
     * @throws NotADirectoryException
     * @throws IOException
     */
    public MirrorThread(int reflectingFrequency, String pathOrigin, String pathDestination) throws NotADirectoryException, IOException {
        super("MirrorDir");
        this.mirror = new LocalMirror(pathOrigin, pathDestination);
        this.reflectingFrequency = reflectingFrequency;
        this.start();
    }
    
    @Override
    public void run() {
        try {
            while (true) {
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
