package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Vinícius
 */
public abstract class AbstractMirror {

    protected static boolean running = false;
    /**
     * Sets the origin path of the files on user's computer
     * @param pathOrigin
     * @throws NotADirectoryException if the path isn't a directory or doesn't exist
     */
    public abstract void setPathOrigin(String pathOrigin) throws NotADirectoryException;

    public abstract void setPathDestination(String pathDestination) throws IOException, NotADirectoryException;

    public abstract void reflect() throws IOException, NotADirectoryException;

    public static boolean isRunning() {
        return running;
    }
    
}
