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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.handsetdetection.data.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.NoSuchFileException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.commons.codec.digest.DigestUtils;

/**
 *
 */
public class HDBase {

    static final String DETECTIONV4_STANDARD = "0";
    static final String DETECTIONV4_GENERIC = "1";
    private static final Pattern CLEAN_STR_PATTERN = Pattern.compile("[^(\\x20-\\x7F)]*");
    private static ObjectMapper mapper = null;
    /**
     * Initialize an ObjectMapper instance, to be used for JSON encoding and decoding
     *
     * @return mapper
     */
    public static ObjectMapper mapper() {
        if (HDBase.mapper == null) {
            HDBase.mapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        }
        return HDBase.mapper;
    }
    protected Properties config = new Properties();
    protected String apiBase = "/apiv4/";
    protected Map<String, String> detectedRuleKey = new  HashMap<>();
    protected char[] deviceUAFilterList = " _\\#-,./:\"".toCharArray();
    protected char[] extraUAFilterList = " ".toCharArray();
    protected String apikit = "Java 4.0.0";
    protected String loggerHost = "logger.handsetdetection.com";
    protected int loggerPort = 80;
    protected Reply reply = new Reply();
    protected byte[] rawreply = null;
    private String error = null;
    protected Map<String, Object> tree = new HashMap<>();
    protected HDStore store;
    protected HDCache cache;
    protected String realm;

    protected Map<String, List<String>> detectionConfigUA = ImmutableMap.of(
            "device-ua-order", ImmutableList.of("x-operamini-phone-ua", "x-mobile-ua", "device-stock-ua", "user-agent", "agent"),
            "platform-ua-order", ImmutableList.of("x-operamini-phone-ua", "x-mobile-ua", "device-stock-ua", "user-agent", "agent"),
            "browser-ua-order", ImmutableList.of("user-agent", "agent", "device-stock-ua"),
            "app-ua-order", ImmutableList.of("user-agent", "agent", "device-stock-ua"),
            "language-ua-order", ImmutableList.of("user-agent", "agent", "device-stock-ua"));

    protected Map<String, Map<String, List<List<String>>>> detectionConfigBI = ImmutableMap.of(
            "device-bi-order", ImmutableMap.of(
                    "android", ImmutableList.of(
                        ImmutableList.of("ro.product.brand", "ro.product.model"),
                        ImmutableList.of("ro.product.manufacturer", "ro.product.model"),
                        ImmutableList.of("ro-product-brand", "ro-product-model"),
                        ImmutableList.of("ro-product-manufacturer", "ro-product-model")),
                    "ios", ImmutableList.of(
                        ImmutableList.of("utsname.brand", "utsname.machine")
                    ),
                    "windows phone", ImmutableList.of(
                        ImmutableList.of("devicemanufacturer", "devicename")
                    )),
            "platform-bi-order", ImmutableMap.of(
                    "android", ImmutableList.of(
                        ImmutableList.of("hd-platform", "ro.build.version.release"),
                        ImmutableList.of("hd-platform", "ro-build-version-release"),
                        ImmutableList.of("hd-platform", "ro.build.id"),
                        ImmutableList.of("hd-platform", "ro-build-id")),
                    "ios", ImmutableList.of(
                        ImmutableList.of("uidevice.systemname", "uidevice.systemversion"),
                         ImmutableList.of("hd-platform", "uidevice.systemversion")
                    ),
                    "windows phone", ImmutableList.of(
                        ImmutableList.of("osname", "osversion"),
                        ImmutableList.of("hd-platform", "osversion")
                    )),
            "browser-bi-order", ImmutableMap.of(),
            "app-bi-order", ImmutableMap.of());

