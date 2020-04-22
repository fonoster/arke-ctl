package io.routr.proxy;

import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.websocket.api.CloseException;
import java.net.URISyntaxException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URI;
import java.util.concurrent.Future;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.commons.lang3.exception.ExceptionUtils;

import static java.lang.System.out;

public class WSHandler extends WebSocketAdapter {
    private Session userSession;
    private Session remoteSession;
    private static final Logger LOG = LogManager.getLogger();

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        LOG.debug("closed connection: code = {}, reason = {}",
          statusCode, reason);
    }

    @Override
    public void onWebSocketConnect(Session userSession) {
        this.userSession = userSession;
        try {
            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setTrustAll(true);
            WebSocketClient client = new WebSocketClient(sslContextFactory);

            client.start();
            Future<Session> fut = client.connect(
              new RemoteWSHandler(userSession), URI.create(this.getUrl()));
            this.remoteSession = fut.get();
        } catch (Exception ex) {
            LOG.error(ExceptionUtils.getRootCause(ex));
        }
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        // Simply ignored if is close exception
        if (cause instanceof CloseException == false)
            LOG.error("error: ", cause);
    }

    @Override
    public void onWebSocketText(String message) {
        try {
            if (remoteSession.isOpen()) {
                remoteSession.getRemote().sendString(message);
            }
        } catch(java.io.IOException ex) {
            LOG.error(ExceptionUtils.getRootCause(ex));
        }
    }

    private static String getUrl() throws URISyntaxException, MalformedURLException {
        URL apiUrl = new URI(System.getenv("ROUTR_API_URL")).toURL();
        String apiUrlHost = "wss://" + apiUrl.getHost();

        if ( apiUrl.getPort() != -1) {
            apiUrlHost += ":" + apiUrl.getPort();
        }

        // WARNING: Harcoded apiversion
        String url = String.join(
            "",
            apiUrlHost,
            "/api/v1beta1/system/logs-ws?token=",
            System.getenv("ROUTR_API_TOKEN")
        );

        return url;
    }
}
