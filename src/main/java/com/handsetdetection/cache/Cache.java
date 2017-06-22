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
import java.io.IOException;

/**
 *
 */
public interface Cache {
    /** Get data for a key.
     * @param key The cache key
     * @param ref Reference to the data type
     * @return  data
     * @throws java.io.IOException I/O failure */
    public Object get(String key, TypeReference ref) throws IOException;

    /** Set data for a key
     * @param key The cache key
     * @param data Data to cache
     * @param ttl In seconds
     * @return  result
     * @throws java.io.IOException I/O failure */
    public boolean set(String key, Object data, int ttl) throws IOException;

    /** Delete a key
     * @param key The cache key
     * @return  result */
    public boolean del(String key);

    /** Flush cache
     * @return  result */
    public boolean flush();

    /** Get cache name
     * @return  name */
    public String getName();
}