    protected Map<String, String> detectionLanguages = ImmutableMap.<String, String>builder()
        .put("af", "Afrikaans")
        .put("ar", "Arabic")
        .put("ar-ae", "Arabic (U.A.E.)")
        .put("ar-bh", "Arabic (Bahrain)")
        .put("ar-dz", "Arabic (Algeria)")
        .put("ar-eg", "Arabic (Egypt)")
        .put("ar-iq", "Arabic (Iraq)")
        .put("ar-jo", "Arabic (Jordan)")
        .put("ar-kw", "Arabic (Kuwait)")
        .put("ar-lb", "Arabic (Lebanon)")
        .put("ar-ly", "Arabic (libya)")
        .put("ar-ma", "Arabic (Morocco)")
        .put("ar-om", "Arabic (Oman)")
        .put("ar-qa", "Arabic (Qatar)")
        .put("ar-sa", "Arabic (Saudi Arabia)")
        .put("ar-sy", "Arabic (Syria)")
        .put("ar-tn", "Arabic (Tunisia)")
        .put("ar-ye", "Arabic (Yemen)")
        .put("as", "Assamese")
        .put("az", "Azeri")
        .put("be", "Belarusian")
        .put("bg", "Bulgarian")
        .put("bn", "Bengali")
        .put("ca", "Catalan")
        .put("cs", "Czech")
        .put("da", "Danish")
        .put("da-dk", "Danish")
        .put("de", "German (Germany)")
        .put("de-at", "German (Austria)")
        .put("de-ch", "German (Switzerland)")
        .put("de-de", "German (Germany)")
        .put("de-li", "German (Liechtenstein)")
        .put("de-lu", "German (Luxembourg)")
        .put("div", "Divehi")
        .put("el", "Greek")
        .put("en", "English")
        .put("en-au", "English (Australia)")
        .put("en-bz", "English (Belize)")
        .put("en-ca", "English (Canada)")
        .put("en-gb", "English (United Kingdom)")
        .put("en-ie", "English (Ireland)")
        .put("en-jm", "English (Jamaica)")
        .put("en-nz", "English (New Zealand)")
        .put("en-ph", "English (Philippines)")
        .put("en-tt", "English (Trinidad)")
        .put("en-us", "English (United States)")
        .put("en-za", "English (South Africa)")
        .put("en-zw", "English (Zimbabwe)")
        .put("es", "Spanish (Traditional Sort)")
        .put("es-ar", "Spanish (Argentina)")
        .put("es-bo", "Spanish (Bolivia)")
        .put("es-cl", "Spanish (Chile)")
        .put("es-co", "Spanish (Colombia)")
        .put("es-cr", "Spanish (Costa Rica)")
        .put("es-do", "Spanish (Dominican Republic)")
        .put("es-ec", "Spanish (Ecuador)")
        .put("es-es", "Spanish (Traditional Sort)")
        .put("es-gt", "Spanish (Guatemala)")
        .put("es-hn", "Spanish (Honduras)")
        .put("es-mx", "Spanish (Mexico)")
        .put("es-ni", "Spanish (Nicaragua)")
        .put("es-pa", "Spanish (Panama)")
        .put("es-pe", "Spanish (Peru)")
        .put("es-pr", "Spanish (Puerto Rico)")
        .put("es-py", "Spanish (Paraguay)")
        .put("es-sv", "Spanish (El Salvador)")
        .put("es-us", "Spanish (United States)")
        .put("es-uy", "Spanish (Uruguay)")
        .put("es-ve", "Spanish (Venezuela)")
        .put("et", "Estonian")
        .put("eu", "Basque")
        .put("fa", "Farsi")
        .put("fi", "Finnish")
        .put("fo", "Faeroese")
        .put("fr", "French (France)")
        .put("fr-be", "French (Belgium)")
        .put("fr-ca", "French (Canada)")
        .put("fr-ch", "French (Switzerland)")
        .put("fr-lu", "French (Luxembourg)")
        .put("fr-mc", "French (Monaco)")
        .put("gd", "Gaelic")
        .put("gu", "Gujarati")
        .put("he", "Hebrew")
        .put("hi", "Hindi")
        .put("hr", "Croatian")
        .put("hu", "Hungarian")
        .put("hy", "Armenian")
        .put("id", "Indonesian")
        .put("is", "Icelandic")
        .put("it", "Italian (Italy)")
        .put("it-ch", "Italian (Switzerland)")
        .put("it-it", "Italian (Italy)")
        .put("ja", "Japanese")
        .put("ka", "Georgian")
        .put("kk", "Kazakh")
        .put("kn", "Kannada")
        .put("ko", "Korean")
        .put("kok", "Konkani")
        .put("kz", "Kyrgyz")
        .put("ls", "Slovenian")
        .put("lt", "Lithuanian")
        .put("lv", "Latvian")
        .put("mk", "FYRO Macedonian")
        .put("ml", "Malayalam")
        .put("mn", "Mongolian (Cyrillic)")
        .put("mr", "Marathi")
        .put("ms", "Malay")
        .put("mt", "Maltese")
        .put("nb-no", "Norwegian (Bokmal)")
        .put("ne", "Nepali (India)")
        .put("nl", "Dutch (Netherlands)")
        .put("nl-be", "Dutch (Belgium)")
        .put("nn-no", "Norwegian (Nynorsk)")
        .put("no", "Norwegian (Bokmal)")
        .put("or", "Oriya")
        .put("pa", "Punjabi")
        .put("pl", "Polish")
        .put("pt", "Portuguese (Portugal)")
        .put("pt-br", "Portuguese (Brazil)")
        .put("rm", "Rhaeto-Romanic")
        .put("ro", "Romanian")
        .put("ro-md", "Romanian (Moldova)")
        .put("ru", "Russian")
        .put("ru-md", "Russian (Moldova)")
        .put("sa", "Sanskrit")
        .put("sb", "Sorbian")
        .put("sk", "Slovak")
        .put("sq", "Albanian")
        .put("sr", "Serbian")
        .put("sv", "Swedish")
        .put("sv-fi", "Swedish (Finland)")
        .put("sw", "Swahili")
        .put("sx", "Sutu")
        .put("syr", "Syriac")
        .put("ta", "Tamil")
        .put("te", "Telugu")
        .put("th", "Thai")
        .put("tn", "Tswana")
        .put("tr", "Turkish")
        .put("ts", "Tsonga")
        .put("tt", "Tatar")
        .put("uk", "Ukrainian")
        .put("ur", "Urdu")
        .put("us", "English (United States)")
        .put("uz", "Uzbek")
        .put("vi", "Vietnamese")
        .put("xh", "Xhosa")
        .put("yi", "Yiddish")
        .put("zh", "Chinese")
        .put("zh-cn", "Chinese (China)")
        .put("zh-hk", "Chinese (Hong Kong SAR)")
        .put("zh-mo", "Chinese (Macau SAR)")
        .put("zh-sg", "Chinese (Singapore)")
        .put("zh-tw", "Chinese (Taiwan)")
        .put("zu", "Zulu")
        .build();

