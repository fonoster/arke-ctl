package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.inamik.text.tables.GridTable;
import com.inamik.text.tables.SimpleTable;
import com.inamik.text.tables.grid.Border;
import com.inamik.text.tables.grid.Util;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import javax.websocket.server.ServerContainer;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.websocket.jsr356.server.deploy.WebSocketServerContainerInitializer;
import java.util.Timer;
import java.util.TimerTask;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.awt.Desktop;
import java.io.IOException;
import java.lang.InterruptedException;
import java.awt.HeadlessException;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.UnsatisfiedLinkError;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import static java.lang.System.out;

class CmdProxy {

    private static String ANSI_BOLD = "\u001B[1m";
    private static String ANSI_BLUE = "\u001B[36m";
    private static String ANSI_GREEN = "\u001B[32m";
    private static String ANSI_RESET = "\u001B[0m";
    private static Server server;
    private static URL proxyToURL;
    private static ServerConnector connector;

    CmdProxy(Subparsers subparsers) {
        // Experimental solution to disable those anoying log messages
        java.lang.System.setProperty("org.eclipse.jetty.LEVEL", "ERROR");
        org.apache.log4j.BasicConfigurator.configure(new org.apache.log4j.varia.NullAppender());
        Subparser proxy = subparsers.addParser("proxy").help("run a proxy to the server (beta)");
        proxy.addArgument("-p", "--port").type(Integer.class).setDefault(8000).help("The port on which to run the proxy");
    }

    void run(String apiUrl, String token, int port) throws Exception {
        // Create Server
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);

        URL proxyToURL = new URI(apiUrl).toURL();
        String proxyTo = proxyToURL.getProtocol() + "://" + proxyToURL.getHost();

        if (proxyToURL.getPort() != -1) {
            proxyTo = proxyTo + ":" + proxyToURL.getPort();
        }

        CmdProxy.connector = connector;
        CmdProxy.proxyToURL = proxyToURL;
        CmdProxy.server = server;

        // Setting Proxy Servlet
        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        sch.setErrorHandler(new ErrorHandler());

        ServletHolder proxyServlet = new ServletHolder(io.routr.proxy.APIProxy.class);
        proxyServlet.setInitParameter("apiKeyName", "token");
        proxyServlet.setInitParameter("apiKeyValue", token);
        proxyServlet.setInitParameter("proxyTo", proxyTo);

        ServletHolder wsHolder = new ServletHolder("ws", new WebSocketServlet() {
            @Override
            public void configure(WebSocketServletFactory factory) {
                // set a 10 minutes idle timeout
                factory.getPolicy().setIdleTimeout(600 * 1000);
                factory.register(io.routr.proxy.WSHandler.class);
            }
        });

        // Setting Resources Servlet
        ServletHolder defServlet = new ServletHolder(DefaultServlet.class);
        defServlet.setInitParameter("resourceBase", System.getenv("ROUTR_WEBAPP"));
        defServlet.setInitParameter("dirAllowed", "true");

        sch.addServlet(proxyServlet, "/api/*");
        sch.addServlet(defServlet, "/*");
        sch.addServlet(wsHolder, "/api/v1beta1/system/logs-ws"); // WARNING: Harcoded apiversion
        server.setHandler(sch);

        // Start the server
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run () {
                try {
                    boolean isRemoteAvailable = CmdProxy.hostAvailabilityCheck(proxyToURL.getHost(),
                    proxyToURL.getPort());
                    if (isRemoteAvailable && !connector.isRunning()) {
                        server.addConnector(connector);
                        connector.start();
                    }
                } catch(Exception ex) {
                    /* ignore */
                }
            }
        }, 5 * 1000, 5 * 1000);

        openBrowser(proxyToURL.getHost(), port);
    		server.start();
    		server.join();
    }

    void openBrowser(String host, int port) {
        CmdProxy.clrscr();
        String url = "http://localhost:" + port;
        out.println(ANSI_GREEN + "Serving Routr Console" + ANSI_RESET);
        out.println("\nYou can now view the " + ANSI_BOLD + "console" + ANSI_RESET + " in the browser.\n");
        out.println(ANSI_BOLD + "  URL\t" + ANSI_RESET + url);
        out.println("\nThis is currently a beta software");
        out.println("All " + ANSI_BLUE + "feedback" + ANSI_RESET + " is greatly appreciated!");

        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (UnsatisfiedLinkError | HeadlessException | URISyntaxException | IOException e) {
            /* ignore */
        }
    }

    public static void clrscr() {
        out.print("\033[H\033[2J");
        out.flush();
    }

    static class ErrorHandler extends ErrorPageErrorHandler {
        @Override
        public void handle(String target, org.eclipse.jetty.server.Request
            baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {

            try {
                if(response.getStatus() == HttpServletResponse.SC_BAD_GATEWAY) {
                    server.removeConnector(connector);
                    connector.stop();
                }
            } catch(Exception ex) {
                /* ignore */
            }
            return;
        }
    }

    public static boolean hostAvailabilityCheck(String address, int port) {
        try (Socket s = new Socket(address, port)) {
            return true;
        } catch (IOException ex) {
            /* ignore */
        }
        return false;
    }
}
