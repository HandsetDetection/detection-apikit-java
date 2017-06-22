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
import com.handsetdetection.cache.*;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class HDCache {
    String prefix;
    int duration;
    protected Cache cache = null;

    /**
     * HDCache constructor
     *
     **/
    public HDCache() {
        this(new Properties());
    }

    /**
     * HDCache constructor
     *
     * @param config properties
     **/
    public HDCache(Properties config) {
        this.setConfig(config);
    }

    /**
     * Set config file
     *
     * @param config An assoc array of config data
     * @return true on success, false otherwise
     **/
    private boolean setConfig(Properties config) {
        this.prefix = config.getProperty("cache.prefix", "hd40");
        this.duration = Integer.parseInt(config.getProperty("cache.ttl", "7200"));
        config.stringPropertyNames().forEach((key) -> {
            if (key.startsWith("cache.memcached")) {
                this.cache = new MemcachedCache(config);
            } else if (key.startsWith("cache.redis")) {
                this.cache = new RedisCache(config);
            } else if (key.startsWith("cache.file")) {
                this.cache = new FileCache(config);
            } else if (key.startsWith("cache.memory")) {
                this.cache = new MemoryCache(config);
            } else if (key.startsWith("cache.none")) {
                this.cache = new NoCache();
            }
        });
        if (this.cache == null) {
            this.cache = new NoCache();
        }
        return true;
    }

    /**
     * Fetch a cache key
     *
     * @param key The cache key
     * @param ref Reference to the data type
     * @return value on success, null otherwise
     * @throws java.io.IOException On I/O failure
     **/
    public Object read(String key, TypeReference ref) throws IOException {
        return this.cache.get(this.prefix + key, ref);
    }

    /**
     * Store a data at key
     *
     * @param key Cache key
     * @param data Data
     * @return true on success, false otherwise
     * @throws com.fasterxml.jackson.core.JsonProcessingException On data serialization failure
     **/
    public boolean write(String key, Object data) throws IOException {
        return this.cache.set(this.prefix + key, data, this.duration);
    }

    /**
     * Remove a cache key (and its data)
     *
     * @param key the cache key
     * @return true on success, false otherwise
     **/
    public boolean delete(String key) {
        return this.cache.del(this.prefix + key);
    }

    /**
     * Flush the whole cache
     *
     * @return true on success, false otherwise
     **/
    public boolean purge() {
        return this.cache.flush();
    }

    /**
     * Return the name of the cache provider
     *
     * @return name
     **/
    public String getName() {
        return this.cache.getName();
    }
}
