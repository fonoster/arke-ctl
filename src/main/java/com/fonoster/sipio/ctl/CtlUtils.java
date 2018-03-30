package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import static java.lang.System.exit;
import static java.lang.System.out;

public class CtlUtils {
    private static String accessToken;
    private static String apiUrl;
    private static String username;
    private static String password;

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
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new NoopHostnameVerifier());
            CloseableHttpClient httpclient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            Unirest.setHttpClient(httpclient);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CtlUtils(String apiUrl, String username, String password) {
        CtlUtils.apiUrl = apiUrl;
        CtlUtils.username = username;
        CtlUtils.password = password;
    }

    public CtlUtils(String apiUrl, String accessToken) {
        CtlUtils.apiUrl = apiUrl;
        CtlUtils.accessToken = accessToken;
    }

    String getToken() {
        HttpResponse<String> result = null;

        try {
            result = Unirest.get(apiUrl + "/credentials" ).basicAuth(username, password).asString();
        } catch (UnirestException ex) {
            ex.printStackTrace();
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        if (result.getStatus() == 401) {
            out.println("Unable to retrieve token. Please verify your credentials");
            exit(1);
        }

        errorHandling(result);

        return result.getBody();
    }

    public HttpResponse getWithToken(String resource, String params) {
        HttpResponse result = null;

        try {
            result = Unirest.get(apiUrl + "/" + resource + "?token=" + accessToken + "&" + params).asString();
        } catch (UnirestException ex) {
            if (resource.equals("system/status")) {
                out.println("Down");
            } else {
                out.println("Unable to perform request. Ensure server is up");
            }
           exit(1);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        errorHandling(result);

        return result;
    }

    HttpResponse postWithToken(String resource, String data) {
        HttpResponse result = null;

        try {
            if (data != null && !data.isEmpty()) {
                result = Unirest.post(apiUrl + "/" + resource + "?token=" + accessToken).body(data).asString();
            } else {
                result = Unirest.post(apiUrl + "/" + resource + "?token=" + accessToken).asString();
            }
        } catch (UnirestException ex) {
            if (resource.equals("system/status/down")) {
                out.println("Down");
            } else {
                out.println("Unable to perform request. Ensure server is up");
            }
            exit(1);
        }

        errorHandling(result);

        return result;
    }

    HttpResponse putWithToken(String resource, String data) {
        HttpResponse result = null;

        try {
            if (data != null && !data.isEmpty()) {
                result = Unirest.put(apiUrl + "/" + resource + "?token=" + accessToken).body(data).asString();
            } else {
                result = Unirest.put(apiUrl + "/" + resource + "?token=" + accessToken).asString();
            }
        } catch (UnirestException ex) {
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        errorHandling(result);

        return result;
    }

    HttpResponse deleteWithToken(String resource, String params) {
        HttpResponse result = null;

        try {
            result = Unirest.delete(apiUrl + "/" + resource + "?token=" + accessToken + "&" + params).asString();
        } catch (UnirestException ex) {
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        errorHandling(result);

        return result;
    }

    private void errorHandling(HttpResponse result) {
        if (result.getStatus() == 404) {
            out.println("Invalid api path");
            exit(1);
        }

        if (result.getStatus() == 401) {
            out.println(Main.INVALID_ACCESS_TOKEN);
            exit(1);
        }

        if (result.getStatus() == 500) {
            out.println("Internal server error");
            exit(1);
        }
    }
}

