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

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.ImmutableMap;
import com.handsetdetection.data.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class HDStoreTest {


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }


    final Map<String, String> testData = ImmutableMap.of(
        "roses", "red",
        "fish", "blue",
        "sugar", "sweet",
        "number", "4"
    );

    final String testDevice3454 = "{\"Device\":{\"_id\":\"3454\",\"hd_ops\":{\"is_generic\":0,\"stop_on_detect\":0,\"overlay_result_specs\":0},\"hd_specs\":{\"general_vendor\":\"Sagem\",\"general_model\":\"MyX5-2\",\"general_platform\":\"\",\"general_image\":\"\",\"general_aliases\":\"\",\"general_eusar\":\"\",\"general_battery\":\"\",\"general_type\":\"\",\"general_cpu\":\"\",\"design_formfactor\":\"\",\"design_dimensions\":\"\",\"design_weight\":0,\"design_antenna\":\"\",\"design_keyboard\":\"\",\"design_softkeys\":\"\",\"design_sidekeys\":\"\",\"display_type\":\"\",\"display_color\":\"\",\"display_colors\":\"\",\"display_size\":\"\",\"display_x\":\"128\",\"display_y\":\"160\",\"display_other\":\"\",\"memory_internal\":\"\",\"memory_slot\":\"\",\"network\":\"\",\"media_camera\":\"\",\"media_secondcamera\":\"\",\"media_videocapture\":\"\",\"media_videoplayback\":\"\",\"media_audio\":\"\",\"media_other\":\"\",\"features\":\"\",\"connectors\":\"\",\"general_platform_version\":\"\",\"general_browser\":\"\",\"general_browser_version\":\"\",\"general_language\":\"\",\"general_platform_version_max\":\"\",\"general_app\":\"\",\"general_app_version\":\"\",\"display_ppi\":0,\"display_pixel_ratio\":0,\"benchmark_min\":0,\"benchmark_max\":0,\"general_app_category\":\"\",\"general_virtual\":0,\"display_css_screen_sizes\":\"\"}}}";

    final String testDevice3455 = "{\"Device\":{\"_id\":\"3455\",\"hd_ops\":{\"is_generic\":0,\"stop_on_detect\":0,\"overlay_result_specs\":0},\"hd_specs\":{\"general_aliases\":\"\",\"display_x\":\"120\",\"display_y\":\"120\",\"general_vendor\":\"Sagem\",\"general_model\":\"MY X55\",\"general_platform\":\"\",\"general_image\":\"\",\"network\":\"\",\"general_type\":\"\",\"general_eusar\":\"\",\"general_battery\":\"\",\"general_cpu\":\"\",\"design_formfactor\":\"\",\"design_dimensions\":\"\",\"design_weight\":0,\"design_antenna\":\"\",\"design_keyboard\":\"\",\"design_softkeys\":\"\",\"design_sidekeys\":\"\",\"display_type\":\"\",\"display_color\":\"\",\"display_colors\":\"\",\"display_size\":\"\",\"display_other\":\"\",\"memory_internal\":\"\",\"memory_slot\":\"\",\"media_camera\":\"\",\"media_secondcamera\":\"\",\"media_videocapture\":\"\",\"media_videoplayback\":\"\",\"media_audio\":\"\",\"media_other\":\"\",\"features\":\"\",\"connectors\":\"\",\"general_platform_version\":\"\",\"general_browser\":\"\",\"general_browser_version\":\"\",\"general_language\":\"\",\"general_platform_version_max\":\"\",\"general_app\":\"\",\"general_app_version\":\"\",\"display_ppi\":0,\"display_pixel_ratio\":0,\"benchmark_min\":0,\"benchmark_max\":0,\"general_app_category\":\"\",\"general_virtual\":0,\"display_css_screen_sizes\":\"\"}}}";

    final String testDevice3456 = "{\"Device\":{\"_id\":\"3456\",\"hd_ops\":{\"is_generic\":0,\"stop_on_detect\":0,\"overlay_result_specs\":0},\"hd_specs\":{\"general_vendor\":\"Sagem\",\"general_model\":\"myX5-2v\",\"general_platform\":\"\",\"general_image\":\"\",\"general_aliases\":\"\",\"general_eusar\":\"\",\"general_battery\":\"\",\"general_type\":\"\",\"general_cpu\":\"\",\"design_formfactor\":\"\",\"design_dimensions\":\"\",\"design_weight\":0,\"design_antenna\":\"\",\"design_keyboard\":\"\",\"design_softkeys\":\"\",\"design_sidekeys\":\"\",\"display_type\":\"\",\"display_color\":\"\",\"display_colors\":\"\",\"display_size\":\"\",\"display_x\":\"128\",\"display_y\":\"160\",\"display_other\":\"\",\"memory_internal\":\"\",\"memory_slot\":\"\",\"network\":\"\",\"media_camera\":\"\",\"media_secondcamera\":\"\",\"media_videocapture\":\"\",\"media_videoplayback\":\"\",\"media_audio\":\"\",\"media_other\":\"\",\"features\":\"\",\"connectors\":\"\",\"general_platform_version\":\"\",\"general_browser\":\"\",\"general_browser_version\":\"\",\"general_language\":\"\",\"general_platform_version_max\":\"\",\"general_app\":\"\",\"general_app_version\":\"\",\"display_ppi\":0,\"display_pixel_ratio\":0,\"benchmark_min\":0,\"benchmark_max\":0,\"general_app_category\":\"\",\"general_virtual\":0,\"display_css_screen_sizes\":\"\"}}}";
    public HDStoreTest() {
    }
    @Before
    public void setUp() {
    }
    @After
    public void tearDown() {
    }

    // Writes to store & cache
    @Test public void readWrite() throws IOException {
        String key = "storekey" + System.currentTimeMillis();
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);
        store.write(key, this.testData);

        TypeReference ref = new TypeReference<Map<String, String>>() {};
        Object data = store.read(key, ref);
        assertEquals(this.testData, data);

        HDCache cache = new HDCache();
        data = cache.read(key, ref);

        if ("none".equals(cache.getName())) {
            assertNull(data);
        } else {
            assertEquals(this.testData, data);
        }

        boolean exists = Files.exists(Paths.get(store.getDirectory(), key + ".json"));
        assertTrue(exists);
    }

    // Writes to store & not cache
    @Test public void storeFetch() throws IOException {
        String key = "storekey2" + System.currentTimeMillis();
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);
        store.store(key, this.testData);

        TypeReference ref = new TypeReference<Map<String, String>>() {};
        HDCache cache = new HDCache();
        Object data = cache.read(key, ref);
        assertEquals(null, data);

        data = store.fetch(key, ref);
        assertEquals(this.testData, data);

        boolean exists = Files.exists(Paths.get(store.getDirectory(), key + ".json"));
        assertTrue(exists);
    }

    // Test purge
    @Test public void purge() throws IOException {
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);

        store.write("storekey" + System.currentTimeMillis(), this.testData);
        assertNotEquals(0, new File(store.getDirectory()).list().length);
        store.purge();
        assertEquals(0, new File(store.getDirectory()).list().length);

    }

    // Reads all devices from Disk (Keys need to be in Device*json format)
    @Test public void fetchDevices() throws IOException {
        String key = "Device" + System.currentTimeMillis();
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);
        store.store(key, HD4.mapper().readValue(this.testDevice3454,
                new TypeReference<Map<String, Device>>() {}));
        List<Device> devices = store.fetchDevices();
        assertEquals("3454", devices.get(0).getId());
        store.purge();
    }

    // Moves a file from disk into store (vanishes from previous location).
    @Test public void moveInFetch() throws IOException {
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);
        store.purge();

        store.moveIn(testDevice3454.getBytes(), "Device_3454.json");
        assertTrue(Files.exists(Paths.get(store.getDirectory(), "Device_3454.json")));
        store.moveIn(testDevice3455.getBytes(), "Device_3455.json");
        store.moveIn(testDevice3456.getBytes(), "Device_3456.json");
        List<Device> devices = store.fetchDevices();
        assertEquals(3, devices.size());
        store.purge();
    }


    // Test singleton"ship
    @Test public void singleton() {
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, false);
        HDStore store2 = HDStore.getInstance();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir") + File.separator + "storetest");
        store.setConfig(props, false);
        assertEquals(store2.getDirectory(), store.getDirectory());
    }

    // Test iterability
    @Test public void iterability() throws IOException {
        HDStore store = HDStore.getInstance();
        Properties props = new Properties();
        props.setProperty("filesdir", System.getProperty("java.io.tmpdir"));
        store.setConfig(props, true);
        store.purge();

        store.moveIn(testDevice3454.getBytes(), "Device_3454.json");
        assertTrue(Files.exists(Paths.get(store.getDirectory(), "Device_3454.json")));
        store.moveIn(testDevice3455.getBytes(),  "Device_3455.json");
        store.moveIn(testDevice3456.getBytes(), "Device_3456.json");

        for(Device device: store) {
            String id = device.getId();
            assertTrue(id.equals("3454") || id.equals("3455") || id.equals("3456"));
        }
        store.purge();
    }
}
