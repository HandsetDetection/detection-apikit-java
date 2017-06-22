/*
 * Copyright (c) 2017, Richard Uren <richard@teleport.com.au>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.handsetdetection;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 *
 */
public class HDExtraTest {


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    public HDExtraTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test public void comparePlatformVersionsA() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("9.0.1", "9.1");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsB() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("9.0.1", "9.0.1");
        assertEquals(result, 0);
    }

    @Test public void comparePlatformVersionsC() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("9.1", "9.0.1");
        assertTrue(1 <= result);
    }

    @Test public void comparePlatformVersionsD() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.2.1", "9.1");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsE() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.2.1", "4.2.2");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsF() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.2.1", "4.2.12");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsG() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.1.1", "4.2.1");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsH() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.0.21", "40.21");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsI() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("4.1.1", "411");
        assertTrue(-1 >= result);
    }

    @Test public void comparePlatformVersionsJ() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("411", "4.1.1");
        assertTrue(1 <= result);
    }

    @Test public void comparePlatformVersionsK() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("Q7.1", "Q7.2");
        assertTrue(1 >= result);
    }

    @Test public void comparePlatformVersionsL() {
        HDExtra extra = new HDExtra();
        int result = extra.comparePlatformVersions("Q5SK", "Q7SK");
        assertTrue(1 >= result);
    }
}
