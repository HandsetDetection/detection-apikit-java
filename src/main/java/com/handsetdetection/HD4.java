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
import com.google.common.collect.ImmutableMap;
import com.handsetdetection.data.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 *
 */
public class HD4 extends HDBase {
    Map<String, String> detectRequest = new HashMap<>();
    String error = "";
    boolean debug = false;
    String configFile = "hdconfig.properties";
    Map<String, String> defaultConfig = ImmutableMap.<String, String>builder()
        .put("username", "")
        .put("secret", "")
        .put("siteId", "")
        .put("useProxy", "false")
        .put("proxy.server", "")
        .put("proxy.port", "")
        .put("proxy.user", "")
        .put("proxy.pass", "")
        .put("useLocal", "false")
        .put("apiServer", "api.handsetdetection.com")
        .put("timeout", "10")
        .put("debug", "false")
        .put("filesdir", "")
        .put("retries", "3")
        .put("cacheRequests", "false")
        .put("geoip", "false")
        .put("logUnknown", "false")
        .build();

    private HDDevice device;

    /**
     * Constructor for the class HD4
     */
    public HD4() {
        this(new Properties());
    }

    /**
     * Constructor for the class HD4
     *
     * @param config can be an array of config options
     */
    public HD4(Properties config) {
        super();
        this.realm = "APIv4";
        this.setConfig(this.defaultConfig);
        this.setConfig(config);
        if (this.getUsername() == null || this.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Error : API username not set. Download a premade config from your Site Settings.");
        } else if (this.config.getProperty("secret") == null || this.config.getProperty("secret").isEmpty()) {
            throw new IllegalArgumentException("Error : API secret not set. Download a premade config from your Site Settings.");
        }
    }

    /**
     * Constructor for the class HD4
     *
     * @param config can be a fully qualified path to an alternate config file.
     */
    public HD4(String config) {
        super();
        this.realm = "APIv4";
        this.setConfig(this.defaultConfig);
        this.setConfig(config);
        if (this.getUsername() == null || this.getUsername().isEmpty()) {
            throw new IllegalArgumentException("Error : API username not set. Download a premade config from your Site Settings.");
        } else if (this.config.getProperty("secret") == null || this.config.getProperty("secret").isEmpty()) {
            throw new IllegalArgumentException("Error : API secret not set. Download a premade config from your Site Settings.");
        }
    }

    void setLocalDetection(boolean enable){
        this.config.setProperty("useLocal", "" + enable);
    }

    void setProxyUser(String user){
        this.config.setProperty("proxy.user", user);
    }

    void setProxyPass(String pass){
        this.config.setProperty("proxy.pass", pass);
    }

    void setUseProxy(String proxy){
        this.config.setProperty("useProxy", proxy);
    }

    void setProxyServer(String name) {
        this.config.setProperty("proxy.server", name);
    }

    void setProxyPort(int number) {this.config.setProperty("proxy.port", "" + number);
    }

    void setSecret(String secret) {
        this.config.setProperty("secret", secret);
    }

    void setUsername(String user) {
        this.config.setProperty("username", user);
    }

    /**
     * Set a connection timeout value
     *
     * @param timeout timeout in seconds
     **/
    public void setTimeout(int timeout) {
        this.config.setProperty("timeout", "" + timeout);
    }

    /**
     * Set a variable to be used for detection
     *
     * @param key variable name
     * @param value variable value
     **/
    public void setDetectVar(String key, String value) {
        this.detectRequest.put(key, value);
    }

    void setSiteId(int siteid) {
        this.config.setProperty("siteId", "" + siteid);
    }

    void setUseLocal(boolean value) {
        this.config.setProperty("useLocal", "" + value);
    }

    void setApiServer(String value) {
        this.config.setProperty("apiServer", value);
    }

    boolean getLocalDetection() {
        return Boolean.parseBoolean(this.config.getProperty("useLocal"));
    }

    String getProxyUser(){
        return this.config.getProperty("proxy.user");
    }

    String getProxyPass(){
        return this.config.getProperty("proxy.pass");
    }

    boolean getUseProxy(){
        return Boolean.parseBoolean(this.config.getProperty("useProxy"));
    }

    String getProxyServer(){
        return this.config.getProperty("proxy.server");
    }

    int getProxyPort(){
        return Integer.parseInt(this.config.getProperty("proxy.port"));
    }

    /**
     * Get error
     *
     * @return error message
     **/
    public String getError() {
        return this.error;
    }

    // Backwards compatibility with previous api kits.
    String getErrorMsg() {
        return this.error;
    }

    String getSecret() {
        return this.config.getProperty("secret");
    }

    final String getUsername() {
        return this.config.getProperty("username");
    }

