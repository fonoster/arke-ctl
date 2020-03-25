package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.impl.Arguments;

import java.util.Iterator;

import static java.lang.System.out;

class CmdSystem {

    private static CtlUtils ctlUtils;

    CmdSystem(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser sys = subparsers.addParser("system").aliases("sys").help("shows system information");
        sys.addArgument("subcommand").metavar("subcommand").choices("status", "info", "stop", "restart").help("system actions");
        sys.addArgument("--now").dest("now").action(Arguments.storeTrue()).setDefault(false).help("use with `stop` to shutdown the server immediately");

        sys.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Shows system information like version and apiPath",
            "  $ rctl system info\n",
            "  # Shows the current system status",
            "  $ rctl sys status\n",
            "  # Stops the system",
            "  $ rctl sys stop --now",
            "  $ rctl sys restart"
        ));

        CmdSystem.ctlUtils = ctlUtils;
    }

    void run(String subCommand, boolean nowFlag) {
        switch (subCommand) {
            case "stop":
                if (nowFlag) {
                    ctlUtils.postWithToken("system/status/down", null, "now=true");
                } else {
                    ctlUtils.postWithToken("system/status/down", null, null);
                }
                out.println("Done");
                break;
            case "restart":
                if (nowFlag) {
                    ctlUtils.postWithToken("system/status/restarting", null, "now=true");
                } else {
                    ctlUtils.postWithToken("system/status/restarting", null, null);
                }
                out.println("Done");
                break;
            case "status": {
                HttpResponse response = ctlUtils.getWithToken("system/status", null);
                Gson gson = new Gson();
                String message = gson.fromJson(response.getBody().toString(), JsonObject.class).get("status").getAsString();
                out.println(message);
                break;
            }
            case "info": {
                HttpResponse response = ctlUtils.getWithToken("system/info", null);
                Gson gson = new Gson();
                JsonElement obj = gson.fromJson(response.getBody().toString(), JsonElement.class).getAsJsonObject();

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
