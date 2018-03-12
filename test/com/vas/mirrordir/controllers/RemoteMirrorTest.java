package com.vas.mirrordir.controllers;

import com.vas.mirrordir.exceptions.NotADirectoryException;
import java.io.IOException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Vinícius
 */
public class RemoteMirrorTest {
    
    private RemoteMirror mirror;
    
    @BeforeClass
    public static void setUpClass() {    }
    
    @AfterClass
    public static void tearDownClass() {    }
    
    @Before
    public void setUp() throws Exception {
        mirror = new RemoteMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigin");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void connect() throws Exception {
        //mirror = new LocalMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination");
    }
}
