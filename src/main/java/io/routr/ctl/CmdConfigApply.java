package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.nio.file.NoSuchFileException;

import static java.lang.System.out;

class CmdConfigApply {

    private static CtlUtils ctlUtils;

    CmdConfigApply(CtlUtils ctlUtils) {
        this.ctlUtils = ctlUtils;
    }

    void run(String data) {
        Gson gson = new Gson();
        create(gson.fromJson(data, JsonElement.class));
    }

    private void create(JsonElement je) {
        Gson gson = new Gson();
        HttpResponse response = this.ctlUtils.putWithToken("system/config", gson.toJson(je));
        JsonObject jObject = gson.fromJson(response.getBody().toString(), JsonObject.class);
        String message = jObject.get("message").getAsString();
        out.println(message);
    }
}
