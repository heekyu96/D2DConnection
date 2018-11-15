package com.example.heegi.d2dconnection.Singleton;

public class SsidTable {
    private static SsidTable instance = new SsidTable();

    public static SsidTable getInstance() {
        return instance;
    }

    private String ssid1Id;
    private String ssid1Pw;

    private String ssid2Id;
    private String ssid2Pw;

    private String ssid3Id;
    private String ssid3Pw;

    public String getSsid1Id() {
        return ssid1Id;
    }

    public void setSsid1Id(String ssid1Id) {
        this.ssid1Id = ssid1Id;
    }

    public String getSsid1Pw() {
        return ssid1Pw;
    }

    public void setSsid1Pw(String ssid1Pw) {
        this.ssid1Pw = ssid1Pw;
    }

    public String getSsid2Id() {
        return ssid2Id;
    }

    public void setSsid2Id(String ssid2Id) {
        this.ssid2Id = ssid2Id;
    }

    public String getSsid2Pw() {
        return ssid2Pw;
    }

    public void setSsid2Pw(String ssid2Pw) {
        this.ssid2Pw = ssid2Pw;
    }

    public String getSsid3Id() {
        return ssid3Id;
    }

    public void setSsid3Id(String ssid3Id) {
        this.ssid3Id = ssid3Id;
    }

    public String getSsid3Pw() {
        return ssid3Pw;
    }

    public void setSsid3Pw(String ssid3Pw) {
        this.ssid3Pw = ssid3Pw;
    }
}
