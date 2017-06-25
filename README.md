[![Build Status](https://travis-ci.org/HandsetDetection/detection-apikit-java.svg?branch=master)](https://travis-ci.org/HandsetDetection/detection-apikit-java)
[ ![Download](https://api.bintray.com/packages/handsetdetection/mvn/detection-apikit-java/images/download.svg) ](https://bintray.com/handsetdetection/mvn/detection-apikit-java/_latestVersion)
[![MIT License](https://badges.frapsoft.com/os/mit/mit.png?v=103)](https://opensource.org/licenses/mit-license.php)

# Java API Kit v4, implementing v4.0 of the HandsetDetection API. #

API Kits can use our web service or resolve detections locally
depending on your configuration.


## Installation ##

The Java API Kit is released via JCenter. You can include it in your project using your preferred build tool, such as Gradle, Maven, SBT...

Example configuration for Gradle:

    repositories {
        jcenter()
    }

    dependencies {
        compile 'com.handsetdetection:detection-apikit-java:4.2.0'
    }


For detailed instructions, direct downloads and other information visit the [Java API Kit page on Bintray](https://bintray.com/handsetdetection/mvn/detection-apikit-java).


## Configuration ##

API Kit configuration files can be downloaded directly from Handset Detection.

1. Login to your dashboard
2. Click 'Add a Site'
3. Configure your new site
4. Grab the config file variables for your API Kit (from the site settings)
5. Place the variables into the `hdconfig.properties` file


## Examples ##

### Instantiate the HD4 object ###

    import com.handsetdetection.HD4;
    import com.handsetdetection.data.*;

    // Using the default config file: hdconfig.properties in the current working directory
    HD4 hd = new HD4();

    // OR using a custom config file
    HD4 hd = new HD4("/tmp/myCustomConfigFile.properties");

### List all vendors ###

    if (hd.deviceVendors()) {
        Reply data = hd.getReply();
        List<String> vendors = data.getVendor();
    } else {
        System.out.println(hd.getError());
    }

### List all device models for a vendor (Nokia) ###

    if (hd.deviceModels("Nokia")) {
        Reply data =  hd.getReply();
        List<String> models = data.getModel();
    } else {
        System.out.println(hd.getError());
    }

### View information for a specific device (Nokia N95) ###

    if (hd.deviceView("Nokia","N95")) {
        Reply data =  hd.getReply();
        HdSpecs deviceSpecs = data.getDevice();
    } else {
        System.out.println(hd.getError());
    }

### What devices have this attribute ? ###

    if (hd.deviceWhatHas("network","CDMA")) {
        Reply data =  hd.getReply();
        List<DeviceSummary> devices = data.getDevices();
    } else {
        System.out.println(hd.getError());
    }

### Basic device detection ###

    hd.setDetectVar("user-agent","Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95-3/20.2.011 Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413");
    hd.setDetectVar("x-wap-profile","http://nds1.nds.nokia.com/uaprof/NN95-1r100.xml");
    if (hd.deviceDetect()) {
        Reply data =  hd.getReply();
        HdSpecs deviceSpecs = data.getDevice();
    } else {
        System.out.println(hd.getError());
    }

### Download the Full Ultimate Edition ###

    // Increase the default timeout before downloading the archive
    hd.setTimeout(500);
    if (hd.deviceFetchArchive()) {
        byte[] data =  hd.getRawReply();
        System.out.println("Downloaded " + data.length + " bytes");
    } else {
        System.out.println(hd.getError());
        System.out.println(Arrays.toString(hd.getRawReply()));
    }

### Download the Community Ultimate Edition ###

    hd.setTimeout(500);
    if (hd.communityFetchArchive()) {
        byte[] data =  hd.getRawReply();
        System.out.println("Downloaded " + data.length + " bytes");
    } else {
        System.out.println(hd.getError());
        System.out.println(Arrays.toString(hd.getRawReply()));
    }

## Flexible Caching Options

This version includes several caching options:

* In-memory (Guava Cache)
* Filesystem
* Memcached
* Redis

### Using Guava

Include the following cache configuration in your config file.

    cache.memory.maximumSize = 10000000

or simply, 

    cache.memory = true

### Using the filesystem

Include this cache configuration in your config file:

    cache.file.directory = /tmp

or simply,

    cache.file = true

### Using Memcached

Include the following cache configuration in your config file.

    cache.memcached.servers = server1.example.com:11211 server2.example.com:11211

or simply,

    cache.memcached = true


### Using Redis

We also have Redis as a caching option. Use a caching config as follows:

    cache.redis.uri = redis://localhost/

or simply,

    cache.redis = true

### Disabling the cache

    cache.none = true

## Extra Examples ##

Additional examples can be found in the `Examples.java` file.


## Getting Started with the Free usage tier and Community Edition ##

After signing up with our service you'll be on a free usage tier which entitles you to 20,000 Cloud detections (web service)
per month, and access to our Community Edition for Ultimate (stand alone) detection. The archive for stand alone detection
can be downloaded manually however its easiest to configure the API kit with your credentials and let the API kit do the
heavy lifting for you. See examples above for how to do this.

Instructions for manually installing the archive are available at [v4 API Ultimate Community Edition, Getting Started](https://handsetdetection.readme.io/docs/getting-started-with-ultimate-community-full-editions).


## Unit testing ##

Unit tests use JUnit and can be found in the `src/test` directory.


## API Documentation ##

See the [v4 API Documentation](https://handsetdetection.readme.io).


## API Kits ##

See the [Handset Detection GitHub Repo](https://github.com/HandsetDetection).


## Support ##

Let us know if you have any hassles (hello@handsetdetection.com).
