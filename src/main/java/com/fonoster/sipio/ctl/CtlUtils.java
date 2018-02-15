package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;


public class CtlUtils {
    private final static String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.S7c6sLg7MvF2iPN4XksXQ4O6TIl-ln8Y8fqXPWgEj8pGQqHNFFXHOec-RqBUGiXKuWXlf_TSlZVfo2xy7AUhLg";
    private final static String baseUrl = "https://localhost:4567";

    // Warn: This should be be a parameter (--insecure?)
    static {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                    }
                }
            };

            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
            Unirest.setHttpClient(httpclient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getWithToken(String resource, String params) throws UnirestException {
        HttpResponse<JsonNode> result = Unirest.get(baseUrl + "/" + resource + "?token=" + token + "&" + params).asJson();
        return result.getBody().toString();
    }

    public HttpResponse postWithToken(String resource, String data) throws UnirestException {
        HttpResponse result;

        if (!data.isEmpty()) {
            result = Unirest.post(this.baseUrl + "/" + resource + "?token=" + token).body(data).asString();
        } else {
            result = Unirest.post(this.baseUrl + "/" + resource + "?token=" + token).asString();
        }

        return result;
    }

}