    int getTimeout() {
        return Integer.parseInt(this.config.getProperty("timeout"));
    }

    /**
     * Get raw reply
     *
     * @return raw reply
     **/
    public byte[] getRawReply() {
        return this.rawreply;
    }

    int getSiteId() {
        return Integer.parseInt(this.config.getProperty("siteId"));
    }

    boolean getUseLocal() {
        return Boolean.parseBoolean(this.config.getProperty("useLocal"));
    }

    String getApiServer() {
        return this.config.getProperty("apiServer");
    }

    Map<String, String> getDetectRequest() {
        return this.detectRequest;
    }

    String getFilesDir() {
        return this.config.getProperty("filesdir");
    }

    /**
     * Set config
     *
     * @param config An assoc array of config data
     * @return true on success, false otherwise
     **/
    private boolean setConfig(Map<String, String> config) {

        config.keySet().forEach((key) -> {
            this.config.setProperty(key, config.get(key));
        });

        this.store = HDStore.getInstance();
        this.store.setConfig(this.config, true);
        this.cache = new HDCache(this.config);
        this.device = new HDDevice(this.config);
        return true;
    }

     /**
     * Set config
     *
     * @param config An assoc array of config data
     * @return true on success, false otherwise
     **/
    private boolean setConfig(Properties config) {

        config.keySet().forEach((key) -> {
            this.config.setProperty((String)key, config.getProperty((String)key));
        });

        this.store = HDStore.getInstance();
        this.store.setConfig(config, true);
        this.cache = new HDCache(config);
        this.device = new HDDevice(config);
        return true;
    }

