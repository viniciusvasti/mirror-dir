package com.vas.mirrordir.ftp;

import com.vas.mirrordir.models.Address;
import com.vas.mirrordir.models.FTPCredentials;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <pre>
 * CWD  = Change working directory.
 * DELE = Delete file.
 * LIST = Returns information of a file or directory if specified, else information of the current working directory is returned.
 * MDTM = Return the last-modified time of a specified file.
 * MKD  = Make directory.
 * MLSD = Lists the contents of a directory if a directory is named.
 * NLST = Returns a list of file names in a specified directory.
 * NOOP = No operation (dummy packet; used mostly on keepalives).
 * QUIT = Disconnect.
 * RETR = Retrieve a copy of the file
 * RMD  = Remove a directory.
 * STOR = Accept the data and to store the data as a file at the server site
 * STOU = Store file uniquely.
 * </pre>
 */
/**
 *
 * @author Vinicius
 */
public class FTPServer {

    private Socket socket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private final boolean DEBUG = false;
    private final FTPCredentials credentials;

    /**
     * Instanciate FTPServer with ftp user credentials
     *
     * @param credentials
     */
    public FTPServer(FTPCredentials credentials) {
        this.credentials = credentials;
    }

    /**
     * Connects with the FTP server.
     *
     * @return True if it is connected. False if it isn't, printing the error
     * message.
     * @throws IOException
     */
    public synchronized boolean connect() throws IOException {
        socket = new Socket(credentials.getDomain(), credentials.getPort());
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        sendCommand("USER " + credentials.getUser() + "\r\n");
        String reply = receiveReply();
        if (reply.startsWith("2")) {
            reply = receiveReply();
        }
        if (!reply.startsWith("331 ")) {
            System.out.println("Error sending user: " + reply);
            return false;
        }

        sendCommand("PASS " + credentials.getPassword());
        reply = receiveReply();
        if (!reply.startsWith("2")) {
            System.out.println("Error sending password: " + reply);
            return false;
        }
        return true;
    }

    /**
     * Disconnects from the FTP server.
     *
     * @throws Exception if it got a error FTP message
     */
    public synchronized void disconnect() throws Exception {
        sendCommand("QUIT");
        String reply = receiveReply();
        if (!reply.startsWith("2")) {
            throw new Exception("Error sending QUIT: " + reply);
        }
        inputStream.close();
        outputStream.close();
        socket.close();
    }

    /**
     * List FTP server files
     *
     * @return List of File with files and directories from FTP server
     * @throws IOException
     * @throws InterruptedException
     * @throws Exception
     */
    public synchronized List<File> getServerFiles() throws InterruptedException, Exception {
        List<File> serverFiles = new ArrayList<>();
        try (Socket passiveSocket = pasv()) {
            sendCommand("MLSD");
            String reply = receiveReply();
            // If the reply code starts with 1, wait for next reply
            while (reply.startsWith("1")) {
                reply = receiveReply();
            }
            if (!reply.startsWith("2")) {
                passiveSocket.close();
                throw new Exception("Error sending MLSD: " + reply);
            }
            reply = receiveReply(passiveSocket.getInputStream());

            if (!reply.isEmpty()) {
                String[] lista = reply.split("\\n");

                for (String url : lista) {
                    serverFiles.add(new File(url.split(";")[4].trim()));
                }
            }
        }
        return serverFiles;
    }