    /**
     * Get reply status
     *
     * @return error status, 0 is Ok, anything else is probably not Ok
     **/
    int getStatus() {
        return this.reply.getStatus();
    }

    /**
     * Get reply message
     *
     * @return A message
     **/
    String getMessage() {
        return this.reply.getMessage();
    }

    /**
     * Get reply payload
     *
     * @return reply
     **/
    public Reply getReply() {
        return this.reply;
    }

    /**
     * Set a reply payload
     *
     * @param reply
     **/
    void setReply(Reply reply) {
        this.reply = reply;
    }

    /**
     * Error handling helper. Sets a message and an error code.
     *
     * @param status error code
     * @param msg message content
     * @return true if no error, or false otherwise.
     **/
    boolean setError(int status, String msg) {
        this.error = msg;
        this.reply.setStatus(status);
        this.reply.setMessage(msg);
        return status == 0;
    }

    /**
     * String cleanse for extras matching.=
     *
     * @param str
     * @return Cleansed string
     **/
    String extraCleanStr(String str) {
        for(char c: extraUAFilterList) {
            str = str.replace("" + c, "");
        }
        str = HDBase.CLEAN_STR_PATTERN.matcher(str).replaceAll("");
        return str.trim();
    }

    /**
     * Standard string cleanse for device matching
     *
     * @param str
     * @return cleansed string
     **/
    String cleanStr(String str) {
        for(char c: deviceUAFilterList) {
            str = str.replace("" + c, "");
        }
        str = HDBase.CLEAN_STR_PATTERN.matcher(str).replaceAll("");
        return str.trim();
    }

