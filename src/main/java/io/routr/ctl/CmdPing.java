package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparsers;
import static java.lang.System.out;

class CmdPing {

    private static CtlUtils ctlUtils;

    CmdPing(Subparsers subparsers, CtlUtils ctlUtils) {
        subparsers.addParser("ping").help("checks engine status");
        this.ctlUtils = ctlUtils;
    }

    void run() {
      HttpResponse response = this.ctlUtils.getWithToken("system/status", null);
      Gson gson = new Gson();
      String message = gson.fromJson(response.getBody().toString(), JsonObject.class).get("data").getAsString();
      out.println(message);
    }
}
