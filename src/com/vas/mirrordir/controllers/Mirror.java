/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Vinícius
 */
public class Mirror {

    private File dirOrigin;
    private File dirDestination;

    public Mirror(String pathOrigin, String pathDestination) throws NotADirectoryException, IOException {
        setPathOrigin(pathOrigin);
        setPathDestination(pathDestination);
    }

    private void setPathOrigin(String pathOrigin) throws NotADirectoryException {
        File fileOrigin = new File(pathOrigin);
        if (!fileOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        if (!fileOrigin.isDirectory()) {
            throw new NotADirectoryException("The origin path is a file. It need to be a directory.");
        }
        this.dirOrigin = fileOrigin;
    }

    public void setPathDestination(String pathDestination) throws IOException, NotADirectoryException {
        File fileDestination = new File(pathDestination);
        if (!fileDestination.exists()) {
            fileDestination.createNewFile();
            if (!fileDestination.isDirectory()) {
                fileDestination.delete();
                throw new NotADirectoryException("The origin path is a file. It need to be a directory.");
            }
        }
        this.dirDestination = fileDestination;
    }

    public void reflect() throws IOException, NotADirectoryException {
        if (!this.dirOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        if (!this.dirDestination.exists()) {
            setPathDestination(this.dirDestination.getPath());
        }
        reflect(this.dirOrigin, this.dirDestination);
    }

    private void reflect(File fileOrigin, File fileDestination) throws IOException {
        // filtering only files on origin directory
        Stream<File> filesInOrigin = Arrays.stream(fileOrigin.listFiles());
        List<File> listFilesInOrigin = filesInOrigin.filter(f -> f.isFile()).collect(Collectors.toList());
        // iterates over the origin directory comparing the files and adding or replacing it if necessary
        for (File fileInOrigin : listFilesInOrigin) {
            // setting the file that may exist in destination
            File possibleFileInDestination = new File(fileDestination.getPath()+File.separator+fileInOrigin.getName());
            // if its doesn't exist, copy from origin
            if (!possibleFileInDestination.exists()) {
                Files.copy(fileInOrigin.toPath(), possibleFileInDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            // Note: verify if when a copy was done, copied file mantains the modified date
            // comparing the last modified dates
            else if (fileInOrigin.lastModified() > possibleFileInDestination.lastModified()) {
                Files.copy(fileInOrigin.toPath(), possibleFileInDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
        
        // filtering only files on destination directory
        Stream<File> filesInDestination = Arrays.stream(fileDestination.listFiles());
        List<File> listFilesInDestination = filesInDestination.filter(f -> f.isFile()).collect(Collectors.toList());
        // iterates over the destination directory comparing the files and excluding it if necessary 
        for (File fileInDestination : listFilesInDestination) {
            File possibleFileInOrigin = new File(fileOrigin.getPath()+File.separator+fileInDestination.getName());
            if (!possibleFileInOrigin.exists()) {
                fileInDestination.delete();
            }
        }
    }
}
