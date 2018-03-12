/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import com.vas.mirrordir.ftp.FTPServer;
import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public class RemoteMirror implements IMirror {

    private File dirOrigin;
    private File dirDestination;
    private FTPServer ftpServer;

    public RemoteMirror(String pathOrigin) throws NotADirectoryException, IOException {
        setPathOrigin(pathOrigin);
        ftpServer = new FTPServer();
    }

    //gonna be private with Java 9
    @Override
    public void setPathOrigin(String pathOrigin) throws NotADirectoryException {
        File fileOrigin = new File(pathOrigin);
        if (!fileOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        if (!fileOrigin.isDirectory()) {
            throw new NotADirectoryException("The origin path is a file. It need to be a directory.");
        }
        this.dirOrigin = fileOrigin;
    }

    @Override
    public void setPathDestination(String pathDestination) throws IOException, NotADirectoryException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reflect() throws IOException, NotADirectoryException, UnknownHostException {
        if (!this.dirOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        try {
            ftpServer.connect();
            reflect(dirOrigin);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reflect(File fileOrigin) throws IOException {
        try {
            System.out.println("Start reflecting...");
            // filtering only files on origin directory
            List<File> localFiles = Arrays.asList(fileOrigin.listFiles());
            List<File> remoteFiles = ftpServer.getServerFiles();
            // iterates over the local directory comparing the files and adding or replacing it if necessary
            for (File localFile : localFiles) {
                if (localFile.isFile() && !remoteFiles.contains(localFile)) {
                    System.out.println(ftpServer.createFile(localFile));
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
