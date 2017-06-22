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
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 *
 */
public class HDStore implements Iterable<Device> {
    private static HDStore _instance = null;
    /**
     * Get the Singleton
     *
     * @return Object $_instance
     **/
    public static HDStore getInstance() {
        if (_instance == null) {
            _instance = new HDStore();
        }
        return _instance;
    }
    public String dirname = "hd40store";
    private String path = "";
    private Path directory;
    private HDCache cache = null;
    private Properties config = new Properties();

    /**
     * Constructor
     *
     **/
    private HDStore() {
    }


     /**
     * Sets the storage config options, optionally creating the storage directory.
     *
     * @param config An assoc array of config info.
     **/
    void setConfig(Properties config) {
        this.setConfig(config, false);
    }

    /**
     * Sets the storage config options, optionally creating the storage directory.
     *
     * @param config An assoc array of config info.
     * @param createDirectory
     **/
    void setConfig(Properties config, boolean createDirectory) {
        this.config = config;
        this.path = this.config.getProperty("filesdir", System.getProperty("java.io.tmpdir"));
        this.directory = FileSystems.getDefault().getPath(this.path, this.dirname);
        this.cache = new HDCache(config);

        if (createDirectory) {
            if (! Files.isDirectory(this.directory)) {
                try {
                    Files.createDirectory(this.directory);
                } catch (IOException ex) {
                    throw new RuntimeException("Error : Failed to create storage directory at " + this.directory + ". Check permissions.");
                }
            }
        }
    }

    /**
     * Write data to cache & disk
     *
     * @param key
     * @param data
     * @return true on success, false otherwise
     */
    boolean write(String key, Object data) throws IOException {
        if (data == null) {
            return false;
        }
        if (! this.store(key, data)) {
            return false;
        }
        return this.cache.write(key, data);
    }

    /**
     * Store data to disk
     *
     * @param key The search key (becomes the filename .. so keep it alphanumeric)
     * @param data Data to persist (will be persisted in json format)
     * @return true on success, false otherwise
     */
    boolean store(String key, Object data) throws IOException {
        String jsonstr = HD4.mapper().writeValueAsString(data);
        Files.write(this.directory.resolve(key + ".json"), jsonstr.getBytes(), StandardOpenOption.CREATE);
        return true;
    }

    /**
     * Read $data, try cache first
     *
     * @param sting $key Key to search for
     * @return true on success, false
     */
    Object read(String key, TypeReference ref) throws IOException {
        Object reply = this.cache.read(key, ref);
        if (reply != null) {
            return reply;
        }
        reply = this.fetch(key, ref);
        if (reply == null) {
            return null;
        }
        if(!this.cache.write(key, reply)) {
            return null;
        }
        return reply;
    }

    /**
     * Fetch data from disk
     *
     * @param key.
     * @reply mixed
     **/
    Object fetch(String key, TypeReference ref) throws IOException {
        byte[] json = Files.readAllBytes(this.directory.resolve(key + ".json"));
        return HD4.mapper().readValue(json, ref);

    }

    /**
     * Returns all devices inside one giant array
     *
     * Used by localDevice* functions to iterate over all devies
     *
     * @return All devices in one giant assoc array
     **/
    List<Device> fetchDevices() throws IOException {
        List<Device> data = new ArrayList<>();
        Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                if (path.getFileName().toString().matches("Device.*\\.json")) {
                    byte[] json = Files.readAllBytes(path);
                    Object obj = HD4.mapper().readValue(json, new TypeReference<Map<String, Device>>() {});
                    @SuppressWarnings("unchecked")
                    Map<String, Device> map = (Map<String, Device>)obj;
                    data.add(map.get("Device"));
                }
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return data;
    }

    /**
     * Moves a json file into storage.
     *
     * @param srcAbsName The fully qualified path and file name eg /tmp/sjjhas778hsjhh
     * @param destName The key name inside the cache eg Device_19.json
     * @return true on success, false otherwise
     */
    boolean moveIn(byte[] content, String destName) throws IOException {
        Files.write(this.directory.resolve(destName), content, StandardOpenOption.CREATE);
        return true;
    }

    /**
     * Cleans out the store - Use with caution
     *
     * @return true on success, false otherwise
     **/
    boolean purge() throws IOException {
        Files.walkFileTree(this.directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                Files.delete(path);
                return FileVisitResult.CONTINUE;
            }
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });
        return this.cache.purge();
    }

    /**
     * Returns the store directory
     *
     * @return directory
     */
    public String getDirectory() {
        return this.directory.toString();
    }

    @Override
    public Iterator<Device> iterator()  {
        return new DeviceIterator(this);
    }

    private class DeviceIterator implements Iterator<Device> {
        private int indexPosition;
        private List<String> indexArray;
        private HDStore store;

        DeviceIterator(HDStore store) {
            this.store = store;
            this.indexPosition = 0;
            // Build the device list.
            this.indexArray = new ArrayList<>();
            try {
                Files.walkFileTree(store.directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                        if (path.getFileName().toString().startsWith("Device_")) {
                            indexArray.add(path.getFileName().toString().replace(".json", ""));
                        }
                        return FileVisitResult.CONTINUE;
                    }
                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException ex)  {
                throw new RuntimeException(ex);
            }
        }

        @Override
        public boolean hasNext() {
            return this.indexArray.size() > this.indexPosition + 1;
        }

        @Override
        public Device next() {
            if(this.hasNext()) {
                this.indexPosition++;
                String file = this.indexArray.get(this.indexPosition);
                Object obj;
                try {
                    obj = this.store.fetch(file, new TypeReference<Map<String, Device>>() {});
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                @SuppressWarnings("unchecked")
                Map<String, Device> map = (Map<String, Device>)obj;
                return map.get("Device");
            }
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
