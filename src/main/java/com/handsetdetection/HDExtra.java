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
import com.handsetdetection.data.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 *
 */
public class HDExtra extends HDBase {
    Extra data = null;

    HDExtra() {
        super();
        this.setConfig(new Properties());
    }
    HDExtra(Properties config) {
        super();
        this.setConfig(config);
    }

    /**
     * Set Config variables
     *
     * @param config A config array
     * @return true on success, false otherwise
     **/
    private boolean setConfig(Properties config) {
        this.store = HDStore.getInstance();
        this.store.setConfig(config);
        return true;
    }

    void set(Extra data) {
        this.data = data;
    }

    /**
     * Matches all HTTP header extras - platform, browser and app
     *
     * @param cls Is "platform","browser" or "app"
     * @return an Extra on success, null otherwise
     **/
    Extra matchExtra(String cls, Map<String, String> headers) {
        headers.remove("profile");

        List<String> order = new ArrayList<>(this.detectionConfigUA.get(cls + "-ua-order"));
        Set<String> keys = headers.keySet();

        keys.stream().filter((key) -> (!order.contains(key) && key.startsWith("x-"))).forEachOrdered((key) -> {
            order.add(key);
        }); // Append any x- headers to the list of headers to check

        for (String field: order) {
            if (headers.containsKey(field)) {
                String _id = this.getMatch("user-agent", headers.get(field), cls, field, cls);
                if (_id != null) {
                    Extra extra = this.findById(_id);
                    return extra;
                    }
            }
        }
        return null;
    }

