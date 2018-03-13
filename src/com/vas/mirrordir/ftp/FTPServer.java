package com.vas.mirrordir.ftp;

import com.vas.mirrordir.models.Address;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

    public FTPServer() throws IOException {
        socket = new Socket("ftp.drivehq.com", 21);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
    }

    public String connect() throws UnknownHostException, IOException, InterruptedException {
        String usr = "USER vinicius.vas.ti" + "\r\n";
        outputStream.write(usr.getBytes());

        System.out.println(getReply(inputStream));

        String password = "PASS 123456" + "\r\n";
        outputStream.write(password.getBytes());
        String reply = getReply(inputStream);
        System.out.println(reply);
        return reply;
    }

    public List<File> getServerFiles() throws IOException, InterruptedException {
        List<File> serverFiles = new ArrayList<>();
        String str = sendFTPCommandWithReply("NLST");

        if (!str.isEmpty()) {
            String[] lista = str.split("\\n");

            for (String url : lista) {
                serverFiles.add(new File(url.trim()));
            }
        }
        return serverFiles;
    }

    public boolean createFile(File file) {
        try {
            return sendFTPCommand("STOR " + file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean deleteFile(File file) {
        try {
            return sendFTPCommand("DELE " + file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public String lastModifiedFile(File file) {
        try {
            return sendFTPCommandWithReply("MDTM " + file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "425";
    }

    public boolean createDirectory(File file) {
        try {
            return sendFTPCommand("MKD " + file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean removeDirectory(File file) {
        try {
            return sendFTPCommand("RMD " + file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Deprecated
    private boolean sendFTPCommand(String command) throws IOException, InterruptedException {
        String resp = pasv();
        Address addr = getIPandPort(resp);

        Socket data = new Socket(addr.getIp(), addr.getPort());
        InputStream dataIn = data.getInputStream();
        command += "\r\n";
        outputStream.write(command.getBytes());
        outputStream.flush();
        String reply = "";
        do {
            // getting FTP reply
            reply = getReply(inputStream);
            // if the cod starts with 1, wait for another reply
        } while (!reply.isEmpty() && reply.charAt(0) == '1');
        if (reply.charAt(0) != '2') {
            System.out.println("ERRO: " + reply);
        }
        // return true if the cod starts with 2
        return reply.charAt(0) == '2';
    }

    @Deprecated
    private String sendFTPCommandWithReply(String command) throws IOException, InterruptedException {
        String resp = pasv();
        Address addr = getIPandPort(resp);

        Socket data = new Socket(addr.getIp(), addr.getPort());
        InputStream dataIn = data.getInputStream();
        command += "\r\n";
        outputStream.write(command.getBytes());
        outputStream.flush();
        String reply = "";
        do {
            // getting FTP reply
            reply = getReply(inputStream);
            // if the cod starts with 1, wait for another reply
        } while (!reply.isEmpty() && reply.charAt(0) == '1');
        return reply;
    }

    public String getReply(InputStream inputStream) throws IOException, InterruptedException {
        ArrayList<String> replyArray = new ArrayList<>();
        String s = "";
        do {
            byte[] buff = new byte[5000];
            inputStream.read(buff);
            replyArray.add(new String(buff).trim());
            Thread.sleep(1000);
        } while (inputStream.available() != 0);
        for (String str : replyArray) {
            s += str + "\n";
        }
        return s.trim();
    }

    private String pasv() throws IOException, InterruptedException {
        String resp = "PASV" + "\r\n";
        outputStream.write(resp.getBytes());
        outputStream.flush();
        do {
            byte[] buff = new byte[10000];
            inputStream.read(buff);
            resp = new String(buff);
            System.out.println(resp.trim());
        } while (inputStream.available() > 0);
        return resp;
    }

    //gonna be private
    public Address getIPandPort(String resp) {
        StringTokenizer st = new StringTokenizer(resp);
        st.nextToken("(");
        String ip = st.nextToken(",").substring(1) + "."
                + st.nextToken(",") + "."
                + st.nextToken(",") + "."
                + st.nextToken(",");
        int value1 = Integer.parseInt(st.nextToken(","));
        int value2 = Integer.parseInt(st.nextToken(")").substring(1));
        int port = value1 * 256 + value2;
        Address addr = new Address(ip, port);
        return addr;
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
        byte[] buff = new byte[10000];
        try {
            inputStream.read(buff);
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return new String(buff).trim();
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
