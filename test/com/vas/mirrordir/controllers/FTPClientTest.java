package com.vas.mirrordir.controllers;

import com.vas.mirrordir.ftp.FTPClient;
import com.vas.mirrordir.models.FTPCredentials;
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
public class FTPClientTest {
    
    private FTPClient ftpConn;
    
    @BeforeClass
    public static void setUpClass() {    }
    
    @AfterClass
    public static void tearDownClass() {    }
    
    @Before
    public void setUp() throws Exception {
        ftpConn = new FTPClient(new FTPCredentials("ftp.drivehq.com", "vinicius.vas.ti", "123456"));
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void connect() throws Exception {
        Assert.assertEquals(ftpConn.connect(),true);
    }

    @Test
    public void makeDir() throws Exception {
        ftpConn.connect();
        ftpConn.getServerFiles();
    }
}
