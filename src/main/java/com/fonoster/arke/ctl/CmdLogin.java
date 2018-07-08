package com.fonoster.routr.ctl;

import com.google.gson.JsonObject;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.IOException;

import static java.lang.System.exit;
import static java.lang.System.out;

class CmdLogin {

    CmdLogin(Subparsers subparsers) {
        Subparser login = subparsers.addParser("login").help("sets connection info");
        login.addArgument("apiUrl").metavar("apiUrl").help("api url");
        login.addArgument("-u").metavar("USERNAME").help("server's username");
        login.addArgument("-p").metavar("PASSWORD").help("server's password");
    }

    void run(String apiUrl, String username, String password)  {
        String token = new CtlUtils(apiUrl, username, password).getToken();

        JsonObject jo = new JsonObject();
        jo.addProperty("apiUrl", apiUrl);
        jo.addProperty("token", token);

        try {
            new FileUtils().writeFile(Main.CONFIG_PATH, jo.toString());
        } catch (IOException e) {
            e.printStackTrace();
            exit(1);
        }

        out.println("Done");
    }
}
