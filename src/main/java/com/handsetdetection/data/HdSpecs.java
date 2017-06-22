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
package com.handsetdetection.data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "general_vendor",
    "general_model",
    "general_platform",
    "general_platform_version",
    "general_browser",
    "general_browser_version",
    "general_image",
    "general_aliases",
    "general_eusar",
    "general_battery",
    "general_type",
    "general_cpu",
    "design_formfactor",
    "design_dimensions",
    "design_weight",
    "design_antenna",
    "design_keyboard",
    "design_softkeys",
    "design_sidekeys",
    "display_type",
    "display_color",
    "display_colors",
    "display_size",
    "display_x",
    "display_y",
    "display_other",
    "memory_internal",
    "memory_slot",
    "network",
    "media_camera",
    "media_secondcamera",
    "media_videocapture",
    "media_videoplayback",
    "media_audio",
    "media_other",
    "features",
    "connectors",
    "general_platform_version_max",
    "general_app",
    "general_app_version",
    "general_language",
    "display_ppi",
    "display_pixel_ratio",
    "benchmark_min",
    "benchmark_max",
    "general_app_category",
    "general_virtual",
    "display_css_screen_sizes",
    "general_browser_engine",
    "general_language_full"
})
public class HdSpecs {

    @JsonProperty("general_vendor")
    private String generalVendor;
    @JsonProperty("general_model")
    private String generalModel;
    @JsonProperty("general_platform")
    private String generalPlatform;
    @JsonProperty("general_platform_version")
    private String generalPlatformVersion;
    @JsonProperty("general_browser")
    private String generalBrowser;
    @JsonProperty("general_browser_version")
    private String generalBrowserVersion;
    @JsonProperty("general_image")
    private String generalImage;
    @JsonProperty("general_aliases")
    private List<String> generalAliases = new ArrayList<String>();
    @JsonProperty("general_eusar")
    private String generalEusar;
    @JsonProperty("general_battery")
    private List<String> generalBattery = new ArrayList<String>();
    @JsonProperty("general_type")
    private String generalType;
    @JsonProperty("general_cpu")
    private List<String> generalCpu = new ArrayList<String>();
    @JsonProperty("design_formfactor")
    private String designFormfactor;
    @JsonProperty("design_dimensions")
    private String designDimensions;
    @JsonProperty("design_weight")
    private String designWeight;
    @JsonProperty("design_antenna")
    private String designAntenna;
    @JsonProperty("design_keyboard")
    private String designKeyboard;
    @JsonProperty("design_softkeys")
    private String designSoftkeys;
    @JsonProperty("design_sidekeys")
    private List<String> designSidekeys = new ArrayList<String>();
    @JsonProperty("display_type")
    private String displayType;
    @JsonProperty("display_color")
    private String displayColor;
    @JsonProperty("display_colors")
    private String displayColors;
    @JsonProperty("display_size")
    private String displaySize;
    @JsonProperty("display_x")
    private String displayX;
    @JsonProperty("display_y")
    private String displayY;
    @JsonProperty("display_other")
    private List<String> displayOther = new ArrayList<String>();
    @JsonProperty("memory_internal")
    private List<String> memoryInternal = new ArrayList<String>();
    @JsonProperty("memory_slot")
    private List<String> memorySlot = new ArrayList<String>();
    @JsonProperty("network")
    private List<String> network = new ArrayList<String>();
    @JsonProperty("media_camera")
    private List<String> mediaCamera = new ArrayList<String>();
    @JsonProperty("media_secondcamera")
    private List<String> mediaSecondcamera = new ArrayList<String>();
    @JsonProperty("media_videocapture")
    private List<String> mediaVideocapture = new ArrayList<String>();
    @JsonProperty("media_videoplayback")
    private List<String> mediaVideoplayback = new ArrayList<String>();
    @JsonProperty("media_audio")
    private List<String> mediaAudio = new ArrayList<String>();
    @JsonProperty("media_other")
    private List<String> mediaOther = new ArrayList<String>();
    @JsonProperty("features")
    private List<String> features = new ArrayList<String>();
    @JsonProperty("connectors")
    private List<String> connectors = new ArrayList<String>();
    @JsonProperty("general_platform_version_max")
    private String generalPlatformVersionMax;
    @JsonProperty("general_app")
    private String generalApp;
    @JsonProperty("general_app_version")
    private String generalAppVersion;
    @JsonProperty("general_language")
    private String generalLanguage;
    @JsonProperty("display_ppi")
    private Integer displayPpi;
    @JsonProperty("display_pixel_ratio")
    private String displayPixelRatio;
    @JsonProperty("benchmark_min")
    private Integer benchmarkMin;
    @JsonProperty("benchmark_max")
    private Integer benchmarkMax;
    @JsonProperty("general_app_category")
    private String generalAppCategory;
    @JsonProperty("general_virtual")
    private Integer generalVirtual;
    @JsonProperty("display_css_screen_sizes")
    private List<String> displayCssScreenSizes = new ArrayList<String>();
    @JsonProperty("general_browser_engine")
    private String generalBrowserEngine;
    @JsonProperty("general_language_full")
    private String generalLanguageFull;

