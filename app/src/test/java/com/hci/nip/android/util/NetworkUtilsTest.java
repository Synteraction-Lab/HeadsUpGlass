package com.hci.nip.android.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class NetworkUtilsTest {

    @Test
    public void testGetIpAddress() {
        throw new RuntimeException("Not implemented yet");
    }

    @DataProvider
    public Object[][] getDataIsIpAddressWithPort() {
        return new Object[][]{
                {
                        "127.0.0.1:123",
                        true
                },
                {
                        "http://127.0.0.1:123",
                        false
                },
        };
    }

    @Test(dataProvider = "getDataIsIpAddressWithPort")
    public void isIpAddressWithPort(String ipAddress, boolean expected) {
        assertEquals(NetworkUtils.isIpAddressWithPort(ipAddress), expected);
    }

    @DataProvider
    public Object[][] getDataGetIpAddress() {
        return new Object[][]{
                {
                        "127.0.0.1:123",
                        "127.0.0.1"
                },
                {
                        "http://127.0.0.1:123",
                        "0.0.0.0"
                },
        };
    }

    @Test(dataProvider = "getDataGetIpAddress")
    public void getIpAddress(String ipAddress, String expected) {
        assertEquals(NetworkUtils.getIpAddress(ipAddress), expected);
    }

    @DataProvider
    public Object[][] getDataGetPortAddress() {
        return new Object[][]{
                {
                        "127.0.0.1:123",
                        123
                },
                {
                        "http://127.0.0.1:123",
                        0
                },
        };
    }

    @Test(dataProvider = "getDataGetPortAddress")
    public void getPortAddress(String ipAddress, int expected) {
        assertEquals(NetworkUtils.getPort(ipAddress), expected);
    }
}