    /**
     * Find a device by its id
     *
     * @param _id
     * @return device on success, false otherwise
     **/
    Extra findById(String _id) {
        Object obj;
        try {
            obj = this.store.read("Extra_" + _id, new TypeReference<Map<String, Extra>>() {});
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        @SuppressWarnings("unchecked")
        Map<String, Extra> map = (Map<String, Extra>)obj;
        return map.get("Extra");
    }

    /**
     * Can learn language from language header or agent
     *
     * @param headers A key => value array of sanitized http headers
     * @return Extra on success, false otherwise
     **/
    Extra matchLanguage(Map<String, String> headers) {

        Extra extra = new Extra();

        // Mock up a fake Extra for merge into detection reply
        extra.setId("0");
        extra.setHdSpecs(new HdSpecs());
        extra.getHdSpecs().setGeneralLanguage("");
        extra.getHdSpecs().setGeneralLanguageFull("");

        // Try directly from http header first
        if (headers.containsKey("language")) {
            String candidate = headers.get("language");
            if (this.detectionLanguages.containsKey(candidate)) {
                extra.getHdSpecs().setGeneralLanguage(candidate);
                extra.getHdSpecs().setGeneralLanguageFull(this.detectionLanguages.get(candidate));
                return extra;
            }
        }

        List<String> checkOrder = new ArrayList<>(this.detectionConfigUA.get("language-ua-order"));
        checkOrder.addAll(headers.keySet());
        Map<String, String> languageList = this.detectionLanguages;
        for(String header: checkOrder) {
            String agent = headers.get(header);

            if (agent != null && !agent.isEmpty()) {
                for(String code: languageList.keySet()) {
                    if (agent.contains(code) && agent.matches(".*[; \\(]" + code + "[; \\)].*")) {
                        extra.getHdSpecs().setGeneralLanguage(code);
                        extra.getHdSpecs().setGeneralLanguageFull(languageList.get(code));
                        return extra;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Returns false if this device definitively cannot run this platform and platform version.
     * Returns true if its possible of if there is any doubt.
     *
     * Note : The detected platform must match the device platform. This is the stock OS as shipped
     * on the device. If someone is running a variant (eg CyanogenMod) then all bets are off.
     *
     * @param specs The specs we want to check.
     * @return false if these specs can not run the detected OS, true otherwise.
     **/
    boolean verifyPlatform(HdSpecs specs) {
        Extra platform = this.data;

        String platformName = null;
        String platformVersion = null;
        if (platform != null) {
            platformName = platform.getHdSpecs().getGeneralPlatform().toLowerCase().trim();
            platformVersion = platform.getHdSpecs().getGeneralPlatformVersion().toLowerCase().trim();
        }
        String devicePlatformName = specs.getGeneralPlatform();
        String devicePlatformVersionMin = specs.getGeneralPlatformVersion();
        String devicePlatformVersionMax = specs.getGeneralPlatformVersionMax();
        if(devicePlatformName != null) {
            devicePlatformName = devicePlatformName.toLowerCase().trim();
        }
        if(devicePlatformVersionMin != null) {
            devicePlatformVersionMin = devicePlatformVersionMin.toLowerCase().trim();
        }
        if(devicePlatformVersionMax != null) {
            devicePlatformVersionMax = devicePlatformVersionMax.toLowerCase().trim();
        }

        // Its possible that we didnt pickup the platform correctly or the device has no platform info
        // Return true in this case because we cant give a concrete false (it might run this version).
        if (platform == null || platformName == null || devicePlatformName == null) {
            return true;
        }

        // Make sure device is running stock OS / Platform
        // Return true in this case because its possible the device can run a different OS (mods / hacks etc..)
        if (!platformName.equals(devicePlatformName)) {
            return true;
        }

        // Detected version is lower than the min version - so definetly false.
        if (platformVersion != null && devicePlatformVersionMin != null && this.comparePlatformVersions(platformVersion, devicePlatformVersionMin) <= -1) {
            return false;
        }
        // Detected version is greater than the max version - so definetly false.
        // Maybe Ok ..

        return !(platformVersion != null && devicePlatformVersionMax != null && this.comparePlatformVersions(platformVersion, devicePlatformVersionMax) >= 1);
    }

    /**
     * Breaks a version number apart into its Major, minor and point release numbers for comparison.
     *
     * Big Assumption : That version numbers separate their release bits by "." !!!
     * might need to do some analysis on the string to rip it up right.
     *
     * @param versionNumber
     * @return of ("major" => x, "minor" => y and "point" => z) on success, null otherwise
     **/
    Map<String, String> breakVersionApart(String versionNumber) {
        String[] tokens = (versionNumber + ".0.0.0").split("\\.", 4);
        Map<String, String> r = new HashMap<>();
        r.put("major", ! tokens[0].isEmpty() ? tokens[0] : "0");
        r.put("minor", ! tokens[1].isEmpty() ? tokens[1] : "0");
        r.put("point", ! tokens[2].isEmpty() ? tokens[2] : "0");
        return r;
    }

    /**
     * Helper for comparing two strings (numerically if possible)
     *
     * @param a Generally a number, but might be a string
     * @param b Generally a number, but might be a string
     * @return
     **/
    int compareSmartly(String a, String b) {
        try {
            return Integer.parseInt(a) - Integer.parseInt(b);
        } catch (NumberFormatException e) {
            return a.compareTo(b);
        }
    }

    /**
     * Compares two platform version numbers
     *
     * @param va Version A
     * @param vb Version B
     * @return < 0 if a < b, 0 if a == b and > 0 if a > b : Also returns 0 if data is absent from either.
     */
    int comparePlatformVersions(String va, String vb) {
        if (va == null || va.isEmpty() || vb == null || vb.isEmpty()) {
                return 0;
        }
        Map<String, String> versionA = this.breakVersionApart(va);
        Map<String, String> versionB = this.breakVersionApart(vb);

        int major = this.compareSmartly(versionA.get("major"), versionB.get("major"));
        int minor = this.compareSmartly(versionA.get("minor"), versionB.get("minor"));
        int point = this.compareSmartly(versionA.get("point"), versionB.get("point"));

        if (major != 0) {
            return major;
        }
        if (minor != 0) {
            return minor;
        }
        if (point != 0) {
            return point;
        }
        return 0;
    }
}
