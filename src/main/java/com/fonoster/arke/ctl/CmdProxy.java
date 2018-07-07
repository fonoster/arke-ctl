package com.fonoster.arke.ctl;

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

import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.server.Server;
import java.net.URI;
import java.net.URL;
import java.awt.Desktop;
import java.io.IOException;
import java.lang.InterruptedException;

import static java.lang.System.out;

class CmdProxy {

    private static String ANSI_BOLD = "\u001B[1m";
    private static String ANSI_BLUE = "\u001B[36m";
    private static String ANSI_GREEN = "\u001B[32m";
    private static String ANSI_RESET = "\u001B[0m";

    CmdProxy(Subparsers subparsers) {
        // Temporary solution to disabl those anoying log messages
        java.lang.System.setProperty("org.eclipse.jetty.LEVEL", "ERROR");
        org.apache.log4j.BasicConfigurator.configure(new org.apache.log4j.varia.NullAppender());
        Subparser proxy = subparsers.addParser("proxy").help("run a proxy to the server");
        proxy.addArgument("-p", "--port").type(Integer.class).setDefault(8000).help("The port on which to run the proxy");
    }

    void run(String apiUrl, String token, int port) throws Exception {
        // Create Server
    		Server server = new Server(port);

        URL proxyToURL = new URI(apiUrl).toURL();

        String proxyTo = proxyToURL.getProtocol() + "://" + proxyToURL.getHost();

        if (proxyToURL.getPort() != -1) {
            proxyTo = proxyTo + ":" + proxyToURL.getPort();
        }

        // Setting Proxy Servlet
        ServletContextHandler sch = new ServletContextHandler(ServletContextHandler.SESSIONS);
        ServletHolder proxyServlet = new ServletHolder(com.fonoster.arke.proxy.APIProxy.class);
        proxyServlet.setInitParameter("apiKeyName", "token");
        proxyServlet.setInitParameter("apiKeyValue", token);
        proxyServlet.setInitParameter("proxyTo", proxyTo);

        // Setting Resources Servlet
        ServletHolder defServlet = new ServletHolder(DefaultServlet.class);
        defServlet.setInitParameter("resourceBase", "./libs/webapp");
        defServlet.setInitParameter("dirAllowed", "true");

        sch.addServlet(proxyServlet, "/api/*");
        sch.addServlet(defServlet, "/*");
        server.setHandler(sch);

    		// Start the server
    		server.start();
        openBrowser(port);
    		server.join();
    }

    void openBrowser(int port) {
        CmdProxy.clrscr();

        String url = "http://localhost:" + port;

        out.println(ANSI_GREEN + "Serving Arke Console" + ANSI_RESET);
        out.println("\nYou can now view the " + ANSI_BOLD + "console" + ANSI_RESET + " in the browser.\n");
        out.println(ANSI_BOLD + "  URL\t" + ANSI_RESET + url);
        out.println("\nKeep in mind that this software is not production ready.");
        out.println("All " + ANSI_BLUE + "feedback" + ANSI_RESET + " is greatly appreciated!");

        try {
            Desktop desktop = java.awt.Desktop.getDesktop();
            URI oURL = new URI(url);
            desktop.browse(oURL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void clrscr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}
