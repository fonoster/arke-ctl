package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import java.nio.file.NoSuchFileException;
import java.io.IOException;
import static java.lang.System.out;

class CmdConfigDescribe {

    private static CtlUtils ctlUtils;

    CmdConfigDescribe(CtlUtils ctlUtils) {
        this.ctlUtils = ctlUtils;
    }

    void run() {
        Gson gson = new Gson();
        HttpResponse response = this.ctlUtils.getWithToken("system/config", null);
        JsonObject jObject = gson.fromJson(response.getBody().toString(), JsonObject.class);
        String j = gson.toJson(jObject.get("data")).toString();

        try {
            out.println(new FormatUtil().asYaml(j));
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
