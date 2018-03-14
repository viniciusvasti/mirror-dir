/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vas.mirrordir.models;

/**
 *
 * @author Vinícius
 */
public class FTPCredentials {
    final private String domain;
    final private String user;
    final private String password;
    private int port;

    public FTPCredentials(String domain, String user, String password) {
        this.domain = domain;
        this.user = user;
        this.password = password;
        this.port = 21;
    }

    public FTPCredentials(String domain, String user, String password, int port) {
        this(domain, user, password);
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }
}
