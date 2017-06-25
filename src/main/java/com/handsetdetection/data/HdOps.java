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

/**
 *
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "is_generic",
    "stop_on_detect",
    "overlay_result_specs"
})

public class HdOps {

    @JsonProperty("is_generic")
    private Integer isGeneric;
    @JsonProperty("stop_on_detect")
    private Integer stopOnDetect;
    @JsonProperty("overlay_result_specs")
    private Integer overlayResultSpecs;

    @JsonProperty("is_generic")
    public Integer getIsGeneric() {
        return isGeneric;
    }

    @JsonProperty("is_generic")
    public void setIsGeneric(Integer isGeneric) {
        this.isGeneric = isGeneric;
    }

    @JsonProperty("stop_on_detect")
    public Integer getStopOnDetect() {
        return stopOnDetect;
    }

    @JsonProperty("stop_on_detect")
    public void setStopOnDetect(Integer stopOnDetect) {
        this.stopOnDetect = stopOnDetect;
    }

    @JsonProperty("overlay_result_specs")
    public Integer getOverlayResultSpecs() {
        return overlayResultSpecs;
    }

    @JsonProperty("overlay_result_specs")
    public void setOverlayResultSpecs(Integer overlayResultSpecs) {
        this.overlayResultSpecs = overlayResultSpecs;
    }

}
