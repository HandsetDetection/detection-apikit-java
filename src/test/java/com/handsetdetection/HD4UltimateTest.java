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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.ImmutableMap;
import com.handsetdetection.data.*;
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
public class HD4UltimateTest {


    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }


    final String ultimateConfig = "hd4UltimateConfig.properties";

    final String testSpecs = "{\"general_vendor\":\"Nokia\",\"general_model\":\"N95\",\"general_platform\":\"Symbian\",\"general_platform_version\":\"9.2\",\"general_browser\":\"\",\"general_browser_version\":\"\",\"general_image\":\"nokian95-1403496370-0.gif\",\"general_aliases\":[],\"general_eusar\":\"0.50\",\"general_battery\":[\"Li-Ion 950 mAh\",\"BL-5F\"],\"general_type\":\"Mobile\",\"general_cpu\":[\"Dual ARM 11\",\"332MHz\"],\"design_formfactor\":\"Dual Slide\",\"design_dimensions\":\"99 x 53 x 21\",\"design_weight\":\"120\",\"design_antenna\":\"Internal\",\"design_keyboard\":\"Numeric\",\"design_softkeys\":\"2\",\"design_sidekeys\":[\"Volume\",\"Camera\"],\"display_type\":\"TFT\",\"display_color\":\"Yes\",\"display_colors\":\"16M\",\"display_size\":\"2.6\\\"\",\"display_x\":\"240\",\"display_y\":\"320\",\"display_other\":[],\"memory_internal\":[\"160MB\",\"64MB RAM\",\"256MB ROM\"],\"memory_slot\":[\"microSD\",\"8GB\",\"128MB\"],\"network\":[\"GSM850\",\"GSM900\",\"GSM1800\",\"GSM1900\",\"UMTS2100\",\"HSDPA2100\",\"Infrared\",\"Bluetooth 2.0\",\"802.11b\",\"802.11g\",\"GPRS Class 10\",\"EDGE Class 32\"],\"media_camera\":[\"5MP\",\"2592x1944\"],\"media_secondcamera\":[\"QVGA\"],\"media_videocapture\":[\"VGA@30fps\"],\"media_videoplayback\":[\"MPEG4\",\"H.263\",\"H.264\",\"3GPP\",\"RealVideo 8\",\"RealVideo 9\",\"RealVideo 10\"],\"media_audio\":[\"MP3\",\"AAC\",\"AAC+\",\"eAAC+\",\"WMA\"],\"media_other\":[\"Auto focus\",\"Video stabilizer\",\"Video calling\",\"Carl Zeiss optics\",\"LED Flash\"],\"features\":[\"Unlimited entries\",\"Multiple numbers per contact\",\"Picture ID\",\"Ring ID\",\"Calendar\",\"Alarm\",\"To-Do\",\"Document viewer\",\"Calculator\",\"Notes\",\"UPnP\",\"Computer sync\",\"VoIP\",\"Music ringtones (MP3)\",\"Vibration\",\"Phone profiles\",\"Speakerphone\",\"Accelerometer\",\"Voice dialing\",\"Voice commands\",\"Voice recording\",\"Push-to-Talk\",\"SMS\",\"MMS\",\"Email\",\"Instant Messaging\",\"Stereo FM radio\",\"Visual radio\",\"Dual slide design\",\"Organizer\",\"Word viewer\",\"Excel viewer\",\"PowerPoint viewer\",\"PDF viewer\",\"Predictive text input\",\"Push to talk\",\"Voice memo\",\"Games\"],\"connectors\":[\"USB\",\"MiniUSB\",\"3.5mm Audio\",\"TV Out\"],\"general_platform_version_max\":\"\",\"general_app\":\"\",\"general_app_version\":\"\",\"general_language\":\"\",\"display_ppi\":154,\"display_pixel_ratio\":\"1.0\",\"benchmark_min\":0,\"benchmark_max\":0,\"general_app_category\":\"\",\"general_virtual\":0,\"display_css_screen_sizes\":[\"240x320\"]}";
    public HD4UltimateTest() {
    }
    @Before
    public void setUp() {
    }
    @After
    public void tearDown() {
    }

    /**
     * Broken Archive Test
     * @group ultimate
     **/
    @Test public void unzipBogusArchive() {
        HD4 hd = new HD4(this.ultimateConfig);
        hd.setTimeout(500);
        boolean result = hd.installArchive("testy mc testery fish fish fish".getBytes());
        Reply data = hd.getReply();
        assertFalse(result);
        assertEquals(299, (int)data.getStatus());
    }

    /**
     * Empty Archive Test
     * @throws java.io.IOException I/O exception
     * @group ultimate
     **/
    @Test public void _001_detectionOnEmptyArchive() throws IOException {
        HD4 hd = new HD4(this.ultimateConfig);

        HDStore store = HDStore.getInstance();
        store.purge();

        Map<String, String> headers = ImmutableMap.of(
                "User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36"
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();
        assertEquals(299, (int)reply.getStatus());
        assertEquals("Branch not found. Is it installed ?", reply.getMessage());
    }

    /**
     * Empty Archive Test
     * @throws java.io.IOException I/O exception
     * @group ultimate
     **/
    @Test public void _002_detectionOnEmptyArchiveStillNotSolved() throws IOException {
        HD4 hd = new HD4(this.ultimateConfig);

        HDStore store = HDStore.getInstance();
        store.purge();

        Map<String, String> headers = ImmutableMap.of(
            "User-Agent", "..."
        );

        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();
        assertEquals(299, (int)reply.getStatus());
        assertEquals("Branch not found. Is it installed ?", reply.getMessage());
    }

    /**
     * Fetch Archive Test
     * @throws java.io.IOException I/O exception
     * @group ultimate
     **/
    @Test public void _003_fetchArchive() throws IOException {
        HD4 hd = new HD4(this.ultimateConfig);
        hd.setTimeout(500);

        HDStore store = HDStore.getInstance();
        store.purge();

        boolean result = hd.deviceFetchArchive();
        byte[] data = hd.getRawReply();
        System.out.println("Downloaded " + data.length + "bytes ");
        assertTrue(result);
        // Filesize greater than 19Mb (currently 28Mb).
        assertTrue(19_000_000 < data.length);
    }

    /**
     * device vendors test
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceVendors() {
        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceVendors();
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals(0, (int)reply.getStatus());
        assertEquals("OK", reply.getMessage());
        assertTrue("Nokia", reply.getVendor().contains("Nokia"));
        assertTrue("Nokia", reply.getVendor().contains("Samsung"));
    }

    /**
     * device models test
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceModels() {
        HD4 hd = new HD4(this.ultimateConfig);
        boolean reply = hd.deviceModels("Nokia");
        Reply data = hd.getReply();

        assertEquals(reply, true);
        assertTrue(700 < data.getModel().size());
        assertEquals(0, (int)data.getStatus());
        assertEquals("OK", data.getMessage());
    }

    /**
     * device view test
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceView() throws JsonProcessingException {
        HD4 hd = new HD4(this.ultimateConfig);
        boolean reply = hd.deviceView("Nokia", "N95");
        Reply data = hd.getReply();
        assertEquals(reply, true);
        assertEquals(0, (int)data.getStatus());
        assertEquals("OK", data.getMessage());
        String json = HD4.mapper().writeValueAsString(data.getDevice());
        assertEquals(this.testSpecs, json);

    }

    /**
     * device whatHas test
     * @throws com.fasterxml.jackson.core.JsonProcessingException
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceWhatHas() throws JsonProcessingException {
        HD4 hd = new HD4(this.ultimateConfig);
        boolean reply = hd.deviceWhatHas("design_dimensions", "101 x 44 x 16");
        Reply data = hd.getReply();
        assertEquals(reply, true);
        assertEquals(0, (int)data.getStatus());
        assertEquals("OK", data.getMessage());
        String jsonString = HD4.mapper().writeValueAsString(data.getDevices());
        assertTrue(jsonString.contains("Asus"));
        assertTrue(jsonString.contains("V80"));
        assertTrue(jsonString.contains("Spice"));
        assertTrue(jsonString.contains("S900"));
        assertTrue(jsonString.contains("Voxtel"));
        assertTrue(jsonString.contains("RX800"));
    }

    /**
     * Windows PC running Chrome
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Computer", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Junk user-agent
     * @depends fetchArchive
     * @group ultimate
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
     * @group ultimate
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
        assertEquals("Console", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.3", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone - user-agent in random other header
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.3", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("en-gb", reply.getHdSpecs().getGeneralLanguage());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone 3GS (same UA as iPhone 3G, different x-local-hardwareinfo header)
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone 3G (same UA as iPhone 3GS, different x-local-hardwareinfo header)
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * iPhone - Crazy benchmark (eg from emulated desktop) with outdated OS
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * Detection test user-agent has been encoded with plus for space.
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone 5s running Facebook 9.0 app (hence no general_browser set).
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertEquals("Facebook", reply.getHdSpecs().getGeneralApp());
        assertEquals("9.0", reply.getHdSpecs().getGeneralAppVersion());
        assertEquals("", reply.getHdSpecs().getGeneralBrowser());
        assertEquals("", reply.getHdSpecs().getGeneralBrowserVersion());

        assertNotNull(reply.getHdSpecs().getDisplayPixelRatio());
        assertNotNull(reply.getHdSpecs().getDisplayPpi());
        assertNotNull(reply.getHdSpecs().getBenchmarkMin());
        assertNotNull(reply.getHdSpecs().getBenchmarkMax());
    }

    /**
     * Android version is not supplied in UA & device base profile has more info than detected platform result
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectNoPlatformOverlay() {
        HD4 hd = new HD4(this.ultimateConfig);
        Map<String, String> headers = ImmutableMap.of(
            "user-agent", "Mozilla/5.0 (Linux; U; Android; en-ca; GT-I9500 Build) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1"
        );
        boolean result = hd.deviceDetect(headers);
        Reply reply = hd.getReply();

        assertTrue(result);
        assertEquals("Samsung", reply.getHdSpecs().getGeneralVendor());
        assertEquals("GT-I9500", reply.getHdSpecs().getGeneralModel());
        assertEquals("Android", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.2.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Samsung GT-I9500 Native - Note : Device shipped with Android 4.2.2, so this device has been updated.
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("4.4.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Samsung Galaxy S4", reply.getHdSpecs().getGeneralAliases().get(0));
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Detection test Samsung GT-I9500 Native - Note : Device shipped with Android 4.2.2, so this device has been updated.
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectBIAndroidUpdatedOs() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "ro.build.id", "KOT49H",
            "ro.build.version.release", "5.2",
            "ro.build.version.sdk", "19",
            "ro.product.brand", "samsung",
            "ro.product.model", "GT-I9500"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Samsung", reply.getHdSpecs().getGeneralVendor());
        assertEquals("GT-I9500", reply.getHdSpecs().getGeneralModel());
        assertEquals("Android", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("5.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Samsung Galaxy S4", reply.getHdSpecs().getGeneralAliases().get(0));
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Detection test Samsung GT-I9500 Native - Note : Device shipped with Android 4.2.2
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectBIAndroidDefaultOs() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "ro.product.brand", "samsung",
            "ro.product.model", "GT-I9500"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Samsung", reply.getHdSpecs().getGeneralVendor());
        assertEquals("GT-I9500", reply.getHdSpecs().getGeneralModel());
        assertEquals("Android", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("4.2.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Samsung Galaxy S4", reply.getHdSpecs().getGeneralAliases().get(0));
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * iPhone 4S Native
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }

    /**
     * Detection test iPhone 4S Native
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectBIiOSOverlayPlatform() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "utsname.machine", "iphone4,1",
            "utsname.brand", "Apple",
            "uidevice.systemversion", "5.1",
            "uidevice.systemname", "iphone os"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 4S", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("5.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }
    //
    /**
     * Windows Phone Native Nokia Lumia 1020
     * @depends fetchArchive
     * @group ultimate
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
        assertEquals("8.0", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertEquals(326, (int)reply.getHdSpecs().getDisplayPpi());
    }

    /**
     * Windows Phone Native Nokia Lumia 1020
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectWindowsPhoneB() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "devicemanufacturer", "nokia",
            "devicename", "RM-875",
            "osname", "windows phone",
            "osversion", "8.1"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Nokia", reply.getHdSpecs().getGeneralVendor());
        assertEquals("RM-875", reply.getHdSpecs().getGeneralModel());
        assertEquals("Windows Phone", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("8.1", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
        assertEquals(326, (int)reply.getHdSpecs().getDisplayPpi());
    }

    /**
     * Detection test Windows Phone Native Nokia Lumia 1020
     * @depends fetchArchive
     * @group ultimate
     **/
    @Test public void deviceDetectBIiPhoneOverlay() {
        Map<String, String> buildInfo = ImmutableMap.of(
            "utsname.brand", "apple",
            "utsname.machine", "iPhone7,2",
            "UIDevice.systemVersion", "9.2",
            "UIDevice.systemName", "iPhone OS"
        );

        HD4 hd = new HD4(this.ultimateConfig);
        boolean result = hd.deviceDetect(buildInfo);
        Reply reply = hd.getReply();

        assertEquals("Apple", reply.getHdSpecs().getGeneralVendor());
        assertEquals("iPhone 6", reply.getHdSpecs().getGeneralModel());
        assertEquals("iOS", reply.getHdSpecs().getGeneralPlatform());
        assertEquals("9.2", reply.getHdSpecs().getGeneralPlatformVersion());
        assertEquals("Mobile", reply.getHdSpecs().getGeneralType());
    }
}
