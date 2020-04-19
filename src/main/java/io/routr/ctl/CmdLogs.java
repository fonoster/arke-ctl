package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparsers;
import static java.lang.System.out;

class CmdLogs {

    private static CtlUtils ctlUtils;

    CmdLogs(Subparsers subparsers, CtlUtils ctlUtils) {
        subparsers.addParser("logs").help("dumps all the available system logs");
        this.ctlUtils = ctlUtils;
    }

    void run() {
        HttpResponse response = this.ctlUtils.getWithToken("system/logs", null);
        Gson gson = new Gson();
        JsonElement el = gson.fromJson(response.getBody().toString(), JsonElement.class).getAsJsonObject();
        JsonElement obj = el.getAsJsonObject().get("data");

        out.println("[System logs]");
        out.println(obj.getAsString());
    }
}
