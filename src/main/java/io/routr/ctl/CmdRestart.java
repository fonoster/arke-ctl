package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.impl.Arguments;
import static java.lang.System.out;

class CmdRestart {

    private static CtlUtils ctlUtils;

    CmdRestart(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser sys = subparsers.addParser("restart").help("restarts the engine");
        sys.addArgument("--now").dest("now").action(Arguments.storeTrue()).setDefault(false).help("restart server immediately");
        this.ctlUtils = ctlUtils;
    }

    void run(boolean nowFlag) {
      this.ctlUtils.postWithToken("system/status/restarting", null, "now=" + nowFlag);
      out.println("Done");
    }
}
