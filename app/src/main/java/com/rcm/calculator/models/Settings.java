package com.rcm.calculator.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Settings {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("versionCode")
    @Expose
    private String versionCode;
    @SerializedName("adsStatus")
    @Expose
    private String adsStatus;
    @SerializedName("interstitialAdsCount")
    @Expose
    private String interstitialAdsCount;
    @SerializedName("adsLimit")
    @Expose
    private String adsLimit;
    @SerializedName("bannerAdmob")
    @Expose
    private String bannerAdmob;
    @SerializedName("interstitialAdmob")
    @Expose
    private String interstitialAdmob;
    @SerializedName("nativeAdmob")
    @Expose
    private String nativeAdmob;
    @SerializedName("appOpenAdmob")
    @Expose
    private String appOpenAdmob;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getAdsStatus() {
        return adsStatus;
    }

    public void setAdsStatus(String adsStatus) {
        this.adsStatus = adsStatus;
    }

    public String getInterstitialAdsCount() {
        return interstitialAdsCount;
    }

    public void setInterstitialAdsCount(String interstitialAdsCount) {
        this.interstitialAdsCount = interstitialAdsCount;
    }

    public String getAdsLimit() {
        return adsLimit;
    }

    public void setAdsLimit(String adsLimit) {
        this.adsLimit = adsLimit;
    }

    public String getBannerAdmob() {
        return bannerAdmob;
    }

    public void setBannerAdmob(String bannerAdmob) {
        this.bannerAdmob = bannerAdmob;
    }

    public String getInterstitialAdmob() {
        return interstitialAdmob;
    }

    public void setInterstitialAdmob(String interstitialAdmob) {
        this.interstitialAdmob = interstitialAdmob;
    }

    public String getNativeAdmob() {
        return nativeAdmob;
    }

    public void setNativeAdmob(String nativeAdmob) {
        this.nativeAdmob = nativeAdmob;
    }

    public String getAppOpenAdmob() {
        return appOpenAdmob;
    }

    public void setAppOpenAdmob(String appOpenAdmob) {
        this.appOpenAdmob = appOpenAdmob;
    }
}