package io.routr.proxy;

import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.Session;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import static java.lang.System.out;

@WebSocket
public class RemoteWSHandler {
    private static final Logger LOG = LogManager.getLogger();
    private Session userSession = null;

    public RemoteWSHandler(Session userSession) {
        this.userSession = userSession;
    }

    @OnWebSocketMessage
    public void onMessage(String message) throws java.io.IOException {
        if (this.userSession.isOpen()) {
            this.userSession.getRemote().sendString(message);
        }
    }

    @OnWebSocketConnect
    public void onConnect(Session sess)  {
        LOG.debug("connection info: ", sess);
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        LOG.debug("closed connection: code = {}, reason = {}",
          statusCode, reason);
    }

    @OnWebSocketError
    public void onError(Throwable cause) {
        LOG.error("error: ", cause);
    }
}
