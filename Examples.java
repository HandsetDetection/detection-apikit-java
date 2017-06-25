/*
** Handset Detection - API call examples
*/

import com.handsetdetection.HD4;
import com.handsetdetection.data.Reply;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.builder.ToStringBuilder;

class Examples {

    public static void main(String[] args) {

        String configFile = "hdconfig.properties";
        HD4 hd = new HD4(configFile);

        /// Vendors example : Get a list of all vendors
        System.out.println("* Vendors *");
        if (hd.deviceVendors()) {
            Reply data = hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // Models example : Get a list of all models for a specific vendor
        System.out.println("* Nokia Models *");
        if (hd.deviceModels("Nokia")) {
            Reply data =  hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // View information for a specific handset
        System.out.println("* Nokia N95 Properties *");
        if (hd.deviceView("Nokia","N95")) {
            Reply data =  hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
            System.out.println(ToStringBuilder.reflectionToString(data.getDevice()));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // What handset have this attribute ?
        System.out.println("* Handsets with Network CDMA *");
        if (hd.deviceWhatHas("network","CDMA")) {
            Reply data =  hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
            System.out.println(ToStringBuilder.reflectionToString(data.getDevices()));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // ***************************** Detection Examples ********************************
        // This manually sets the headers that a Nokia N95 would set.
        // Other agents you also might like to try
        // Mozilla/5.0 (BlackBerry; U; BlackBerry 9300; es) AppleWebKit/534.8+ (KHTML, like Gecko) Version/6.0.0.534 Mobile Safari/534.8+
        // Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95-3/20.2.011 Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413
        // Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5
        System.out.println("* Simple Detection - Setting Headers for an N95 *");
        hd.setDetectVar("user-agent","Mozilla/5.0 (SymbianOS/9.2; U; Series60/3.1 NokiaN95-3/20.2.011 Profile/MIDP-2.0 Configuration/CLDC-1.1 ) AppleWebKit/413");
        hd.setDetectVar("x-wap-profile","http://nds1.nds.nokia.com/uaprof/NN95-1r100.xml");
        if (hd.deviceDetect()) {
            Reply data =  hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
            System.out.println(ToStringBuilder.reflectionToString(data.getHdSpecs()));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // Query for some other information (remember the N95 headers are still set).
        // Add detection options to query for additional reply information such as geoip information
        // Note : We use the ipaddress to get the geoip location.
        System.out.println("* Simple Detection - Passing a different ip address *");
        hd.setDetectVar("ipaddress", "64.34.165.180");
        Map<String, String> params = new HashMap<>();
        params.put("options", "geoip,hd_specs");
        if (hd.deviceDetect(params)) {
            Reply data =  hd.getReply();
            System.out.println(ToStringBuilder.reflectionToString(data));
            System.out.println(ToStringBuilder.reflectionToString(data.getHdSpecs()));
        } else {
            System.out.println(hd.getError());
        }
        System.out.println();

        // Ultimate customers can also download the ultimate database.
        // Note  - Increase default timeout
        System.out.println("* Archive Information *");
        hd.setTimeout(500);
        long timeStart = System.currentTimeMillis();
        if (hd.deviceFetchArchive()) {
            byte[] data =  hd.getRawReply();
            System.out.println("Downloaded " + data.length + " bytes");
        } else {
            System.out.println(hd.getError());
            System.out.println(Arrays.toString(hd.getRawReply()));
        }
        long timeElapsed = System.currentTimeMillis() - timeStart;
        System.out.println("Time elapsed " + timeElapsed + "ms");
        System.out.println();
    }
}
