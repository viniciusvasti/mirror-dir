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
public class MirrorTest {
    
    private LocalMirror mirror;
    
    @BeforeClass
    public static void setUpClass() {    }
    
    @AfterClass
    public static void tearDownClass() {    }
    
    @Before
    public void setUp() throws Exception {
        mirror = new LocalMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void mirrorTest() throws Exception {
        mirror = new LocalMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination");
    }

    @Test(expected = NotADirectoryException.class)
    public void mirrorTestOriginDirdIsValid() throws NotADirectoryException, IOException {
        mirror = new LocalMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigi", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination");
    }

    @Test(expected = NotADirectoryException.class)
    public void mirrorTestDestinationDirIsValid() throws NotADirectoryException, IOException {
        mirror = new LocalMirror("C:\\Users\\Vinícius\\Desktop\\mirrorOrigin", "C:\\Users\\Vinícius\\Desktop\\mirrorDestination.txt");
    }
    
    @Test
    public void reflectTest() throws IOException, NotADirectoryException {
        this.mirror.reflect();
    }
}
