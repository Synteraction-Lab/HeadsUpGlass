package com.hci.nip.android.util;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.WIFI_SERVICE;

public final class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getName();

    private static final String IP_PORT_FORMAT = "^(\\d+\\.\\d+\\.\\d+\\.\\d+):(\\d+)$";
    private static final Pattern IP_PORT_PATTERN = Pattern.compile(IP_PORT_FORMAT);

    public static String getIpAddress(Context context) {
        int hostAddress = 0;
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            hostAddress = wifiManager.getConnectionInfo().getIpAddress();
        } catch (Exception e) {
            Log.e(TAG, "getIpAddress", e);
        }
        return (0xff & hostAddress) + "." + (0xff & (hostAddress >> 8)) + "." + (0xff & (hostAddress >> 16)) + "." + (0xff & (hostAddress >> 24));
    }

    /**
     * @param ipAddressWithPort
     * @return true if the format is <IP_ADDRESS>:<PORT>
     */
    public static boolean isIpAddressWithPort(String ipAddressWithPort) {
        return IP_PORT_PATTERN.matcher(ipAddressWithPort).find();
    }

    /**
     * @param ipAddress
     * @return ip address if found, else return "0.0.0.0"
     */
    public static String getIpAddress(String ipAddress) {
        Matcher matcher = IP_PORT_PATTERN.matcher(ipAddress);
        String ip = "0.0.0.0";
        if (matcher.find()) {
            ip = matcher.group(1);
        }
        return ip;
    }

    /**
     * @param ipAddressWithPort
     * @return port if found, else return 0
     */
    public static int getPort(String ipAddressWithPort) {
        Matcher matcher = IP_PORT_PATTERN.matcher(ipAddressWithPort);
        int port = 0;
        if (matcher.find()) {
            try {
                port = Integer.parseInt(matcher.group(2));
            } catch (NumberFormatException e) {
                Log.e(TAG, "getPort", e);
            }
        }
        return port;
    }


}
