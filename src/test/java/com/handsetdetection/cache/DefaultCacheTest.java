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
package com.handsetdetection.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.handsetdetection.HDCache;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class DefaultCacheTest {
    @BeforeClass
    public static void setUpClass() {
    }
    @AfterClass
    public static void tearDownClass() {
    }
    final int volumeTest = 10_000;
    final Map<String, String> testData = ImmutableMap.of(
        "roses", "red",
        "fish", "blue",
        "sugar", "sweet",
        "number", "4"
    );
    final TypeReference ref = new TypeReference<Map<String, String>>() {};

    public DefaultCacheTest() {
    }


    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test public void basic() throws IOException {
        HDCache cache = new HDCache();

        if (!cache.getName().equals("none")) {
            String key = "" + System.currentTimeMillis();

            // Test Write & Read
            cache.write(key, this.testData);
            Object getReply = cache.read(key, this.ref);
            assertNotNull(getReply);

            // Test Flush
            boolean reply = cache.purge();
            assertTrue(reply);
            getReply = cache.read(key, this.ref);
            assertNull(getReply);
        }
    }

    @Test public void volume() throws IOException {
        HDCache cache = new HDCache();
        if (!cache.getName().equals("none")) {
            long now = System.currentTimeMillis();
            for(int i=0; i < this.volumeTest; i++) {
                String key = "test" + now + i;

                // Write
                boolean reply = cache.write(key, this.testData);
                assertTrue(reply);

                // Read
                Object getReply = cache.read(key, this.ref);
                assertNotNull(getReply);

                // Delete
                reply = cache.delete(key);
                assertTrue(reply);

                // Read
                getReply = cache.read(key, this.ref);
                assertNull(getReply);
        }
        cache.purge();

        }
        long now = System.currentTimeMillis();

    }

    @Test public void getName() {
        HDCache cache = new HDCache();
        assertNotNull(cache.getName());
    }
}
