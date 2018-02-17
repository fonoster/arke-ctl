package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.util.Iterator;

import static java.lang.System.out;

public class CmdSystem {

    private static CtlUtils ctlUtils;

    public CmdSystem(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser sys = subparsers.addParser("system").aliases("sys").help("display a list of resources");
        sys.addArgument("subcommand").metavar("subcommand").choices("status", "info", "stop").help("system actions");

        sys.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Shows system information like version and apiPath",
            "  $ sipioctl -- system info\n",
            "  # Shows the current system status",
            "  $ sipioctl -- sys status\n",
            "  # Stops the system",
            "  $ sipioctl -- sys halt"
        ));

        this.ctlUtils = ctlUtils;
    }

    void run(String subCommand) {
        if (subCommand.equals("stop")) {
            ctlUtils.postWithToken("system/status/down", null);
            // Server will not respond, so do nothing :P
            out.println("Done.");
        } else if (subCommand.equals("status")) {
            HttpResponse result = ctlUtils.getWithToken("system/status", null);
            Gson gson = new Gson();
            String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("status").getAsString();
            out.println(message);
        } else if (subCommand.equals("info")) {
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
            while(i.hasNext()) {
                JsonElement jo = i.next();
                String var = jo.getAsJsonObject().get("var").getAsString();
                String value = "not set";
                if(!jo.getAsJsonObject().get("value").isJsonNull()) {
                    value = jo.getAsJsonObject().get("value").getAsString();
                }
                out.println(var + "=" + value);
            }
        }

    }
}
