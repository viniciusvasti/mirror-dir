package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Vinícius
 */
public class LocalMirror implements IMirror {

    private File dirOrigin;
    private File dirDestination;

    public LocalMirror(String pathOrigin, String pathDestination) throws NotADirectoryException, IOException {
        setOriginPath(pathOrigin);
        setDestinationPath(pathDestination);
    }

    @Override
    public void setOriginPath(String pathOrigin) throws NotADirectoryException {
        this.dirOrigin = new File(pathOrigin);
        validOriginDirectory();
    }

    @Override
    public void setDestinationPath(String pathDestination) throws IOException, NotADirectoryException {
        File fileDestination = new File(pathDestination);
        // create directory if it doesn't exist
        if (!fileDestination.exists()) {
            fileDestination.createNewFile();
            if (!fileDestination.isDirectory()) {
                fileDestination.delete();
                throw new NotADirectoryException("The origin path is a file. It need to be a directory.");
            }
        }
        this.dirDestination = fileDestination;
    }

    @Override
    public void reflect() throws IOException, NotADirectoryException {
        if (!this.dirOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        if (!this.dirDestination.exists()) {
            setDestinationPath(this.dirDestination.getPath());
        }
        reflect(this.dirOrigin, this.dirDestination);
    }

    private void reflect(File fileOrigin, File fileDestination) throws IOException {
        try {
            // filtering only files on origin directory
            List<File> listFilesInOrigin = Arrays.asList(fileOrigin.listFiles());
            // filtering only files on destination directory
            List<File> listFilesInDestination = Arrays.asList(fileDestination.listFiles());

            // iterates over the origin directory comparing the files and adding or replacing it if necessary
            for (File fileInOrigin : listFilesInOrigin) {
                // setting the file that may exist in destination
                File possibleFileInDestination = new File(fileDestination.getPath() + File.separator + fileInOrigin.getName());
                // if its doesn't exist, copy from origin
                if (!possibleFileInDestination.exists()) {
                    Files.copy(fileInOrigin.toPath(), possibleFileInDestination.toPath());
                    //if its a directory, then we need to reflect it too.
                    if (possibleFileInDestination.isDirectory()) {
                        reflect(fileInOrigin, possibleFileInDestination);
                    }
                } // verify if it was modified
                else if (fileInOrigin.lastModified() > possibleFileInDestination.lastModified()) {
                    //if its a directory, then we need to reflect it too.
                    if (possibleFileInDestination.isDirectory()) {
                        reflect(fileInOrigin, possibleFileInDestination);
                    } else {
                        Files.copy(fileInOrigin.toPath(), possibleFileInDestination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                    // the listFilesInDestination will be used to delete files that don't exist on origin
                    // it had been modified implies that it hasn't to be deleted, we can remove it from listFilesInDestination
                    listFilesInDestination.remove(possibleFileInDestination);
                } //If it is a directory, even if it isn't a new neither modified file, we need to verify the children
                else if (possibleFileInDestination.isDirectory()) {
                    reflect(fileInOrigin, possibleFileInDestination);
                }
            }

            // iterates over the destination directory comparing the files and excluding it if necessary
            listFilesInDestination.forEach((fileInDestination) -> {
                File possibleFileInOrigin = new File(fileOrigin.getPath() + File.separator + fileInDestination.getName());
                if (!possibleFileInOrigin.exists()) {
                    fileInDestination.delete();
                }
            });
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void validOriginDirectory() throws NotADirectoryException {
        if (!this.dirOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        if (!this.dirOrigin.isDirectory()) {
            throw new NotADirectoryException("The origin path is a file. It need to be a directory.");
        }
    }
}
