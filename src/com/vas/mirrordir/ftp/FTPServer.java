package com.vas.mirrordir.ftp;

import com.vas.mirrordir.models.Address;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
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
    private final boolean DEBUG = true;

    public FTPServer() throws IOException {
    }

    public boolean connect() throws UnknownHostException, IOException, InterruptedException, Exception {
        socket = new Socket("ftp.drivehq.com", 21);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();

        String usr = "USER vinicius.vas.ti" + "\r\n";
        sendCommand(usr);
        String reply = receiveReply();
        if (DEBUG) {
            System.out.println("Sent USER - " + reply);
        }
        if (reply.startsWith("2")) {
            reply = receiveReply();
        }

        if (DEBUG) {
            System.out.println("Sent USER - " + reply);
        }
        if (!reply.startsWith("331 ")) {
            throw new Exception("Error: " + reply);
        }

        String password = "PASS 123456";
        sendCommand(password);
        reply = receiveReply();
        if (DEBUG) {
            System.out.println("Sent PASSWORD - " + reply);
        }
        if (!reply.startsWith("2")) {
            throw new Exception("Error: " + reply);
        }
        return true;
    }

    public boolean disconnect() throws Exception {
        sendCommand("QUIT");
        String reply = receiveReply();
        if (DEBUG) {
            System.out.println("Sent QUIT - " + reply);
        }
        if (!reply.startsWith("2")) {
            throw new Exception("Error: " + reply);
        }
        inputStream.close();
        outputStream.close();
        socket.close();
        return true;
    }

    public List<File> getServerFiles() throws IOException, InterruptedException, Exception {
        List<File> serverFiles = new ArrayList<>();
        try (Socket passiveSocket = pasv()) {
            sendCommand("NLST");
            String reply = receiveReply();
            // If the reply code starts with 1, wait for next reply
            while (reply.startsWith("1")) {
                reply = receiveReply();
            }
            if (DEBUG) {
                System.out.println("Requested File List - " + reply);
            }
            if (!reply.startsWith("2")) {
                passiveSocket.close();
                throw new Exception("Error: " + reply);
            }
            reply = receiveReply(passiveSocket.getInputStream());
            if (DEBUG) {
                System.out.println("Requested File List - " + reply);
            }
            
            if (!reply.isEmpty()) {
                String[] lista = reply.split("\\n");
                
                for (String url : lista) {
                    serverFiles.add(new File(url.trim()));
                }
            }
        }
        return serverFiles;
    }

    public String lastModifiedFile(File file) {
        //reply if exist: "213 20180314120615"
        try {
            sendCommand("MDTM " + file.getName());
            String reply = receiveReply();
            if (DEBUG) {
                System.out.println("Requested Last Modified - " + reply);
            }
            if (!reply.startsWith("2")) {
                return "";
            }
            return reply;
        } catch (Exception ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "425";
    }

    public void changeDirectory(String path) throws Exception {
        sendCommand("CWD " + path);
        String reply = receiveReply();
        if (DEBUG) {
            System.out.println("Requested Last Modified - " + reply);
        }
        if (!reply.startsWith("2")) {
            throw new Exception("Error: " + reply);
        }
    }

    public boolean createFile(File file) {
        try {
            toBinaryMode();
            try (Socket passiveSocket = pasv()) {
                sendCommand("STOR " + file.getName());
                String reply = receiveReply();
                
                if (DEBUG) {
                    System.out.println("Sent STOR - " + reply);
                }
                if (!reply.startsWith("1")) {
                    passiveSocket.close();
                    throw new Exception("Error: " + reply);
                }
                
                InputStream fileInputStream = new FileInputStream(file);
                OutputStream fileOutputStream = passiveSocket.getOutputStream();
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer);
                }
                fileOutputStream.flush();
                
                reply = receiveReply();
//            if (!reply.startsWith("2")) {
//                passiveSocket.close();
//                throw new Exception("Error: " + reply);
//            }
if (DEBUG) {
    System.out.println(reply);
}
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

    public boolean deleteFile(File file) {
        try {
            try (Socket passiveSocket = pasv()) {
                sendCommand("DELE " + file.getName());
                String reply = receiveReply();
                // If the reply code starts with 1, wait for next reply
                while (reply.startsWith("1")) {
                    reply = receiveReply();
                }
                if (DEBUG) {
                    System.out.println("Sent DELE - " + reply);
                }
                if (!reply.startsWith("2")) {
                    passiveSocket.close();
                    throw new Exception("Error: " + reply);
                }
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

    public boolean createDirectory(File file) {
        try {
            sendCommand("MKD " + file.getName());
            String reply = receiveReply();
            // If the reply code starts with 1, wait for next reply
            while (reply.startsWith("1")) {
                reply = receiveReply();
            }
            if (DEBUG) {
                System.out.println(reply);
            }
            if (!reply.startsWith("2") && !reply.startsWith("550")) {
                throw new Exception("Error: " + reply);
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

    public boolean removeDirectory(File file) {
        try {
            try (Socket passiveSocket = pasv()) {
                sendCommand("RMD " + file.getName());
                String reply = receiveReply();
                // If the reply code starts with 1, wait for next reply
                while (reply.startsWith("1")) {
                    reply = receiveReply();
                }
                if (DEBUG) {
                    System.out.println("Sent DELE - " + reply);
                }
                if (!reply.startsWith("2")) {
                    System.out.println("Error: " + reply);
                }
                reply = receiveReply(passiveSocket.getInputStream());
                if (DEBUG) {
                    System.out.println(reply);
                }
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

    public boolean toBinaryMode() throws Exception {
        sendCommand("TYPE I");
        String reply = receiveReply();
        if (!reply.startsWith("2")) {
            throw new Exception("Error: " + reply);
        }
        return true;
    }

    //gonna be private
    public Socket pasv() throws IOException, InterruptedException {
        sendCommand("PASV");
        String reply = receiveReply();
        if (DEBUG) {
            System.out.println(reply);
        }
        if (!reply.startsWith("2")) {
            System.out.println("Error: " + reply);
            return null;
        }
        Address address = new Address(reply);
        return new Socket(address.getIp(), address.getPort());
    }

    //gonna be private
    public void sendCommand(String command) {
        command += "\r\n";
        try {
            outputStream.write(command.getBytes());
            outputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //gonna be private
    public String receiveReply() {
        return receiveReply(inputStream);
    }

    //gonna be private
    public String receiveReply(InputStream inputStream) {
        byte[] buff = new byte[10000];
        try {
            inputStream.read(buff);
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new String(buff).trim();
    }
}
