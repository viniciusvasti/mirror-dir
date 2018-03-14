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
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public class RemoteMirror extends AbstractMirror {

    private File localDir;
    private FTPServer ftpServer;
    Stack<File> directoryStack;

    public RemoteMirror(String pathOrigin) throws NotADirectoryException, IOException {
        setPathOrigin(pathOrigin);
        ftpServer = new FTPServer();
        directoryStack = new Stack<>();
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
        this.localDir = fileOrigin;
    }

    @Override
    public void setPathDestination(String pathDestination) throws IOException, NotADirectoryException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void reflect() throws IOException, NotADirectoryException, UnknownHostException {
        System.out.println("Start reflecting...");
        running = true;
        if (!this.localDir.exists()) {
            throw new NotADirectoryException("The origin path doesn't exists.");
        }
        try {
            if (ftpServer.connect()) {
                reflectDir();
                ftpServer.disconnect();
            } else {
                throw new Exception("Can't stablish connection with server");
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Finish reflecting...");
        running = false;
    }

    public void reflectDir() throws IOException {
        try {            
            directoryStack.push(localDir);
            do {
                File currentFile = directoryStack.pop();
                // getting a list of files on local directory
                List<File> localFiles = Arrays.asList(currentFile.listFiles());
                if (!currentFile.equals(localDir)) {
                    String path = currentFile.getAbsolutePath().replace(localDir.getAbsolutePath(), "");
                    ftpServer.changeDirectory(path);
                }
                List<File> remoteFiles = ftpServer.getServerFiles();
                // iterates over the local directory comparing the files and adding or replacing it if necessary
                for (File localFile : localFiles) {
                    createFileOrDirectoryIfNecessary(localFile);
                }

                // iterates over the remote directory comparing the files and excluding it if necessary 
                for (File remoteFile : remoteFiles) {
                    deleteFileOrDirectoryIfNecessary(currentFile, remoteFile);
                }
            } while (!directoryStack.isEmpty());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createFileOrDirectoryIfNecessary(File localFile) {
        String lastModifiedFile = "";
        if (localFile.isFile()) {
            lastModifiedFile = ftpServer.lastModifiedFile(localFile);
            if (lastModifiedFile.isEmpty()) {
                ftpServer.createFile(localFile);
            }
        } else {
            ftpServer.createDirectory(localFile);
            directoryStack.push(localFile);
        }
    }

    private void deleteFileOrDirectoryIfNecessary(File localFile, File remoteFile) {
        File possibleLocalFile = new File(localFile.getPath() + File.separator + remoteFile.getName());
        if (!possibleLocalFile.exists()) {
            ftpServer.deleteFile(remoteFile);
            ftpServer.removeDirectory(remoteFile);
        }
    }
}
