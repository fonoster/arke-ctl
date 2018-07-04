package com.fonoster.sipio.proxy;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

public class APIProxy extends org.eclipse.jetty.proxy.ProxyServlet.Transparent {

    @Override
    protected HttpClient newHttpClient() {
        SslContextFactory factory = new SslContextFactory();
        factory.setTrustAll(true);
        return new HttpClient(factory);
    }

    @Override
    protected String rewriteTarget(HttpServletRequest request) {
        String rewrittenURI = super.rewriteTarget(request);
        String apiKeyName = getInitParameter("apiKeyName");

        if (apiKeyName != null) {
            String fragment = apiKeyName + "=" + getInitParameter("apiKeyValue");

            String queryString = request.getQueryString();

            rewrittenURI += (queryString == null ? "?" : "&") + fragment;

            rewrittenURI = URI.create(rewrittenURI).normalize().toString();
        }

        return rewrittenURI;
    }

}
