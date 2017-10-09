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

import com.google.common.collect.ImmutableMap;
import com.handsetdetection.data.Reply;
import java.io.IOException;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;



@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class HD4CommunityTest {


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }
    final String ultimateConfig = "hd4UltimateConfig.properties";
    public HD4CommunityTest() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }


    /**
     * Fetch Archive Test
     *
     * The community fetchArchive version contains a cut down version of the device specs.
     * It has general_vendor, general_model, display_x, display_y, general_platform, general_platform_version,
     * general_browser, general_browser_version, general_app, general_app_version, general_language,
     * general_language_full, benahmark_min & benchmark_max
     *
     * @throws java.io.IOException I/O exception
     * @group community
     **/
    @Test public void _002_fetchArchive() throws IOException {
        HD4 hd = new HD4(this.ultimateConfig);
        hd.setTimeout(500);

        // Purge store
        hd.store.purge();

        // Fetch new device specs into store.
        boolean result = hd.communityFetchArchive();

        byte[] data = hd.getRawReply();
        assertTrue(result);
        System.out.println( "" + data.length + " bytes ");
        // Filesize greater than 9Mb
        assertTrue(9_000_000 < data.length);
    }


    /**
     * Windows PC running Chrome
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPDesktop() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();
        assertTrue(result);
        assertEquals(0, (int)reply.getStatus());
        assertEquals("OK", reply.getMessage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Junk user-agent
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPDesktopJunk() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "User-Agent", "aksjakdjkjdaiwdidjkjdkawjdijwidawjdiajwdkawdjiwjdiawjdwidjwakdjajdkad"
        );
        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();
        assertFalse(result);
        assertEquals(301, (int)reply.getStatus());
        assertEquals("Not Found", reply.getMessage());
    }

    /**
     * Wii
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPWii() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "User-Agent", "Opera/9.30 (Nintendo Wii; U; ; 2047-7; es-Es)"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals(0, (int)reply.getStatus());
        assertEquals("OK", reply.getMessage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTP() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "User-Agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3 like Mac OS X; en-gb) AppleWebKit/533.17.9 (KHTML, like Gecko)"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals(0, (int)reply.getStatus());
        assertEquals("OK", reply.getMessage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.3", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone - user-agent in random other header
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPOtherHeader() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "blahblahblah",
            "x-fish-header", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3 like Mac OS X; en-gb) AppleWebKit/533.17.9 (KHTML, like Gecko)"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals(0, (int)reply.getStatus());
        assertEquals("OK", reply.getMessage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.3", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone 3GS (same UA as iPhone 3G, different x-local-hardwareinfo header)
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPHardwareInfo() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; en-gb) AppleWebKit/533.17.9 (KHTML, like Gecko)",
            "x-local-hardwareinfo", "320:480:100:100"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 3GS", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.2.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone 3G (same UA as iPhone 3GS, different x-local-hardwareinfo header)
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPHardwareInfoB() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; en-gb) AppleWebKit/533.17.9 (KHTML, like Gecko)",
            "x-local-hardwareinfo", "320:480:100:72"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 3G", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.2.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull( reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone - Crazy benchmark (eg from emulated desktop) with outdated OS
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPHardwareInfoC() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0 (iPhone; U; CPU iPhone OS 2_0 like Mac OS X; en-gb) AppleWebKit/533.17.9 (KHTML, like Gecko)",
            "x-local-hardwareinfo", "320:480:200:1200"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 3G", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("2.0", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * Detection test user-agent has been encoded with plus for space.
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPPlusForSpace() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0+(Linux;+Android+5.1.1;+SM-J110M+Build/LMY48B;+wv)+AppleWebKit/537.36+(KHTML,+like+Gecko)+Version/4.0+Chrome/47.0.2526.100+Mobile+Safari/537.36"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Samsung", reply.getHdSpecs().getGeneralVendor());
        assertEquals("SM-J110M", reply.getHdSpecs().getGeneralModel());
        assertEquals("Android", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("5.1.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone 5s running Facebook 9.0 app (hence no general_browser set).
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectHTTPFBiOS() {

        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D201 [FBAN/FBIOS;FBAV/9.0.0.25.31;FBBV/2102024;FBDV/iPhone6,2;FBMD/iPhone;FBSN/iPhone OS;FBSV/7.1.1;FBSS/2; FBCR/vodafoneIE;FBID/phone;FBLC/en_US;FBOP/5]",
            "Accept-Language", "da, en-gb;q=0.8, en;q=0.7"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 5S", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("7.1.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("da", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("Danish", reply.getHdSpecs().getGeneralLanguageFull());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertEquals("Facebook", reply.getHdSpecs().getGeneralApp());
        assertEquals("9.0", reply.getHdSpecs().getGeneralAppVersion());
        assertEquals("Safari Webview", reply.getHdSpecs().getGeneralBrowser());
        assertEquals("537.51", reply.getHdSpecs().getGeneralBrowserVersion());

        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * Samsung GT-I9500 Native - Note : Device shipped with Android 4.2.2, so this device has been updated.
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectBIAndroid() {
        Map<String, String> buildInfo = ImmutableMap.<String, String>builder()
            .put("ro.build.PDA", "I9500XXUFNE7")
            .put("ro.build.changelist", "699287")
            .put("ro.build.characteristics", "phone")
            .put("ro.build.date.utc", "1401287026")
            .put("ro.build.date", "Wed May 28 23:23:46 KST 2014")
            .put("ro.build.description", "ja3gxx-user 4.4.2 KOT49H I9500XXUFNE7 release-keys")
            .put("ro.build.display.id", "KOT49H.I9500XXUFNE7")
            .put("ro.build.fingerprint", "samsung/ja3gxx/ja3g:4.4.2/KOT49H/I9500XXUFNE7:user/release-keys")
            .put("ro.build.hidden_ver", "I9500XXUFNE7")
            .put("ro.build.host", "SWDD5723")
            .put("ro.build.id", "KOT49H")
            .put("ro.build.product", "ja3g")
            .put("ro.build.tags", "release-keys")
            .put("ro.build.type", "user")
            .put("ro.build.user", "dpi")
            .put("ro.build.version.codename", "REL")
            .put("ro.build.version.incremental", "I9500XXUFNE7")
            .put("ro.build.version.release", "4.4.2")
            .put("ro.build.version.sdk", "19")
            .put("ro.product.board", "universal5410")
            .put("ro.product.brand", "samsung")
            .put("ro.product.cpu.abi2", "armeabi")
            .put("ro.product.cpu.abi", "armeabi-v7a")
            .put("ro.product.device", "ja3g")
            .put("ro.product.locale.language", "en")
            .put("ro.product.locale.region", "GB")
            .put("ro.product.manufacturer", "samsung")
            .put("ro.product.model", "GT-I9500")
            .put("ro.product.name", "ja3gxx")
            .put("ro.product_ship", "true")
            .build();

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Samsung", reply.getHdSpecs().getGeneralVendor());
        assertEquals("GT-I9500", reply.getHdSpecs().getGeneralModel());
        assertEquals("Android", reply.getHdSpecs().getGeneralPlatform());
        //assertEquals("4.4.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertTrue(reply.getHdSpecs().getGeneralAliases().isEmpty());
        assertEquals("", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone 4S Native
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectBIiOS() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "utsname.machine", "iphone4,1",
            "utsname.brand", "Apple"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 4S", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        // Note : Default shipped version in the absence of any version information
        assertEquals("5.0", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Windows Phone Native Nokia Lumia 1020
     * @depends fetchArchive
     * @group community
     **/
    @Test public void deviceDetectWindowsPhone() {
        Map<String, String> buildInfo = ImmutableMap.of(
                "devicemanufacturer", "nokia",
                "devicename", "RM-875"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Nokia", reply.getHdSpecs().getGeneralVendor());
        assertEquals("RM-875", reply.getHdSpecs().getGeneralModel());
        assertEquals("Windows Phone", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("", reply.getHdSpecs().getGeneralType());
        assertEquals(0, (int)reply.getHdSpecs().getDisplayPpi());
    }

}
