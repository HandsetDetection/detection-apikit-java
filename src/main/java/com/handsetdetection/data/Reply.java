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
import java.util.List;

/**
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "hd_specs",
    "class",
    "model",
    "vendor",
    "device",
    "devices",
    "message",
    "status"
})

public class Reply {

    @JsonProperty("hd_specs")
    private HdSpecs hdSpecs;
    @JsonProperty("class")
    private String _class;
    @JsonProperty("message")
    private String message;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("model")
    private List<String> model;
    @JsonProperty("vendor")
    private List<String> vendor;
    @JsonProperty("device")
    private HdSpecs device;
    @JsonProperty("devices")
    private List<DeviceSummary> devices;

    @JsonProperty("hd_specs")
    public HdSpecs getHdSpecs() {
        return hdSpecs;
    }

    @JsonProperty("hd_specs")
    public void setHdSpecs(HdSpecs hdSpecs) {
        this.hdSpecs = hdSpecs;
    }

    @JsonProperty("class")
    public String getClass_() {
        return _class;
    }

    @JsonProperty("class")
    public void setClass_(String _class) {
        this._class = _class;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("status")
    public Integer getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(Integer status) {
        this.status = status;
    }

    @JsonProperty("model")
    public List<String> getModel() {
        return model;
    }

    @JsonProperty("model")
    public void setModel(List<String> model) {
        model.remove("");
        this.model = model;
    }

    @JsonProperty("vendor")
    public List<String> getVendor() {
        return vendor;
    }

    @JsonProperty("vendor")
    public void setVendor(List<String> vendor) {
        vendor.remove("");
        this.vendor = vendor;
    }

    @JsonProperty("device")
    public HdSpecs getDevice() {
        return device;
    }

    @JsonProperty("device")
    public void setDevice(HdSpecs device) {
        this.device = device;
    }

    @JsonProperty("devices")
    public List<DeviceSummary> getDevices() {
        return devices;
    }

    @JsonProperty("devices")
    public void setDevices(List<DeviceSummary> devices) {
        this.devices = devices;
    }
}
