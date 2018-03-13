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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public class RemoteMirror extends AbstractMirror {

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
        System.out.println("Start reflecting...");
        running = true;
        if (!this.dirOrigin.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        try {
            ftpServer.connect();
            reflect(dirOrigin);
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finish reflecting...");
        running = false;
    }

    public void reflect(File fileOrigin) throws IOException {
        try {
            // filtering only files on origin directory
            List<File> localFiles = Arrays.asList(fileOrigin.listFiles());
            List<File> remoteFiles = ftpServer.getServerFiles();
            String lastModifiedFile = "";
            // iterates over the local directory comparing the files and adding or replacing it if necessary
            for (File localFile : localFiles) {
                if (localFile.isFile()) {
                    lastModifiedFile = ftpServer.lastModifiedFile(localFile);
                    if (lastModifiedFile.substring(0, 3).equals("550")) {
                        ftpServer.createFile(localFile);
                    }
                } else {
                    ftpServer.createDirectory(localFile);
                }
            }

            // iterates over the destination directory comparing the files and excluding it if necessary 
            for (File remoteFile : remoteFiles) {
                File possibleFileInOrigin = new File(fileOrigin.getPath() + File.separator + remoteFile.getName());
                if (!possibleFileInOrigin.exists()) {
                    ftpServer.createFile(remoteFile);
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
