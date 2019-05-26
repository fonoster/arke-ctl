package com.fonoster.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.Iterator;

import static java.lang.System.out;

class CmdSystem {

    private static CtlUtils ctlUtils;

    CmdSystem(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser sys = subparsers.addParser("system").aliases("sys").help("shows system information");
        sys.addArgument("subcommand").metavar("subcommand").choices("status", "info", "stop", "reload").help("system actions");

        sys.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Shows system information like version and apiPath",
            "  $ rctl system info\n",
            "  # Shows the current system status",
            "  $ rctl sys status\n",
            "  # Stops the system",
            "  $ rctl sys stop",
            "  # Reloads the system configuration from file",
            "  $ rctl sys reload"
        ));

        CmdSystem.ctlUtils = ctlUtils;
    }

    void run(String subCommand) {
        switch (subCommand) {
            case "stop":
                ctlUtils.postWithToken("system/status/down", null);
                // Server will not respond, so do nothing :P
                out.println("Done.");
                break;
            case "reload":
                ctlUtils.postWithToken("system/status/reload", null);
                out.println("Reloaded configuration from file.");
                break;
            case "status": {
                HttpResponse result = ctlUtils.getWithToken("system/status", null);
                Gson gson = new Gson();
                String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("status").getAsString();
                out.println(message);
                break;
            }
            case "info": {
                HttpResponse result = ctlUtils.getWithToken("system/info", null);
                Gson gson = new Gson();
                JsonElement obj = gson.fromJson(result.getBody().toString(), JsonElement.class).getAsJsonObject();

                out.println("[System info]");
                out.println("Server version: " + obj.getAsJsonObject().get("version").getAsString());
                out.println("API version: " + obj.getAsJsonObject().get("apiVersion").getAsString());
                out.println("API path: " + obj.getAsJsonObject().get("apiPath").getAsString());

                JsonArray ja = obj.getAsJsonObject().getAsJsonArray("env");
                Iterator<JsonElement> i = ja.iterator();

                out.println("[Env]");
                while (i.hasNext()) {
                    JsonElement jo = i.next();
                    String var = jo.getAsJsonObject().get("var").getAsString();
                    String value = "not set";
                    if (!jo.getAsJsonObject().get("value").isJsonNull()) {
                        value = jo.getAsJsonObject().get("value").getAsString();
                    }
                    out.println(var + "=" + value);
                }
                break;
            }
        }

    }
}
