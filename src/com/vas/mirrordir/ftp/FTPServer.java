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

    private Socket cmd;
    private InputStream cmdIn;
    private OutputStream cmdOut;

    public FTPServer() throws IOException {
        cmd = new Socket("ftp.drivehq.com", 21);
        cmdIn = cmd.getInputStream();
        cmdOut = cmd.getOutputStream();
    }

    public String connect() throws UnknownHostException, IOException, InterruptedException {
        String usr = "USER vinicius.vas.ti" + "\r\n";
        cmdOut.write(usr.getBytes());

        System.out.println(getResponse(cmdIn));

        String password = "PASS 123456" + "\r\n";
        cmdOut.write(password.getBytes());
        String response = getResponse(cmdIn);
        System.out.println(response);
        return response;
    }

    public String getResponse(InputStream cmdIn) throws IOException, InterruptedException {
        ArrayList<String> arrayDeResponse = new ArrayList<>();
        String s = "";
        do {
            byte[] buff = new byte[5000];
            cmdIn.read(buff);
            arrayDeResponse.add(new String(buff).trim());
            Thread.sleep(1000);
        } while (cmdIn.available() != 0);
        for (String str : arrayDeResponse) {
            s += str + "\n";
        }
        return s.trim();
    }

    public List<File> getServerFiles() throws IOException, InterruptedException {
        List<File> serverFiles = new ArrayList<>();
        String str = receiveFTPCommand("NLST");

        if (!str.isEmpty()) {
            String[] lista = str.split("\\n");

            for (String url : lista) {
                serverFiles.add(new File(url.trim()));
            }
        }
        return serverFiles;
    }

    public String createFile(File file) {
        String response = "";
        try {
            response = sendFTPCommand("STOR "+file.getName());
        } catch (IOException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(FTPServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    private String sendFTPCommand(String command) throws IOException, InterruptedException {
        pasv();
        command += "\r\n";
        cmdOut.write(command.getBytes());
        return getResponse(cmdIn);
    }

    private String receiveFTPCommand(String command) throws IOException, InterruptedException {
        String resp = pasv();
        Address addr = getIPandPort(resp);

        Socket data = new Socket(addr.getIp(), addr.getPort());
        InputStream dataIn = data.getInputStream();

        command += "\r\n";
        cmdOut.write(command.getBytes());
        getResponse(cmdIn);

        return getResponse(dataIn);
    }

    private String pasv() throws IOException, InterruptedException {
        String resp = "PASV" + "\r\n";
        cmdOut.write(resp.getBytes());
        cmdOut.flush();
        do {
            byte[] buff = new byte[10000];
            cmdIn.read(buff);
            resp = new String(buff);
            System.out.println(resp.trim());
        } while (cmdIn.available() > 0);
        return resp;
    }

    private Address getIPandPort(String resp) {
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
}
