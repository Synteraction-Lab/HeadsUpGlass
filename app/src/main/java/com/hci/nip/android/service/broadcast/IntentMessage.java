package com.hci.nip.android.service.broadcast;

public class IntentMessage {

    private final String baseUrl;
    private final String json;

    public IntentMessage(String baseUrl, String json) {
        this.baseUrl = baseUrl;
        this.json = json;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getJson() {
        return json;
    }

    @Override
    public String toString() {
        return "IntentMessage{" +
                "baseUrl='" + baseUrl + '\'' +
                ", json='" + json + '\'' +
                '}';
    }
}
