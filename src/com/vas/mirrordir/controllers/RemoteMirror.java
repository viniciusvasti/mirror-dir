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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vinícius
 */
public final class RemoteMirror extends AbstractMirror {

    private File localDir;
    private final FTPServer ftpServer;
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
                localFiles.forEach((localFile) -> {
                    createFileOrDirectoryIfNecessary(localFile);
                });

                // iterates over the remote directory comparing the files and excluding it if necessary 
                remoteFiles.forEach((remoteFile) -> {
                    deleteFileOrDirectoryIfNecessary(currentFile, remoteFile);
                });
            } while (!directoryStack.isEmpty());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void createFileOrDirectoryIfNecessary(File localFile) {
        try {
            String lastModifiedFile = "";
            if (localFile.isFile()) {
                lastModifiedFile = ftpServer.lastModifiedFile(localFile);
                if (lastModifiedFile.isEmpty()) {
                    ftpServer.createFile(localFile);
                } else {
                    System.out.println("Local file modified at " + new Date(localFile.lastModified()));
                    System.out.println("Remote file modified at " + new Date(lastTimeModified(lastModifiedFile)));
                    if (localFile.lastModified() > lastTimeModified(lastModifiedFile)) {
                        ftpServer.deleteFile(localFile);
                        ftpServer.createFile(localFile);
                    }
                }
            } else {
                ftpServer.createDirectory(localFile);
                directoryStack.push(localFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(RemoteMirror.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteFileOrDirectoryIfNecessary(File localFile, File remoteFile) {
        File possibleLocalFile = new File(localFile.getPath() + File.separator + remoteFile.getName());
        if (!possibleLocalFile.exists()) {
            ftpServer.deleteFile(remoteFile);
            ftpServer.removeDirectory(remoteFile);
        }
    }

    /**
     * Converts the FTP response for MDTM (like "213 20180314123417" =
     * 2018-03-14 12:34:17) to milliseconds
     *
     * @param lastModified
     * @return a long that represents time in milliseconds
     */
    private long lastTimeModified(String lastModified) {
        // TODO needs find a way to verify the time fuse to compare time correctly
        long time = 0;
        try {
            if (!lastModified.isEmpty()) {
                String dateString = lastModified.split(" ")[1];
                LocalDateTime date = LocalDateTime.of(
                        Integer.parseInt(dateString.substring(0, 4)),
                        Integer.parseInt(dateString.substring(4, 6)),
                        Integer.parseInt(dateString.substring(6, 8)),
                        Integer.parseInt(dateString.substring(8, 10))-3,
                        Integer.parseInt(dateString.substring(10, 12)),
                        Integer.parseInt(dateString.substring(12))
                );
                time = date.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return time;
    }
}
