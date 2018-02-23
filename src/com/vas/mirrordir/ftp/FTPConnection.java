/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vas.mirrordir.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 *
 * @author Vinicius
 */
public class FTPConnection {

    private Socket cmd;
    private InputStream cmdIn;
    private OutputStream cmdOut;
    
    public void conecta() throws UnknownHostException, IOException, InterruptedException {
        cmd = new Socket("ftp.drivehq.com", 21);
        cmdIn = cmd.getInputStream();
        cmdOut = cmd.getOutputStream();

        String usr = "USER vinicius.vas.ti" + "\r\n";
        cmdOut.write(usr.getBytes());

        System.out.println(getResposta(cmdIn));

        String password = "PASS vini9010" + "\r\n";
        cmdOut.write(password.getBytes());

        System.out.println(getResposta(cmdIn));
    }

    public String getResposta(InputStream cmdIn) throws IOException, InterruptedException {
        ArrayList<String> arrayDeResposta = new ArrayList<>();
        String s = "";
        do {
            byte[] buff = new byte[5000];
            cmdIn.read(buff);
            arrayDeResposta.add(new String(buff).trim());
            Thread.sleep(1000);
        } while (cmdIn.available() != 0);
        for (String str : arrayDeResposta) {
            s += str + "\n";
        }
        return s.trim();
    }
}
