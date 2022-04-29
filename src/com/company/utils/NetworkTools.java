package com.company.utils;

import com.company.vinfo.VakcinaInfoParser;
import javax.net.ssl.*;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkTools {

    public NetworkTools() { }

    private static final String defaultUrl = "https://koronavirus.gov.hu/elhunytak?page=";
    private static final String defaultUrlShort ="https://koronavirus.gov.hu/elhunytak";
    private static final String worldometerUrl = "https://www.worldometers.info/coronavirus/country/hungary/";

    public void showNetworkState() {
        if (testNetwork()) {
            log("showNetworkState()", "All pages available. ");
        }
        else {
            log("showNetworkState()", "Needed pages not available. ");
        }
    }

    public boolean testNetwork() {
        boolean result = false;
        setHTTPSconnectionEnviroment();
        if (checkThisPage(defaultUrl) && checkThisPage(worldometerUrl)) {
            result = true;
        }
        return result;
    }

    private boolean checkThisPage(String url) {
        boolean result = false;

        try {
            HttpURLConnection httpUrlConn;
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();
            httpUrlConn.setRequestMethod("HEAD");
            httpUrlConn.setConnectTimeout(30000);
            httpUrlConn.setReadTimeout(30000);
            result = true;
        }  catch (IOException e) {
            log("checkThisPage()", "Page is unavailable. + " + e);
        }

        return result;
    }

    public void setHTTPSconnectionEnviroment() {
        TrustManager[] trustAllCerts =  trustAllCertificates();
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (NoSuchAlgorithmException e) {
            log("setHTTPSconnectionEnviroment()", "Cryptographic algorithm not available. + " + e);
        } catch (KeyManagementException e) {
            log("setHTTPSconnectionEnviroment()", "Key management error. + " + e);
        }
    }

    private TrustManager[] trustAllCertificates() {

        return new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {  }
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {  }
                }
        };
    }

    private void log(String methodName, String message) {
        Logger.getLogger(NetworkTools.class.getName())
                .log(Level.INFO, "NetworkTools/" + methodName + " - " + message);
    }

}