    // Log function - User defined functions can be supplied in the "logger" config variable.
    void log(String msg) {
        Logger.getLogger(HDBase.class.getName()).log(Level.INFO, null, msg);
    }

    /**
     * Makes requests to the various web services of Handset Detection.
     *
     * Note : suburl - the url fragment of the web service eg site/detect/${site_id}
     *
     * @param suburl
     * @param data
     * @return true on success, false otherwise
     */

    boolean remote(String suburl, Map<String, String> data) {
        return remote(suburl, data, "json");
    }

    /**
     * Makes requests to the various web services of Handset Detection.
     *
     * Note : suburl - the url fragment of the web service eg site/detect/${site_id}
     *rvices of Handset Detection.
     *
     * Note : suburl - the url fragment of
     * @param suburl
     * @param data
     * @param filetype
     * @return true on success, false otherwise
     */

    boolean remote(String suburl, Map<String, String> data, String filetype) {
        return remote(suburl, data, filetype, true);
    }

    /**
     * Makes requests to the various web services of Handset Detection.
     *
     * Note : suburl - the url fragment of the web service eg site/detect/${site_id}
     *
     * @param suburl
     * @param data
     * @param filetype
     * @param authRequired - Is authentication required ?
     * @return true on success, false otherwise
     */
    boolean remote(String suburl, Map<String, String> data, String filetype, boolean authRequired) {
        this.reply = new Reply();
        this.rawreply = new byte[] {};
        this.setError(0, "");

        if (data == null) {
            data = new HashMap<>();
        }

        String url = this.apiBase + suburl;
        int attempts = Integer.parseInt(this.config.getProperty("retries")) + 1;
        int tries = 0;

        String requestdata;
        try {
            requestdata = HD4.mapper().writeValueAsString(data);
        } catch (JsonProcessingException ex) {
            return this.setError(299, "Error : Cannot encode request.");
        }

        boolean success = false;
        while(tries++ < attempts && !success) {
            this.rawreply = this.post(this.config.getProperty("apiServer"), url, requestdata, authRequired);
            if (this.rawreply != null) {
                if (filetype.equals("json")) {
                    try {
                        this.reply = HD4.mapper().readValue(this.rawreply, Reply.class);
                    } catch (IOException ex) {
                        this.setError(299, "Error : " + ex.getMessage());
                    }
                    if (this.reply == null) {
                        this.setError(299, "Error : Empty Reply.");
                    } else if (this.reply.getStatus() == null) {
                        this.setError(299, "Error : No status set in reply");
                    } else if (this.reply.getStatus() != 0) {
                        this.setError(this.reply.getStatus(), this.reply.getMessage());
                        tries = attempts + 1;
                    } else {
                        success = true;
                    }
                } else {
                    success = true;
                }
            }
        }
        return success;
    }

    /**
     * Post data to remote server
     *
     * Modified version of PHP Post from From http://www.enyem.com/wiki/index.php/Send_POST_request_(PHP)
     * Thanks dude !
     *
     * @param server Server name
     * @param url URL name
     * @param jsondata Data in json format
     * @return false on failue (sets error), or string on success.
     **/
    private byte[] post(String server, String url, String jsondata) {
        return post(server, url, jsondata, true);
    }

