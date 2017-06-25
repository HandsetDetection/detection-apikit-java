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
package com.handsetdetection.cache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheBuilder;
import java.util.Properties;
import java.util.concurrent.TimeUnit;


/**
 *
 */
public class MemoryCache implements Cache {
    private final String name = "memory";
    private final com.google.common.cache.Cache<String, Object> cache;

    public MemoryCache(Properties config) {
        int ttl = Integer.parseInt(config.getProperty("cache.ttl", "7200"));
        int maximumSize = Integer.parseInt(
                config.getProperty("cache." + this.name + ".maximumSize", "10000000"));
        this.cache = CacheBuilder.<String, Object>newBuilder().
                maximumSize(maximumSize).
                expireAfterWrite(ttl, TimeUnit.SECONDS).
                build();
    }

    @Override
    public Object get(String key, TypeReference ref) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public boolean set(String key, Object data, int ttl) {
        this.cache.put(key, data);
        return true;
    }

    @Override
    public boolean del(String key) {
        this.cache.invalidate(key);
        return true;
    }

    @Override
    public boolean flush() {
        this.cache.invalidateAll();
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
