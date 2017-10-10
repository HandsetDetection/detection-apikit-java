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
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.google.common.collect.ImmutableList;
import com.handsetdetection.data.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;


/**
 *
 */
public class HDDevice extends HDBase {
    private static final Pattern BIN_START_PATTERN = Pattern.compile("^[\\| \\t\\n\\r\\u0000\\u000B]*");
    private static final Pattern BIN_END_PATTERN = Pattern.compile("[\\| \\t\\n\\r\\u0000\\u000B]*$");
    Device device = null;
    Extra platform = null;
    Extra browser = null;
    Extra app = null;
    Extra language = null;
    List<Map<String, Integer>>  ratingResult = null;
    HDExtra extra = null;
    Properties config = null;
    Map<String, String> buildInfo = null;
    final private Map<String, String> deviceHeaders = new HashMap<>();
    final private Map<String, String> extraHeaders = new HashMap<>();
    private List<BeanPropertyDefinition> deviceBeanProps;

    HDDevice() {
        super();
        this.setConfig(new Properties());
    }

    HDDevice(Properties config) {
        super();
        this.setConfig(config);
    }

    /**
     * Set Config sets config vars
     *
     * @param config A config assoc array.
     * @return true on success, false otherwise
     **/
    private void setConfig(Properties config) {
        this.config = config;
        this.store = HDStore.getInstance();
        this.store.setConfig(this.config);
        this.extra = new HDExtra(this.config);
        BeanDescription descr = HD4.mapper().getSerializationConfig().introspect(
                HD4.mapper().getTypeFactory().constructType(HdSpecs.class));
        this.deviceBeanProps = descr.findProperties();
    }
    /**
     * Find all device vendors
     *
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    boolean localVendors() {
        this.reply = new Reply();
        SortedSet<String> vendorNames = new TreeSet<>();
        int i = 0;
        for(Device dev: this.store) {
            vendorNames.add(dev.getHdSpecs().getGeneralVendor());
        }
        this.reply.setVendor(new ArrayList<>(vendorNames));
        return this.setError(0, "OK");
    }

    /**
     * Find all models for the specified vendor
     *
     * @param vendor The device vendor
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    boolean localModels(String vendor) {
        this.reply = new Reply();
        vendor = vendor.toLowerCase();
        SortedSet<String> modelNames = new TreeSet<>();
        String trim = "";
        for(Device dev: this.store) {
            if (vendor.equals(dev.getHdSpecs().getGeneralVendor().toLowerCase())) {
                modelNames.add(dev.getHdSpecs().getGeneralModel());
            }
            String key = vendor + " ";
            if (!dev.getHdSpecs().getGeneralAliases().isEmpty()) {
                dev.getHdSpecs().getGeneralAliases().stream().filter((alias_item) -> (alias_item.toLowerCase().startsWith(key.toLowerCase()))).forEachOrdered((alias_item) -> {
                    modelNames.add(alias_item.replace(key, ""));
                });
            }
        }
        this.reply.setModel(new ArrayList<>(modelNames));
        return this.setError(0, "OK");
    }

    /**
     * Finds all the specs for a specific device
     *
     * @param vendor The device vendor
     * @param model The device model
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    boolean localView(String vendor, String model) {
        this.reply = new Reply();
        vendor = vendor.toLowerCase();
        model = model.toLowerCase();
        for (Device dev : this.store) {
            if (vendor.equals(dev.getHdSpecs().getGeneralVendor().toLowerCase()) &&
                    model.equals(dev.getHdSpecs().getGeneralModel().toLowerCase())) {
                this.reply.setDevice(dev.getHdSpecs());
                return this.setError(0, "OK");
            }
        }
        return this.setError(301, "Nothing found");
    }

    /**
     * Finds all devices that have a specific property
     *
     * @param key
     * @param value
     * @return true on success, false otherwise. Use getReply to inspect results on success.
     */
    boolean localWhatHas(String key, String value) {
        value = value.toLowerCase();
        BeanPropertyDefinition prop = null;
        for(BeanPropertyDefinition p: this.deviceBeanProps) {
            if (p.getName().equals(key)) {
                prop = p;
                break;
            }
        }
        if (prop == null) {
            return this.setError(299, "No such property.");
        }
        List<DeviceSummary> matchingDevices = new ArrayList<>();
        for(Device dev: this.store) {
            Object obj = null;
            try {
                obj = prop.getGetter().callOn(dev.getHdSpecs());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
            if (obj == null) {
                continue;
            }
            boolean match = false;
            if (obj.getClass().equals(List.class)) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>)obj;
                for(String v: list) {
                    if (v.toLowerCase().contains(value)) {
                        match = true;
                    }
                }
            } else if (obj.getClass().equals(String.class)) {
                @SuppressWarnings("unchecked")
                String str = (String)obj;
                if (str.toLowerCase().contains(value)) {
                    match = true;
                }
            } else {
                // Pass
            }
            if (match) {
                DeviceSummary ds = new DeviceSummary();
                ds.setId(dev.getId());
                ds.setGeneralVendor(dev.getHdSpecs().getGeneralVendor());
                ds.setGeneralModel(dev.getHdSpecs().getGeneralModel());
                matchingDevices.add(ds);
            }
        }
        this.reply.setDevices(matchingDevices);
        return this.setError(0, "OK");
    }

    /**
     * Perform a local detection
     *
     * @param headersArg HTTP headers as an assoc array. keys are standard http header names eg user-agent, x-wap-profile
     * @return true on success, false otherwise
     */
    boolean localDetect(Map<String, String> headersArg) {
        this.device = null;
        this.platform = null;
        this.browser = null;
        this.app = null;
        this.language = null;
        this.ratingResult = null;
        this.detectedRuleKey = new HashMap<>();
        this.reply = new Reply();
        this.reply.setStatus(0);
        this.reply.setMessage("");

        // lowercase headers on the way in.
        Map<String, String> headers = new HashMap<>();
        headersArg.entrySet().forEach((k) -> {
            headers.put(k.getKey().toLowerCase(), k.getValue().toLowerCase());
        });
        String hardwareInfo = headers.get("x-local-hardwareinfo");
        headers.remove("x-local-hardwareinfo");

        // Is this a native detection or a HTTP detection ?
        if (this.hasBiKeys(headers) != null) {
           return this.v4MatchBuildInfo(headers);
        }
        return this.v4MatchHttpHeaders(headers, hardwareInfo);
    }

    /**
     * Returns the rating score for a device based on the passed values
     *
     * @param deviceId : The ID of the device to check.
     * @param props Properties extracted from the device (display_x, display_y etc .. )
     * @return of rating information. (which includes "score" which is an int value that is a percentage.)
     */
    Map<String, Integer> findRating(String deviceId, Map<String, Integer> props) {
        Device dev = this.findById(deviceId);
        if (dev.getHdSpecs() == null) {
            return null;
        }

        HdSpecs specs = dev.getHdSpecs();

        int total = 70;
        Map<String, Integer> result = new HashMap<>();

        // Display Resolution - Worth 40 points if correct
        result.put("resolution", 0);
        if (props.containsKey("display_x") && props.containsKey("display_y")) {
            int pMaxRes = Math.max(props.get("display_x"), props.get("display_y"));
            int pMinRes = Math.min(props.get("display_x"), props.get("display_y"));
            int sMaxRes = Math.max(Integer.parseInt(specs.getDisplayX()), Integer.parseInt(specs.getDisplayY()));
            int sMinRes = Math.min(Integer.parseInt(specs.getDisplayX()), Integer.parseInt(specs.getDisplayY()));
            if (pMaxRes == sMaxRes && pMinRes == sMinRes) {
                // Check for native match first
                result.put("resolution", 40);
            } else {
                // Check for css dimensions match.
                // css dimensions should be display_[xy] / display_pixel_ratio or others in other modes.
                // Devices can have multiple css display modes (eg. iPhone 6, iPhone 6+ Zoom mode)
                List<String> cssScreenSizes = specs.getDisplayCssScreenSizes() == null ? new ArrayList<>() : specs.getDisplayCssScreenSizes();
                for (String size: cssScreenSizes) {
                    String[] dimensions = size.split("x");
                    int tmpMaxRes = Math.max(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
                    int tmpMinRes = Math.min(Integer.parseInt(dimensions[0]), Integer.parseInt(dimensions[1]));
                    if (pMaxRes == tmpMaxRes && pMinRes == tmpMinRes) {
                        result.put("resolution", 40);
                        break;
                    }
                }
            }
        }

        // Display pixel ratio - 20 points
        result.put("display_pixel_ratio", 20);
        if (props.get("display_pixel_ratio") != null) {
            // Note : display_pixel_ratio will be a string stored as 1.33 or 1.5 or 2, perhaps 2.0 ..
            DecimalFormat df = new DecimalFormat("#.##");
            if (df.format(Float.parseFloat(specs.getDisplayPixelRatio()))
                    .equals(df.format(props.get("display_pixel_ratio") / 100f))) {
                result.put("display_pixel_ratio", 40);
            }
        }

        // Benchmark - 10 points - Enough to tie break but not enough to overrule display or pixel ratio.
        result.put("benchmark", 0);
        if (props.containsKey("benchmark")) {
            if (specs.getBenchmarkMin() != null && specs.getBenchmarkMax() != null) {
                if (props.get("benchmark") >= specs.getBenchmarkMin() && props.get("benchmark") <= specs.getBenchmarkMax()) {
                    // Inside range
                    result.put("benchmark", 10);
                }
            }
        }

        result.put("score", result.values().stream().mapToInt(Integer::intValue).sum());
        result.put("possible", total);
        result.put("_id", Integer.parseInt(deviceId));

        // Distance from mean used in tie breaking situations if two devices have the same score.
        result.put("distance", 100_000);
        if (specs.getBenchmarkMin() != null && specs.getBenchmarkMax() != null && props.get("benchmark") != null) {
            result.put("distance", (int)Math.abs((specs.getBenchmarkMin() + specs.getBenchmarkMax()) / 2 - props.get("benchmark")));
        }
        return result;
    }

    /**
     * Overlays specs onto a device
     *
     * @param specsField : Either "platform", "browser", "language"
     **/
    void specsOverlay(String specsField, Device device, Extra specs) {
        if (specs != null) {
            switch (specsField) {
                case "platform" :
                    if (specs.getHdSpecs().getGeneralPlatform() != null && !specs.getHdSpecs().getGeneralPlatform().isEmpty() && specs.getHdSpecs().getGeneralPlatformVersion()!= null && !specs.getHdSpecs().getGeneralPlatformVersion().isEmpty()) {
                        device.getHdSpecs().setGeneralPlatform(specs.getHdSpecs().getGeneralPlatform());
                        device.getHdSpecs().setGeneralPlatformVersion(specs.getHdSpecs().getGeneralPlatformVersion());
                    } else if (specs.getHdSpecs().getGeneralPlatform() != null && !specs.getHdSpecs().getGeneralPlatform().equals(device.getHdSpecs().getGeneralPlatform())) {
                        device.getHdSpecs().setGeneralPlatform(specs.getHdSpecs().getGeneralPlatform());
                        device.getHdSpecs().setGeneralPlatformVersion("");
                    }
                    break;

                case "browser" :
                    if (specs.getHdSpecs().getGeneralBrowser() != null) {
                        device.getHdSpecs().setGeneralBrowser(specs.getHdSpecs().getGeneralBrowser());
                        device.getHdSpecs().setGeneralBrowserVersion(specs.getHdSpecs().getGeneralBrowserVersion());
                    }
                    break;

                case "app" :
                    if (specs.getHdSpecs().getGeneralApp() != null) {
                        device.getHdSpecs().setGeneralApp(specs.getHdSpecs().getGeneralApp());
                        device.getHdSpecs().setGeneralAppVersion(specs.getHdSpecs().getGeneralAppVersion());
                        device.getHdSpecs().setGeneralAppCategory(specs.getHdSpecs().getGeneralAppCategory());
                    }
                    break;

                case "language" :
                    if (specs.getHdSpecs().getGeneralLanguage() != null) {
                        device.getHdSpecs().setGeneralLanguage(specs.getHdSpecs().getGeneralLanguage());
                        device.getHdSpecs().setGeneralLanguageFull(specs.getHdSpecs().getGeneralLanguageFull());
                    }
                    break;
            }
        }
    }

    /**
     * Takes a string of onDeviceInformation and turns it into something that can be used for high accuracy checking.
     *
     * Strings a usually generated from cookies, but may also be supplied in headers.
     * The format is $w;$h;$r;$b where w is the display width, h is the display height, r is the pixel ratio and b is the benchmark.
     * display_x, display_y, display_pixel_ratio, general_benchmark
     *
     * @param hardwareInfo String of light weight device property information, separated by ':'
     * @return partial specs array of information we can use to improve detection accuracy
     **/
    Map<String, Integer> infoStringToArray(String hardwareInfo) {
        Map<String, Integer> response = new HashMap<>();
        // Remove the header or cookie name from the string 'x-specs1a='
        if (hardwareInfo.contains("=")) {
            String[] tokens = hardwareInfo.split("=");
            if (tokens[1].isEmpty()) {
                return response;
            } else {
                hardwareInfo = tokens[1];
            }
        }

        String[] info = hardwareInfo.split(":");
        if (info.length != 4) {
            return response;
        }
        response.put("display_x", Integer.parseInt(info[0].trim()));
        response.put("display_y", Integer.parseInt(info[1].trim()));
        response.put("display_pixel_ratio", Integer.parseInt(info[2].trim()));
        response.put("benchmark", Integer.parseInt(info[3].trim()));
        return response;
    }

    /**
     * Overlays hardware info onto a device - Used in generic replys
     *
     * @param device
     * @param hardwareInfo
     **/
    void hardwareInfoOverlay(Device device, Map<String, Integer> infoArray) {
        if (infoArray.containsKey("display_x")) {
            device.getHdSpecs().setDisplayX(infoArray.get("display_x").toString());
        }
        if (infoArray.containsKey("display_y")) {
            device.getHdSpecs().setDisplayX(infoArray.get("display_y").toString());
        }
        if (infoArray.containsKey("display_pixel_ratio")) {
            device.getHdSpecs().setDisplayX(infoArray.get("display_pixel_ratio").toString());
        }
    }

    /**
     * Device matching
     *
     * Plan of attack :
     *  1) Look for opera headers first - as they're definitive
     *  2) Try profile match - only devices which have unique profiles will match.
     *  3) Try user-agent match
     *  4) Try other x-headers
     *  5) Try all remaining headers
     *
     * @return The matched device or null if not found
     **/
    Device matchDevice(Map<String, String> headers) {
        String agent = "";
        // Remember the agent for generic matching later.
        String _id = null;
        // Opera mini sometimes puts the vendor # model in the header - nice! ... sometimes it puts ? # ? in as well
        if (headers.containsKey("x-operamini-phone") && !headers.get("x-operamini-phone").trim().equals("? # ?")) {
            _id = this.getMatch("x-operamini-phone", headers.get("x-operamini-phone"), HDDevice.DETECTIONV4_STANDARD, "x-operamini-phone", "device");
            if (_id != null) {
                return this.findById(_id);
            }
            agent = headers.get("x-operamini-phone");
            headers.remove("x-operamini-phone");
        }

        // Profile header matching
        if (headers.containsKey("profile")) {
            _id = this.getMatch("profile", headers.get("profile"), HDDevice.DETECTIONV4_STANDARD, "profile", "device");
            if (_id != null) {
                return this.findById(_id);
            }
            headers.remove("profile");
        }

        // Profile header matching
        if (headers.containsKey("x-wap-profile")) {
            _id = this.getMatch("profile", headers.get("x-wap-profile"), HDDevice.DETECTIONV4_STANDARD, "x-wap-profile", "device");
            if (_id != null) {
                return this.findById(_id);
            }
            headers.remove("x-wap-profile");
        }

        // Match nominated headers ahead of x- headers
        List<String> order = new ArrayList<>(this.detectionConfigUA.get("device-ua-order"));
        headers.keySet().stream().filter((key) -> (!order.contains(key) && key.startsWith("x-"))).forEachOrdered((key) -> {
            order.add(key);
        });

        for(String item: order) {
            if (headers.containsKey(item)) {
                _id = this.getMatch("user-agent", headers.get(item), HDDevice.DETECTIONV4_STANDARD, item, "device");
                if (_id != null) {
                    return this.findById(_id);
                }
            }
        }

        // Generic matching - Match of last resort
        //this.log("Trying Generic Match");

        if (headers.containsKey("x-operamini-phone-ua")) {
            _id = this.getMatch("user-agent", headers.get("x-operamini-phone-ua"), HDDevice.DETECTIONV4_GENERIC, "agent", "device");
        }
        if (_id == null && headers.containsKey("agent")) {
            _id = this.getMatch("user-agent", headers.get("agent"), HDDevice.DETECTIONV4_GENERIC, "agent", "device");
        }
        if (_id == null && headers.containsKey("user-agent")) {
            _id = this.getMatch("user-agent", headers.get("user-agent"), HDDevice.DETECTIONV4_GENERIC, "agent", "device");
        }

        if (_id != null) {
            return this.findById(_id);
        }
        return null;
    }

    /**
     * Find a device by its id
     *
     * @param _id
     * @return device on success, false otherwise
     **/
    Device findById(String _id) {
        Object obj;
        try {
            obj = this.store.read("Device_" + _id, new TypeReference<Map<String, Device>>() {});
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        @SuppressWarnings("unchecked")
        Map<String, Device> map = (Map<String, Device>)obj;
        return map.get("Device");
    }

    /**
     * Internal helper for building a list of all devices.
     *
     * @return List of all devices.
     */
    List<Device> fetchDevices() {
        try {
            List<Device> result = this.store.fetchDevices();
            if (result != null) {
                return result;
            }
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
        this.setError(299, "Error : fetchDevices cannot read files from store.");
        return null;
    }

    /**
     * BuildInfo Matching
     *
     * Takes a set of buildInfo key/value pairs & works out what the device is
     *
     * @param buildInfo - Buildinfo key/value array
     * @return device array on success, false otherwise
     */
    boolean v4MatchBuildInfo(Map<String, String> buildInfo) {
        // Nothing to check
        if (buildInfo.isEmpty()) {
            return false;
        }

        this.buildInfo = buildInfo;

        // Device Detection
        this.device = (Device)this.v4MatchBIHelper(buildInfo, "device");
        if (this.device == null) {
            return false;
        }

        // Platform Detection
        this.platform = (Extra)this.v4MatchBIHelper(buildInfo, "platform");
        if (this.platform != null) {
            this.specsOverlay("platform", this.device, this.platform);
        }

        this.reply.setHdSpecs(this.device.getHdSpecs());
        return this.setError(0, "OK");
    }

    /**
     * buildInfo Match helper - Does the build info match heavy lifting
     *
     * @param buildInfo A buildInfo key/value array
     * @param category - 'device' or 'platform' (cant match browser or app with buildinfo)
     * @return device or extra on success, false otherwise
     **/
    Object v4MatchBIHelper(Map<String, String> buildInfo, String category) {
        // ***** Device Detection *****
        Map<String, List<List<String>>> confBIKeys = this.detectionConfigBI.get(category + "-bi-order");
        if (confBIKeys.isEmpty() || buildInfo.isEmpty()) {
            return null;
        }

        for( String p: confBIKeys.keySet()) {
            List<List<String>> set = confBIKeys.get(p);
            for (List<String> tuple: set) {
                boolean checking = true;
                String value = "";
                for(String item: tuple) {
                    if (item.equals("hd-platform")) {
                        value += "|" + p;
                    } else if (!buildInfo.containsKey(item)) {
                        checking = false;
                        break;
                    } else {
                        value += "|" + buildInfo.get(item);
                    }
                }

                if (checking) {
                    value = HDDevice.BIN_START_PATTERN.matcher(value).replaceFirst("");
                    value = HDDevice.BIN_END_PATTERN.matcher(value).replaceFirst("");
                    String subtree = (category.equals("device")) ? DETECTIONV4_STANDARD : category;
                    value = (category.equals("device")) ? this.cleanStr(value) : this.extraCleanStr(value);
                    String _id = this.getMatch("buildinfo", value, subtree, "buildinfo", category);
                    if (_id != null) {
                        return category.equals("device") ? this.findById(_id) : this.extra.findById(_id);
                    }
                }
            }
        }

        // If we get this far then not found, so try generic.
        String plat = this.hasBiKeys(buildInfo);
        if (plat != null) {
            List<String> tr = ImmutableList.of("generic|" + plat, plat + "|generic");
            for(String value: tr) {
                String subtree = (category.equals("device")) ? DETECTIONV4_GENERIC : category;
                value = category.equals("device") ? this.cleanStr(value) : this.extraCleanStr(value);
                String _id = this.getMatch("buildinfo", value, subtree, "buildinfo", category);
                if (_id != null) {
                    return (category.equals("device")) ? this.findById(_id) : this.extra.findById(_id);
                }
            }
        }
        return null;
    }

    /**
     * Find the best device match for a given set of headers and optional device properties.
     *
     * In 'all' mode all conflicted devces will be returned as a list.
     * In 'default' mode if there is a conflict then the detected device is returned only (backwards compatible with v3).
     *
     * @param headers Set of sanitized http headers
     * @param hardwareInfo Information about the hardware
     * @return device specs. (device.hd_specs)
     **/
    boolean v4MatchHttpHeaders(Map<String, String> headers, String hardwareInfo) {
        Map<String, Integer> hwProps = null;

        // Nothing to check
        if (headers == null || headers.isEmpty()) {
            return false;
        }

        headers.remove("ip");
        headers.remove("host");

        // Sanitize headers & cleanup language
        headers.entrySet().forEach((e) -> {
            String key = e.getKey();
            String value = e.getValue();
            if (key.equals("accept-language") || key.equals("content-language")) {
                key = "language";
                String[] tokens = value.replace(" ", "").split("[,;]");
                if (tokens.length > 0 && !tokens[0].isEmpty()) {
                    value = tokens[0];
                } else {
                    value = null;
                }
            } else if (!key.equals("profile") && !key.equals("x-wap-profile")) {
                // Handle strings that have had + substituted for a space ie. badly (semi) url encoded
                int stringLen = value.length();
                int spaces = stringLen - value.replace(" ", "").length();
                int plusses = stringLen - value.replace("+", "").length();
                if (spaces == 0 && plusses > 5 && stringLen > 20) {
                    value = value.replace("+", " ");
                }
            }
            if (value != null) {
                this.deviceHeaders.put(key, this.cleanStr(value));
                this.extraHeaders.put(key, this.extra.extraCleanStr(value));
            }
        });

        this.device = this.matchDevice(this.deviceHeaders);
        if (this.device == null) {
            if ( this.reply.getStatus() == 0) {
                // If no downstream error set then return not found.
                return this.setError(301, "Not Found");
            }
            // Error is already set, so return false
            return false;
        }

        if (hardwareInfo != null && !hardwareInfo.isEmpty()) {
            hwProps = this.infoStringToArray(hardwareInfo);
        }

        // Stop on detect set - Tidy up and return
        if (this.device.getHdOps().getStopOnDetect() == 1) {
            // Check for hardwareInfo overlay
            if (this.device.getHdOps().getOverlayResultSpecs() == 1) {
                    this.hardwareInfoOverlay(this.device, hwProps);
            }
            this.reply.setHdSpecs(this.device.getHdSpecs());
            return this.setError(0, "OK");
        }

        // Get extra info
        this.platform = this.extra.matchExtra("platform", this.extraHeaders);
        this.browser = this.extra.matchExtra("browser", this.extraHeaders);
        this.app = this.extra.matchExtra("app", this.extraHeaders);
        this.language = this.extra.matchLanguage(this.extraHeaders);

        // Find out if there is any contention on the detected rule.
        List<String> deviceList = this.getHighAccuracyCandidates();
        if (deviceList != null && !deviceList.isEmpty()) {

            // Resolve contention with OS check
            this.extra.set(this.platform);
            List<String> pass1List = new ArrayList<>();
            deviceList.forEach((_id) -> {
                Device tryDevice = this.findById(_id);
                if (this.extra.verifyPlatform(tryDevice.getHdSpecs())) {
                    pass1List.add(_id);
                }
            });

            // Contention still not resolved .. check hardware
            if (pass1List.size() >= 2 && hwProps != null && !hwProps.isEmpty()) {

                // Score the list based on hardware

                Comparator<Map<String, Integer>> hd_sortByScore = (Map<String, Integer> d1, Map<String, Integer> d2) -> {
                    if (d2.get("score") - d1.get("score") != 0) {
                        return d2.get("score") - d1.get("score");
                    }
                    return d1.get("distance") - d2.get("distance");
                };

                SortedSet<Map<String, Integer>> result = new TreeSet<>(hd_sortByScore);
                for(String _id: pass1List) {
                    Map<String, Integer> rat = this.findRating(_id, hwProps);
                    if (rat != null && !rat.isEmpty()) {
                        rat.put("_id", Integer.parseInt(_id));
                        result.add(rat);
                    }
                }

                this.ratingResult = new ArrayList<>(result);

                // Take the first one
                if (this.ratingResult.get(0).get("score") != 0) {
                    Device d = this.findById("" + this.ratingResult.get(0).get("_id"));
                    if (d != null) {
                        this.device = d;
                    }
                }
            }
        }

        // Overlay specs
        this.specsOverlay("platform", this.device, this.platform);
        this.specsOverlay("browser", this.device, this.browser);
        this.specsOverlay("app", this.device, this.app);
        this.specsOverlay("language", this.device, this.language);

        // Overlay hardware info result if required
        if (this.device.getHdOps().getOverlayResultSpecs() != null && hardwareInfo != null) {
            this.hardwareInfoOverlay(this.device, hwProps);
        }

        this.reply.setHdSpecs(this.device.getHdSpecs());
        return this.setError(0, "OK");
    }

    /**
     * Determines if high accuracy checks are available on the device which was just detected
     *
     * @returns array, a list of candidate devices which have this detection rule or false otherwise.
     */
    List<String> getHighAccuracyCandidates() {
        if(this.detectedRuleKey.containsKey("device")) {
            String ruleKey = this.detectedRuleKey.get("device");
            Map<String, Object> branch = this.getBranch("hachecks");
            Object obj = branch.get(ruleKey);
            if (obj != null) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>)obj;
                return list;
            }
        }
        return null;
    }

    /**
     * Determines if hd4Helper would provide more accurate results.
     *
     * @param headers HTTP Headers
     * @return true if required, false otherwise
     **/
    boolean isHelperUseful(Map<String, String> headers) {
        if (headers.isEmpty()) {
            return false;
        }
        headers = new HashMap<>(headers);
        headers.remove("ip");
        headers.remove("host");

        boolean result = this.localDetect(headers);
        if (!result) {
            return false;
        }

        List<String> t = this.getHighAccuracyCandidates();
        return !(t == null || t.isEmpty());
    }

}
