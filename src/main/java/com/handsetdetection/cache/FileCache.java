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
import com.handsetdetection.HD4;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;


/**
 *
 */
public class FileCache implements Cache {

    private final Path dir;
    private final String name = "file";
    private final String generalPrefix;
    private final int generalDuration;

    public FileCache(Properties config) {
        this.dir = Paths.get(config.getProperty("cache." + this.name + "directory",
                System.getProperty("java.io.tmpdir")));
        this.generalPrefix = config.getProperty("cache.prefix", "hd40");
        this.generalDuration = Integer.parseInt(config.getProperty("cache.ttl", "7200"));
        if (!Files.isDirectory(this.dir)) {
            throw new RuntimeException("Cache directory does not exist.");
        }
        if (!Files.isWritable(this.dir)) {
            throw new RuntimeException("Cache directory is now writeable.");
        }
    }

    @Override
    public Object get(String key, TypeReference ref) throws IOException {
        if (!Files.exists(this.dir.resolve(key))) {
            return null;
        }
        BasicFileAttributes attr = Files.readAttributes(this.dir, BasicFileAttributes.class);
        if (System.currentTimeMillis() - attr.creationTime().toMillis() > this.generalDuration * 1_000) {
            return null;
        }
        byte[] data = Files.readAllBytes(this.dir.resolve(key));
        return HD4.mapper().readValue(data, ref);
    }

    @Override
    public boolean set(String key, Object data, int ttl) throws IOException {
        Files.write(this.dir.resolve(key), HD4.mapper().writeValueAsString(data).getBytes());
        return true;
    }

    @Override
    public boolean del(String key) {
        try {
            Files.deleteIfExists(this.dir.resolve(key));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    @Override
    public boolean flush() {
        try {
            Files.walkFileTree(this.dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().startsWith(generalPrefix)) {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });

        } catch (AccessDeniedException ex) {
            // Pass
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
