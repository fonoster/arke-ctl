package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparsers;
import java.util.Iterator;
import static java.lang.System.out;

class CmdVersion {

    private static CtlUtils ctlUtils;

    CmdVersion(Subparsers subparsers, CtlUtils ctlUtils) {
        subparsers.addParser("version").aliases("ver").help("obtain rctl's version information");
        this.ctlUtils = ctlUtils;
    }

    void run() {
        HttpResponse response = this.ctlUtils.getWithToken("system/info", null);
        Gson gson = new Gson();
        JsonElement el = gson.fromJson(response.getBody().toString(), JsonElement.class).getAsJsonObject();
        JsonElement obj = el.getAsJsonObject().get("data");

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
    }
}