    /**
     * Get a the last modified time of the file in FTP server.
     *
     * @param file
     * @return A string with the Format: 20180314120615 -> 2018-03-14 12:06:15,
     * or an empty string if it doesn't exist
     * @throws Exception
     */
    public synchronized String lastModifiedFile(File file) throws Exception {
        try {
            sendCommand("MDTM " + file.getName());
            String reply = receiveReply();
            if (!reply.startsWith("2")) {
                return "";
            }
            return reply;
        } catch (Exception ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        throw new Exception("Error at request MDTM");
    }

    /**
     * Changes de working directory on FTP server
     *
     * @param path
     * @throws Exception
     */
    public synchronized void changeDirectory(String path) throws Exception {
        sendCommand("CWD " + path);
        String reply = receiveReply();
        if (!reply.startsWith("2")) {
            throw new Exception("Error sending CWD: " + reply);
        }
    }

    /**
     * Creates a file in FTP server
     *
     * @param file
     * @return True if the file has been created. False if it hasn't;
     * @throws IOException
     */
    public synchronized boolean createFile(File file) throws IOException {
        InputStream fileInputStream = null;
        OutputStream fileOutputStream = null;
        try {
            toBinaryMode();
            try (Socket passiveSocket = pasv()) {
                sendCommand("STOR " + file.getName());
                String reply = receiveReply();

                if (!reply.startsWith("1")) {
                    passiveSocket.close();
                    throw new Exception("Error sending STOR: " + reply);
                }

                fileInputStream = new FileInputStream(file);
                fileOutputStream = passiveSocket.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer);
                }
                fileOutputStream.flush();

                receiveReply();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        }
        return false;
    }

    /**
     * Deletes a file in FTP server
     *
     * @param file
     * @return True if it has been deleted. False if it hasn't
     * @throws java.lang.InterruptedException
     */
    public synchronized boolean deleteFile(File file) throws InterruptedException, Exception {
        try (Socket passiveSocket = pasv()) {
            sendCommand("DELE " + file.getName());
            String reply = receiveReply();
            if (!reply.startsWith("2")) {
                passiveSocket.close();
                //System.out.println("Error sending DELE: " + reply);
                return false;
            }
            return true;
        }
    }

    /**
     * Creates a directory in FTP server
     *
     * @param file
     * @return True if the directory has been created. False if it hasn't;
     */
    public synchronized boolean createDirectory(File file) {
        try {
            sendCommand("MKD " + file.getName());
            String reply = receiveReply();
            if (!reply.startsWith("2") && !reply.startsWith("550")) {
                throw new Exception("Error sending MKD: " + reply);
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Deletes a directory in FTP server
     *
     * @param file
     * @return True if the directory has been deleted. False if it hasn't
     */
    public synchronized boolean removeDirectory(File file) throws InterruptedException, Exception {
        try (Socket passiveSocket = pasv()) {
            sendCommand("RMD " + file.getName());
            String reply = receiveReply();
            if (!reply.startsWith("2")) {
                //System.out.println("Error sending RMD: " + reply);
                return false;
            }
        }
        return true;
    }

    /**
     * Change the transference data type to binary
     *
     * @return True if was successful, false if not.
     * @throws Exception
     */
    public synchronized boolean toBinaryMode() throws Exception {
        sendCommand("TYPE I");
        String reply = receiveReply();
        if (!reply.startsWith("2")) {
            throw new Exception("Error sending TYPE I: " + reply);
        }
        return true;
    }

    /**
     * Active passive mode
     *
     * @return A Socket based on ip and port returned by the FTP server
     * @throws IOException
     * @throws InterruptedException
     * @throws Exception
     */
    private Socket pasv() throws IOException, InterruptedException, Exception {
        sendCommand("PASV");
        String reply = receiveReply();
        if (!reply.startsWith("2")) {
            throw new Exception("Error sending PASV: " + reply);
        }
        Address address = new Address(reply);
        return new Socket(address.getIp(), address.getPort());
    }

    /**
     * Sends specified command to FTP server
     * @param command 
     */
    private void sendCommand(String command) {
        if (DEBUG) {
            System.out.println("Sent - " + command);
        }
        command += "\r\n";
        try {
            outputStream.write(command.getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Receive responso from FTP server
     * @return FTP server reply
     */
    private String receiveReply() {
        return receiveReply(inputStream);
    }

    private String receiveReply(InputStream inputStream) {
        byte[] buff = new byte[10000];
        try {
            inputStream.read(buff);
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        String reply = new String(buff).trim();
        if (DEBUG) {
            System.out.println("Receive - " + reply + "\n");
        }
        return reply;
    }
}