    @JsonProperty("general_vendor")
    public String getGeneralVendor() {
        return generalVendor;
    }

    @JsonProperty("general_vendor")
    public void setGeneralVendor(String generalVendor) {
        this.generalVendor = generalVendor;
    }

    @JsonProperty("general_model")
    public String getGeneralModel() {
        return generalModel;
    }

    @JsonProperty("general_model")
    public void setGeneralModel(String generalModel) {
        this.generalModel = generalModel;
    }

    @JsonProperty("general_platform")
    public String getGeneralPlatform() {
        return generalPlatform;
    }

    @JsonProperty("general_platform")
    public void setGeneralPlatform(String generalPlatform) {
        this.generalPlatform = generalPlatform;
    }

    @JsonProperty("general_platform_version")
    public String getGeneralPlatformVersion() {
        return generalPlatformVersion;
    }

    @JsonProperty("general_platform_version")
    public void setGeneralPlatformVersion(String generalPlatformVersion) {
        this.generalPlatformVersion = generalPlatformVersion;
    }

    @JsonProperty("general_browser")
    public String getGeneralBrowser() {
        return generalBrowser;
    }

    @JsonProperty("general_browser")
    public void setGeneralBrowser(String generalBrowser) {
        this.generalBrowser = generalBrowser;
    }

    @JsonProperty("general_browser_version")
    public String getGeneralBrowserVersion() {
        return generalBrowserVersion;
    }

    @JsonProperty("general_browser_version")
    public void setGeneralBrowserVersion(String generalBrowserVersion) {
        this.generalBrowserVersion = generalBrowserVersion;
    }

    @JsonProperty("general_image")
    public String getGeneralImage() {
        return generalImage;
    }

    @JsonProperty("general_image")
    public void setGeneralImage(String generalImage) {
        this.generalImage = generalImage;
    }

    @JsonProperty("general_aliases")
    public List<String> getGeneralAliases() {
        return generalAliases;
    }

    @JsonProperty("general_aliases")
    public void setGeneralAliases(List<String> generalAliases) {
        generalAliases.remove("");
        this.generalAliases = generalAliases;
    }

    @JsonProperty("general_eusar")
    public String getGeneralEusar() {
        return generalEusar;
    }

    @JsonProperty("general_eusar")
    public void setGeneralEusar(String generalEusar) {
        this.generalEusar = generalEusar;
    }

    @JsonProperty("general_battery")
    public List<String> getGeneralBattery() {
        return generalBattery;
    }

    @JsonProperty("general_battery")
    public void setGeneralBattery(List<String> generalBattery) {
        generalBattery.remove("");
        this.generalBattery = generalBattery;

    }

    @JsonProperty("general_type")
    public String getGeneralType() {
        return generalType;
    }

    @JsonProperty("general_type")
    public void setGeneralType(String generalType) {
        this.generalType = generalType;
    }

    @JsonProperty("general_cpu")
    public List<String> getGeneralCpu() {
        return generalCpu;
    }

    @JsonProperty("general_cpu")
    public void setGeneralCpu(List<String> generalCpu) {
        generalCpu.remove("");
        this.generalCpu = generalCpu;
    }

    @JsonProperty("design_formfactor")
    public String getDesignFormfactor() {
        return designFormfactor;
    }

    @JsonProperty("design_formfactor")
    public void setDesignFormfactor(String designFormfactor) {
        this.designFormfactor = designFormfactor;
    }

    @JsonProperty("design_dimensions")
    public String getDesignDimensions() {
        return designDimensions;
    }

    @JsonProperty("design_dimensions")
    public void setDesignDimensions(String designDimensions) {
        this.designDimensions = designDimensions;
    }

    @JsonProperty("design_weight")
    public String getDesignWeight() {
        return designWeight;
    }

    @JsonProperty("design_weight")
    public void setDesignWeight(String designWeight) {
        this.designWeight = designWeight;
    }

    @JsonProperty("design_antenna")
    public String getDesignAntenna() {
        return designAntenna;
    }

    @JsonProperty("design_antenna")
    public void setDesignAntenna(String designAntenna) {
        this.designAntenna = designAntenna;
    }

