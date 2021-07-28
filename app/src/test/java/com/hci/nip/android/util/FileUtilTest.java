package com.hci.nip.android.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class FileUtilTest {

    @DataProvider
    public Object[][] getDataIsAudioFile() {
        return new Object[][]{
                {
                        "abs.mp3",
                        true
                },
        };
    }

    @Test(dataProvider = "getDataIsAudioFile")
    public void isAudioFile(String name, boolean expected) {
        assertEquals(FileUtil.isAudioFile(name), expected);
    }

    @DataProvider
    public Object[][] getDataIsUrl() {
        return new Object[][]{
                {
                        "https://www.google.com/",
                        true
                },
                {
                        "http://127.0.0.1:8080/abs.mp3",
                        true
                },
        };
    }

    @Test(dataProvider = "getDataIsUrl")
    public void isUrl(String url, boolean expected) {
        assertEquals(FileUtil.isUrl(url), expected);
    }

    @DataProvider
    public Object[][] getDataGetMimeType() {
        return new Object[][]{
                {
                        "absc.aac",
                        "audio/x-aac"
                },
                {
                        "http://127.0.0.1:8080/abs.mp3",
                        "audio/mpeg"
                },
                {
                        "abs.png",
                        "image/png"
                },
                {
                        "abs.3gp",
                        "video/3gpp"
                },
                {
                        "abs.mp4",
                        "video/mp4"
                },
        };
    }

    @Test(dataProvider = "getDataGetMimeType")
    public void getMimeType(String fileName, String expected) {
        assertEquals(FileUtil.getMimeType(fileName, null), expected);
    }
}