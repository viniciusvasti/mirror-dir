package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.File;
import java.io.IOException;

/**
 *
 * @author Vinícius
 */
public interface IMirror {

    /**
     * Sets the origin path of the files on user's computer
     * @param pathOrigin
     * @throws NotADirectoryException if the path isn't a directory or doesn't exist
     */
    public void setPathOrigin(String pathOrigin) throws NotADirectoryException;

    public void setPathDestination(String pathDestination) throws IOException, NotADirectoryException;

    public void reflect() throws IOException, NotADirectoryException;

    public void reflect(File fileOrigin, File fileDestination) throws IOException;
}