    @JsonProperty("design_keyboard")
    public String getDesignKeyboard() {
        return designKeyboard;
    }

    @JsonProperty("design_keyboard")
    public void setDesignKeyboard(String designKeyboard) {
        this.designKeyboard = designKeyboard;
    }

    @JsonProperty("design_softkeys")
    public String getDesignSoftkeys() {
        return designSoftkeys;
    }

    @JsonProperty("design_softkeys")
    public void setDesignSoftkeys(String designSoftkeys) {
        this.designSoftkeys = designSoftkeys;
    }

    @JsonProperty("design_sidekeys")
    public List<String> getDesignSidekeys() {
        return designSidekeys;
    }

    @JsonProperty("design_sidekeys")
    public void setDesignSidekeys(List<String> designSidekeys) {
        designSidekeys.remove("");
        this.designSidekeys = designSidekeys;
    }

    @JsonProperty("display_type")
    public String getDisplayType() {
        return displayType;
    }

    @JsonProperty("display_type")
    public void setDisplayType(String displayType) {
        this.displayType = displayType;
    }

    @JsonProperty("display_color")
    public String getDisplayColor() {
        return displayColor;
    }

    @JsonProperty("display_color")
    public void setDisplayColor(String displayColor) {
        this.displayColor = displayColor;
    }

    @JsonProperty("display_colors")
    public String getDisplayColors() {
        return displayColors;
    }

    @JsonProperty("display_colors")
    public void setDisplayColors(String displayColors) {
        this.displayColors = displayColors;
    }

    @JsonProperty("display_size")
    public String getDisplaySize() {
        return displaySize;
    }

    @JsonProperty("display_size")
    public void setDisplaySize(String displaySize) {
        this.displaySize = displaySize;
    }

    @JsonProperty("display_x")
    public String getDisplayX() {
        return displayX;
    }

    @JsonProperty("display_x")
    public void setDisplayX(String displayX) {
        this.displayX = displayX;
    }

    @JsonProperty("display_y")
    public String getDisplayY() {
        return displayY;
    }

    @JsonProperty("display_y")
    public void setDisplayY(String displayY) {
        this.displayY = displayY;
    }

    @JsonProperty("display_other")
    public List<String> getDisplayOther() {
        return displayOther;
    }

    @JsonProperty("display_other")
    public void setDisplayOther(List<String> displayOther) {
        displayOther.remove("");
        this.displayOther = displayOther;
    }

    @JsonProperty("memory_internal")
    public List<String> getMemoryInternal() {
        return memoryInternal;
    }

    @JsonProperty("memory_internal")
    public void setMemoryInternal(List<String> memoryInternal) {
        memoryInternal.remove("");
        this.memoryInternal = memoryInternal;
    }

    @JsonProperty("memory_slot")
    public List<String> getMemorySlot() {
        return memorySlot;
    }

    @JsonProperty("memory_slot")
    public void setMemorySlot(List<String> memorySlot) {
        memorySlot.remove("");
        this.memorySlot = memorySlot;
    }

    @JsonProperty("network")
    public List<String> getNetwork() {
        return network;
    }

    @JsonProperty("network")
    public void setNetwork(List<String> network) {
        network.remove("");
        this.network = network;
    }

    @JsonProperty("media_camera")
    public List<String> getMediaCamera() {
        return mediaCamera;
    }

    @JsonProperty("media_camera")
    public void setMediaCamera(List<String> mediaCamera) {
        mediaCamera.remove("");
        this.mediaCamera = mediaCamera;
    }

    @JsonProperty("media_secondcamera")
    public List<String> getMediaSecondcamera() {
        return mediaSecondcamera;
    }

    @JsonProperty("media_secondcamera")
    public void setMediaSecondcamera(List<String> mediaSecondcamera) {
        mediaSecondcamera.remove("");
        this.mediaSecondcamera = mediaSecondcamera;
    }

    @JsonProperty("media_videocapture")
    public List<String> getMediaVideocapture() {
        return mediaVideocapture;
    }

    @JsonProperty("media_videocapture")
    public void setMediaVideocapture(List<String> mediaVideocapture) {
        mediaVideocapture.remove("");
        this.mediaVideocapture = mediaVideocapture;
    }

    @JsonProperty("media_videoplayback")
    public List<String> getMediaVideoplayback() {
        return mediaVideoplayback;
    }

    @JsonProperty("media_videoplayback")
    public void setMediaVideoplayback(List<String> mediaVideoplayback) {
        mediaVideoplayback.remove("");
        this.mediaVideoplayback = mediaVideoplayback;
    }

    @JsonProperty("media_audio")
    public List<String> getMediaAudio() {
        return mediaAudio;
    }

