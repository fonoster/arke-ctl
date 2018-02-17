package com.fonoster.sipio.ctl;

import com.google.gson.JsonObject;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.IOException;

import static java.lang.System.out;

public class CmdLogin {

    private static CtlUtils ctlUtils;

    public CmdLogin(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser login = subparsers.addParser("login").help("sets connection info");
        login.addArgument("apiUrl").metavar("apiUrl").help("api url");
        login.addArgument("-u").metavar("USERNAME").help("server's username");
        login.addArgument("-p").metavar("PASSWORD").help("server's password");

        this.ctlUtils = ctlUtils;
    }

    void run(String apiUrl, String username, String password) throws IOException {
        String token = new CtlUtils(apiUrl, username, password).getToken();

        JsonObject jo = new JsonObject();
        jo.addProperty("apiUrl", apiUrl);
        jo.addProperty("token", token);

        new FileUtils().writeFile(Main.CONFIG_PATH, jo.toString());
        out.println("Done");
    }
}
