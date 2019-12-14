package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
        HttpResponse<String> response = null;

        try {
            response = Unirest.get(apiUrl + "/credentials" ).basicAuth(username, password).asString();
        } catch (UnirestException ex) {
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        if (response.getStatus() == 401) {
            out.println("Unable to retrieve token. Please verify your credentials");
            exit(1);
        }

        errorHandling(response);

        return response.getBody();
    }

    public HttpResponse getWithToken(String resource, String params) {
        HttpResponse response = null;

        try {
            response = Unirest.get(apiUrl + "/" + resource + "?token=" + accessToken + "&" + params).asString();
        } catch (UnirestException ex) {
            if (resource.equals("system/status")) {
                out.println("Down");
            } else {
                out.println("Unable to perform request. Ensure server is up");
            }
           exit(1);
        }

        errorHandling(response);

        return response;
    }

    HttpResponse postWithToken(String resource, String data, String params) {
        HttpResponse response = null;

        try {
            if (data != null && !data.isEmpty()) {
                response = Unirest.post(apiUrl + "/" + resource + "?token="
                    + accessToken + "&" + params).body(data).asString();
            } else {
                response = Unirest.post(apiUrl + "/" + resource + "?token="
                    + accessToken + "&" + params).asString();
            }
        } catch (UnirestException ex) {
            if (resource.equals("system/status/down")) {
                out.println("Down");
            } else {
                out.println("Unable to perform request. Ensure server is up");
            }
            exit(1);
        }

        errorHandling(response);

        return response;
    }

    HttpResponse putWithToken(String resource, String data) {
        HttpResponse response = null;

        try {
            if (data != null && !data.isEmpty()) {
                response = Unirest.put(apiUrl + "/" + resource + "?token=" + accessToken).body(data).asString();
            } else {
                response = Unirest.put(apiUrl + "/" + resource + "?token=" + accessToken).asString();
            }
        } catch (UnirestException ex) {
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        errorHandling(response);

        return response;
    }

    HttpResponse deleteWithToken(String resource, String params) {
        HttpResponse response = null;

        try {
            response = Unirest.delete(apiUrl + "/" + resource + "?token=" + accessToken + "&" + params).asString();
        } catch (UnirestException ex) {
            out.println("Unable to perform request. Ensure server is up");
            exit(1);
        }

        errorHandling(response);

        return response;
    }

    private void errorHandling(HttpResponse response) {
        if (response.getStatus() == 404) {
            out.println("Invalid api path");
            exit(1);
        }

        if (response.getStatus() == 400) {
            Gson gson = new Gson();
            JsonObject jo = gson.fromJson(response.getBody() + "", JsonObject.class);
            String message = jo.get("message").getAsString();
            out.println(message);
            exit(1);
        }

        if (response.getStatus() == 401) {
            out.println(Main.INVALID_ACCESS_TOKEN);
            exit(1);
        }

        if (response.getStatus() == 500) {
            out.println("Internal server error");
            exit(1);
        }
    }
}