    @JsonProperty("media_audio")
    public void setMediaAudio(List<String> mediaAudio) {
        mediaAudio.remove("");
        this.mediaAudio = mediaAudio;
    }

    @JsonProperty("media_other")
    public List<String> getMediaOther() {
        return mediaOther;
    }

    @JsonProperty("media_other")
    public void setMediaOther(List<String> mediaOther) {
        mediaOther.remove("");
        this.mediaOther = mediaOther;
    }

    @JsonProperty("features")
    public List<String> getFeatures() {
        return features;
    }

    @JsonProperty("features")
    public void setFeatures(List<String> features) {
        features.remove("");
        this.features = features;
    }

    @JsonProperty("connectors")
    public List<String> getConnectors() {
        return connectors;
    }

    @JsonProperty("connectors")
    public void setConnectors(List<String> connectors) {
        connectors.remove("");
        this.connectors = connectors;
    }

    @JsonProperty("general_platform_version_max")
    public String getGeneralPlatformVersionMax() {
        return generalPlatformVersionMax;
    }

    @JsonProperty("general_platform_version_max")
    public void setGeneralPlatformVersionMax(String generalPlatformVersionMax) {
        this.generalPlatformVersionMax = generalPlatformVersionMax;
    }

    @JsonProperty("general_app")
    public String getGeneralApp() {
        return generalApp;
    }

    @JsonProperty("general_app")
    public void setGeneralApp(String generalApp) {
        this.generalApp = generalApp;
    }

    @JsonProperty("general_app_version")
    public String getGeneralAppVersion() {
        return generalAppVersion;
    }

    @JsonProperty("general_app_version")
    public void setGeneralAppVersion(String generalAppVersion) {
        this.generalAppVersion = generalAppVersion;
    }

    @JsonProperty("general_language")
    public String getGeneralLanguage() {
        return generalLanguage;
    }

    @JsonProperty("general_language")
    public void setGeneralLanguage(String generalLanguage) {
        this.generalLanguage = generalLanguage;
    }

    @JsonProperty("display_ppi")
    public Integer getDisplayPpi() {
        return displayPpi;
    }

    @JsonProperty("display_ppi")
    public void setDisplayPpi(Integer displayPpi) {
        this.displayPpi = displayPpi;
    }

    @JsonProperty("display_pixel_ratio")
    public String getDisplayPixelRatio() {
        return displayPixelRatio;
    }

    @JsonProperty("display_pixel_ratio")
    public void setDisplayPixelRatio(String displayPixelRatio) {
        this.displayPixelRatio = displayPixelRatio;
    }

    @JsonProperty("benchmark_min")
    public Integer getBenchmarkMin() {
        return benchmarkMin;
    }

    @JsonProperty("benchmark_min")
    public void setBenchmarkMin(Integer benchmarkMin) {
        this.benchmarkMin = benchmarkMin;
    }

    @JsonProperty("benchmark_max")
    public Integer getBenchmarkMax() {
        return benchmarkMax;
    }

    @JsonProperty("benchmark_max")
    public void setBenchmarkMax(Integer benchmarkMax) {
        this.benchmarkMax = benchmarkMax;
    }

    @JsonProperty("general_app_category")
    public String getGeneralAppCategory() {
        return generalAppCategory;
    }

    @JsonProperty("general_app_category")
    public void setGeneralAppCategory(String generalAppCategory) {
        this.generalAppCategory = generalAppCategory;
    }

    @JsonProperty("general_virtual")
    public Integer getGeneralVirtual() {
        return generalVirtual;
    }

    @JsonProperty("general_virtual")
    public void setGeneralVirtual(Integer generalVirtual) {
        this.generalVirtual = generalVirtual;
    }

    @JsonProperty("display_css_screen_sizes")
    public List<String> getDisplayCssScreenSizes() {
        return displayCssScreenSizes;
    }

    @JsonProperty("display_css_screen_sizes")
    public void setDisplayCssScreenSizes(List<String> displayCssScreenSizes) {
        displayCssScreenSizes.remove("");
        this.displayCssScreenSizes = displayCssScreenSizes;
    }

    @JsonProperty("general_browser_engine")
    public String getGeneralBrowserEngine() {
        return generalBrowserEngine;
    }

    @JsonProperty("general_browser_engine")
    public void setGeneralBrowserEngine(String generalBrowserEngine) {
        this.generalBrowserEngine = generalBrowserEngine;
    }

    @JsonProperty("general_language_full")
    public String getGeneralLanguageFull() {
        return generalLanguageFull;
    }

    @JsonProperty("general_language_full")
    public void setGeneralLanguageFull(String generalLanguageFull) {
        this.generalLanguageFull = generalLanguageFull;
    }

}