    /**
     * Post data to remote server
     *
     * Modified version of PHP Post from From http://www.enyem.com/wiki/index.php/Send_POST_request_(PHP)
     * Thanks dude !
     *
     * @param server Server name
     * @param url URL name
     * @param jsondata Data in json format
     * @param authRequired Is suthentication reguired ?
     * @return false on failue (sets error), or string on success.
     **/
    private byte[] post(String server, String url, String jsondata, boolean authRequired) {
        String host = server;
        int port = 80;
        int timeout = 1_000 * Integer.parseInt(this.config.getProperty("timeout"));
        String username = this.config.getProperty("username");
        String nc = "00000001";
        String snonce = this.realm;

        long time = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        String cnonce = DigestUtils.md5Hex("" + time + this.config.getProperty("secret"));
        String qop = "auth";

        if (this.config.getProperty("useProxy").equals("true")) {
            host = this.config.getProperty("proxy.server");
            port = Integer.parseInt(this.config.getProperty("proxy.port"));
        }



        // AuthDigest Components
        // http://en.wikipedia.org/wiki/Digest_access_authentication
        String ha1 = DigestUtils.md5Hex(username + ":" + this.realm + ":" + this.config.getProperty("secret"));
        String ha2 = DigestUtils.md5Hex("POST:" + url);
        String response = DigestUtils.md5Hex(ha1 + ":" + snonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);


        // Use HTTP/1.0 (to disable content chunking on large replies).
        StringBuilder out = new StringBuilder();
        out.append("POST ").append(url).append(" HTTP/1.0\r\n");
        out.append("Host: ").append(server).append("\r\n");
        if (Boolean.parseBoolean(this.config.getProperty("useProxy")) && this.config.getProperty("proxy.user") != null && this.config.getProperty("proxy.pass") != null) {
            out.append("Proxy-Authorization:Basic ");
            out.append(Base64.getEncoder().encodeToString(
                (this.config.get("proxy.user")  + ":" + this.config.get("proxy.pass")).getBytes()
            ));
            out.append("\r\n");
        }
        out.append("Content-Type: application/json\r\n");
        // Pre-computed auth credentials, saves waiting for the auth challenge hence makes things round trip time 50% faster.
        if (authRequired) {
            out.append("Authorization: Digest ");
            out.append("username=\"").append(username).append("\", ");
            out.append("realm=\"").append(this.realm).append("\", ");
            out.append("nonce=\"").append(snonce).append("\", ");
            out.append("uri=\"").append(url).append("\", ");
            out.append("qop=").append(qop).append(", ");
            out.append("nc=").append(nc).append(", ");
            out.append("cnonce=\"").append(cnonce).append("\", ");
            out.append("response=\"").append(response).append("\", ");
            out.append("opaque=\"").append(this.realm).append("\"");
            out.append("\r\n");
        }
        out.append("Content-length: ").append(jsondata.length()).append("\r\n\r\n");
        out.append(jsondata).append("\r\n\r\n");

        String r;

        try (Socket fp = new Socket()) {
            // * Connect *
            fp.connect(new InetSocketAddress(host, port), timeout);
            fp.setSoTimeout(timeout);
            DataOutputStream os = new DataOutputStream(fp.getOutputStream());
            os.writeBytes(out.toString());
            InputStream is = fp.getInputStream();
            byte[] buffer = new byte[1_024];
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            while (true) {
                int n = is.read(buffer);
                if (n < 0) {
                    break;
                }
                bytes.write(buffer, 0, n);
            }
            r = bytes.toString("ISO-8859-1");

        } catch (IOException ex) {
            this.setError(299, "Error connecting to " + host + ", port " + port + " timeout " + timeout + " : " + ex.getMessage());
            return null;
        }

        String[] hunks = r.split("\r\n\r\n");

        if (hunks == null || hunks.length < 2) {
            this.setError(299, "Error : Reply is too short.");
            return null;
        }

        String header = hunks[hunks.length - 2];
        String body = hunks[hunks.length - 1];
        // String[] headers = header.split("\n");

        if (body.length() > 0) {
            try {
                return body.getBytes("ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                this.setError(299, "Error : Unsupported encoding.");
                return null;
            }
        }

        this.setError(299, "Error : Reply body is empty.");
        return null;
    }

    /**
     * Helper for determining if a header has BiKeys
     *
     * @param header
     * @return platform name on success, null otherwise
     **/
    String hasBiKeys(Map<String, String> headers) {
        Map<String, List<List<String>>> biKeys = this.detectionConfigBI.get("device-bi-order");

        Set<String> dataKeys = headers.keySet();

        // Fast check
        if (headers.containsKey("agent")) {
            return null;
        }

        if (headers.containsKey("user-agent")) {
            return null;
        }

        for(String platform: biKeys.keySet()) {
            List<List<String>> set = biKeys.get(platform);
            for(List<String> tuple: set) {
                int count = 0;
                int total = tuple.size();
                for(String item: tuple) {
                    if (dataKeys.contains(item)) {
                        count++;
                    }
                    if (count == total) {
                        return platform;
                    }
                }
            }
        }
        return null;
    }

    /**
     * The heart of the detection process
     *
     * @param header The type of header we are matching against - user-agent type headers use a sieve matching, all others are hash matching.
     * @param value The http headers sanitised value (could be a user-agent or some other x- header value)
     * @param subtree The 0 or 1 for devices (isGeneric), category name for extras ("platform", "browser", "app")
     * @param actualHeader Unused (optimized away)
     * @param category : One of "device", "platform", "browser" or "app"
     * @return node (which is an id) on success, null otherwise
     */
    String getMatch(String header, String value, String subtree, String actualHeader, String category) {
        int f = 0;
        int r = 0;
        String treetag;
        if (category.equals("device")) {
            treetag = header + subtree;
        } else {
            treetag = header + subtree;
        }

        // Fetch branch before validating params to confirm local files are installed correctly.
        Map<String, Object> branch = this.getBranch(treetag);
        if (branch == null) {
            return null;
        }

        if (value.length() < 4) {
            return null;
        }

        if (header.equals("user-agent")) {
            // Sieve matching strategy
            for(String order: branch.keySet()) {
                @SuppressWarnings("unchecked")
                Map<String, Map<String, String>> filters = (Map<String, Map<String, String>>)branch.get(order);
                for(String filter: filters.keySet()) {
                    Map<String, String> matches = filters.get(filter);
                    ++f;
                    if (value.contains(filter)) {
                        for (String match: matches.keySet()) {
                            String node = matches.get(match);
                            ++r;
                            if(value.contains(match)) {
                                this.detectedRuleKey.put(category, this.cleanStr(header) + ":" + this.cleanStr(filter) + ":" + this.cleanStr(match));
                                return node;
                            }
                        }
                    }
                }
            }
        } else {
            // Hash matching strategy
            if (branch.containsKey(value)) {
                String node = (String)branch.get(value);
                return node;
            }
        }
        return null;
    }

    /**
     * Find a branch for the matching process
     *
     * @param branch The name of the branch to find
     * @return an assoc array on success, null otherwise.
     */

    Map<String, Object> getBranch(String branchName) {
        if (this.tree.containsKey(branchName)) {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)this.tree.get(branchName);
            return map;
        }
        Map<String, Object> branch;
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>)this.store.read(branchName,
                    new TypeReference<Map<String, Object>>() {});
            branch = map;
        } catch (NoSuchFileException ex) {
            this.setError(299, "Branch not found. Is it installed ?");
            return null;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        if (branch != null) {
            this.tree.put(branchName, branch);
        }
        return branch;
    }
    /**
     * Send a message via UDP, used if logUnknown is set in config and running in Ultimate (local) mode.
     *
     * @param headers
     **/
    void sendRemoteSyslog(Map<String, String> headers) {
        headers.put("version", System.getProperty("java.version"));
        headers.put("apikit", this.apikit);
        try (DatagramSocket sock = new DatagramSocket()) {
            byte[] message = ("<22>" + HD4.mapper().writeValueAsString(headers)).getBytes();
            sock.send(new DatagramPacket(message,message.length,
                    InetAddress.getByName(this.loggerHost), this.loggerPort));
        } catch (IOException ex) {
            // Pass
        }
    }

}
