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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.handsetdetection.HD4;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import java.io.IOException;
import java.util.Properties;

/**
 *
 */
public class RedisCache implements Cache {

    private final RedisConnection<String, String> redis;
    private final String name = "redis";

    public RedisCache(Properties config) {
        String uri = config.getProperty("cache." + this.name + ".uri", "redis://localhost/");
        RedisClient client = RedisClient.create(uri);
        this.redis = client.connect();
    }

    @Override
    public Object get(String key, TypeReference ref) throws IOException {
        String data = this.redis.get(key);
        if (data == null) {
            return null;
        }
        return HD4.mapper().readValue(data, ref);
    }

    @Override
    public boolean set(String key, Object data, int ttl) throws JsonProcessingException {
        String code;
        code = this.redis.set(key, HD4.mapper().writeValueAsString(data));
        if (!code.equals("OK")) {
            return false;
        }
        if (ttl > 0) {
            return this.redis.expire(key, ttl * 1_000);
        }
        return true;
    }

    @Override
    public boolean del(String key) {
        return this.redis.del(key) == 1;
    }

    @Override
    public boolean flush() {
        return this.redis.flushdb().equals("OK");
    }

    @Override
    public String getName() {
        return this.name;
    }

}
