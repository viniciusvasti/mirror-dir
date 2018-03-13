package com.vas.mirrordir.controllers;

import com.vas.mirrordir.ftp.FTPServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Vinícius
 */
public class FTPServerTest {
    
    private FTPServer ftpConn;
    
    @BeforeClass
    public static void setUpClass() {    }
    
    @AfterClass
    public static void tearDownClass() {    }
    
    @Before
    public void setUp() throws Exception {
        ftpConn = new FTPServer();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void connect() throws Exception {
        String resposta = ftpConn.connect();
        Assert.assertNotEquals(resposta,"");
    }

    @Test
    public void makeDir() throws Exception {
        ftpConn.connect();
        ftpConn.getServerFiles();
    }
}
