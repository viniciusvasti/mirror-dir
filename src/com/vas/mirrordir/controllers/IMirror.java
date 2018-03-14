package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.IOException;

/**
 *
 * @author Vinícius
 */
public interface IMirror {

    /**
     * Sets the origin directory of the files on user's computer
     * @param originPath
     * @throws NotADirectoryException if the path isn't a directory or doesn't exist
     */
    public abstract void setOriginPath(String originPath) throws NotADirectoryException;
    /**
     * Sets the destination directory to reflect the origin
     * @param destinationPath
     * @throws IOException
     * @throws NotADirectoryException 
     */
    public abstract void setDestinationPath(String destinationPath) throws IOException, NotADirectoryException;

    
    /**
     * Starts to reflect from origin path to destination path
     * @throws IOException
     * @throws NotADirectoryException 
     */
    public abstract void reflect() throws IOException, NotADirectoryException;
    
}