    /**
     * Set config file
     *
     * @param
     * @return true on success, false otherwise
     **/
    private boolean setConfig(String cfg) {
        try (InputStream input = new FileInputStream(cfg);) {
            // load a properties file
            this.config.load(input);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        this.store = HDStore.getInstance();
        this.store.setConfig(this.config, true);
        this.cache = new HDCache(this.config);
        this.device = new HDDevice(this.config);
        return true;
    }

    /**
     * List all known vendors
     *
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceVendors() {
        if (!this.getUseLocal()) {
            return this.remote("device/vendors", null);
        }
        if (this.device.localVendors()) {
            this.reply = this.device.getReply();
        }
        return this.setError(this.device.getStatus(), this.device.getMessage());
    }

    /**
     * List all models for a given vendor
     *
     * @param vendor The device vendor eg Apple
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceModels(String vendor) {
        if (!this.getUseLocal()) {
            return this.remote("device/models/" + vendor, null);
        }

        if (this.device.localModels(vendor)) {
            this.reply = this.device.getReply();
        }

        return this.setError(this.device.getStatus(), this.device.getMessage());
    }

    /**
     * Find properties for a specific device
     *
     * @param vendor The device vendor eg. Nokia
     * @param model The device model eg. N95
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceView(String vendor, String model) {
        if (!this.getUseLocal()) {
            return this.remote("device/view/" + vendor + "/" + model, null);
        }

        if (this.device.localView(vendor, model)) {
            this.reply = this.device.getReply();
        }

        return this.setError(this.device.getStatus(), this.device.getMessage());
    }

    /**
     * Find which devices have property "X".
     *
     * @param key Property to inquire about eg "network", "connectors" etc...
     * @param value Value to inquire about eg "CDMA", "USB" etc ...
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceWhatHas(String key, String value) {
        if (!this.getUseLocal()) {
            return this.remote("device/whathas/" + key + "/" + value, null);
        }

        if (this.device.localWhatHas(key, value)) {
            this.reply = this.device.getReply();
        }

        return this.setError(this.device.getStatus(), this.device.getMessage());
    }

    /**
     * Device Detect
     *
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceDetect() {
        return deviceDetect(new HashMap<>());
    }
    /**
     * Device Detect
     *
     * @param data Data for device detection : HTTP Headers usually
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    public boolean deviceDetect(Map<String, String> data) {
        data = new HashMap<>(data);
        int id = !data.containsKey("id") ? this.getSiteId() : Integer.parseInt(data.get("id"));
        data.remove("id");
        Map<String, String> requestBody = !data.isEmpty() ? data : this.detectRequest;
        String fastKey = "";

        // If caching enabled then check cache
        if (this.config.getProperty("cacheRequests") != null && this.config.getProperty("cacheRequests").equals("true")) {
            SortedMap<String, String> headers = new TreeMap<>();
            requestBody.keySet().forEach((key) -> {
                headers.put(key.toLowerCase(), requestBody.get(key));
            });
            try {
                fastKey = HD4.mapper().writeValueAsString(headers).replace(" ", "");
            } catch (JsonProcessingException ex) {
                return this.setError(299, "Error : Cannot encode headers.");
            }
            Reply r = null;
            try {
                r = (Reply)this.cache.read(fastKey, new TypeReference<Reply>() {});
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            if (r != null) {
                this.reply = r;
                this.rawreply = new byte[] {};
                return this.setError(0, "OK");
            }
        }

        boolean result;

        if (this.getUseLocal()) {
            result = this.device.localDetect(requestBody);
            this.setError(this.device.getStatus(), this.device.getMessage());
            this.setReply(this.device.getReply());
            // Log unknown headers if enabled
            if (this.config.getProperty("logUnknown").equals("true") && ! result) {
                this.sendRemoteSyslog(requestBody);
            }
        } else {
            result = this.remote("device/detect/" + id, requestBody);
        }

        // If we got a result then cache it
        if (result && this.config.get("cacheRequests") != null && this.config.get("cacheRequests").equals("true")) {
            try {
                this.cache.write(fastKey, this.reply);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        return result;
    }

    /**
     * Fetch an archive from handset detection which contains all the device specs and matching trees as individual json files.
     *
     * @return hd_specs data on success, false otherwise
     */
    public boolean deviceFetchArchive() {
        byte[] data;
        if (this.config.getProperty("localArchive") != null) {
            try {
                data = this.rawreply = Files.readAllBytes(Paths.get(this.config.getProperty("localArchive")));
            } catch (IOException ex) {
                return this.setError(299, ex.getMessage());
            }
        } else {
            if (! this.remote("device/fetcharchive", new HashMap<>(), "zip")) {
                return false;
            }
            data = this.getRawReply();
        }
        if (data == null) {
            return this.setError(299, "Error : FetchArchive failed. Bad Download. File is zero length");
        } else if (data.length < 9_000_000) {
            Map<String, String> trythis;
            try {
                trythis = HD4.mapper().readValue(data, new TypeReference<Map<String, String>>() {});
                if (trythis != null && trythis.containsKey("status") && trythis.containsKey("message")) {
                    return this.setError(Integer.parseInt(trythis.get("status")), trythis.get("message"));
                }
            } catch (IOException ex) {
                return this.setError(299, ex.getMessage());
            }
            return this.setError(299, "Error : FetchArchive failed. Bad Download. File too short at " + data.length + " bytes.");
        }
        return this.installArchive(data);
    }

    /**
     * Community Fetch Archive - Fetch the community archive version
     *
     * @return hd_specs data on success, false otherwise
     */
    public boolean communityFetchArchive() {
        byte[] data;
        if (this.config.getProperty("localCommunityArchive") != null) {
            try {
                data = this.rawreply = Files.readAllBytes(Paths.get(this.config.getProperty("localCommunityArchive")));
            } catch(IOException ex) {
                return this.setError(299, ex.getMessage());
            }
        } else {
            if (! this.remote("community/fetcharchive", null, "zip", false)) {
                return false;
            }
            data = this.getRawReply();
        }
        if (data == null) {
            return this.setError(299, "Error : FetchArchive failed. Bad Download. File is zero length");
        } else if (data.length < 9_000_000) {
            Map<String, String> trythis;
            try {
                trythis = HD4.mapper().readValue(data, new TypeReference<Map<String, String>>() {});
                if (trythis != null && trythis.containsKey("status") && trythis.containsKey("message")) {
                    return this.setError(Integer.parseInt(trythis.get("status")), trythis.get("message"));
                }
            } catch (IOException ex) {
                return this.setError(299, ex.getMessage());
            }
            return this.setError(299, "Error : FetchArchive failed. Bad Download. File too short at " + data.length + " bytes.");
        }
        return this.installArchive(data);
    }
    /**
     * Install an ultimate archive file
     *
     * @param file Fully qualified path to file
     * @return true on success, false otherwise
     **/
    boolean installArchive(byte[] bytes) {
        int nEntries = 0;
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(bytes))) {
            byte[] buf = new byte[1_024];
            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                String filename = entry.getName();
                try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                    int n;
                    while ((n = zip.read(buf, 0, buf.length)) > -1) {
                        out.write(buf, 0, n);
                    }
                    this.store.moveIn(out.toByteArray(), filename);
                }
                zip.closeEntry();
                nEntries++;
                entry = zip.getNextEntry();
            }
        } catch (IOException ex) {
            return this.setError(299, "Error : Failed to unzip archive, Message : " + ex.getMessage());
        }
        if (nEntries == 0) {
            return this.setError(299, "Error : No files could be read from archive.");
        }
        return true;
    }

    /**
     * This method can indicate if using the js Helper would yield more accurate results.
     *
     * @param headers headers
     * @return true if helpful, false otherwise.
     **/
    public boolean isHelperUseful(Map<String, String> headers) {
        return this.device.isHelperUseful(headers);
    }
}
