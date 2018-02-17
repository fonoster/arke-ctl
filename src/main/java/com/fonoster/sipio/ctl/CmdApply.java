package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.nio.file.NoSuchFileException;
import java.util.Iterator;

import static java.lang.System.out;

public class CmdApply {

    public CmdApply(Subparsers subparsers) {
        Subparser apply = subparsers.addParser("apply").help("apply changes over existing resource(s)");
        apply.addArgument("-f").metavar("FILE").help("path to yaml file with a resources(s)");

        apply.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Apply changes to an existing agent",
            "  $ sipioctl -- apply -f agent.yaml\n",
            "  # Updates a set of gateways",
            "  $ sipioctl -- apply -f gws.yaml"
        ));
    }

    void run(String path) throws UnirestException {
        CtlUtils ctlUtils = new CtlUtils();

        String data = "";

        if (path.isEmpty()) {
            out.print("You must indicate the path to the resource");
            System.exit(1);
        }

        try {
            data = new FileUtils().getJsonString(path);
        } catch(Exception ex) {
            if (ex instanceof NoSuchFileException) {
                out.print("Please ensure file '" + ex.getMessage() + "' exist and has proper permissions");
            } else if (ex instanceof NullPointerException) {
                out.print("You must indicate a file :(");
            } else {
                out.print("Unexpected Exception: " + ex.getMessage());
            }
            System.exit(1);
        }

        Gson gson = new Gson();
        JsonElement jo = gson.fromJson(data, JsonElement.class);

        if(jo.isJsonArray()) {
            Iterator i = jo.getAsJsonArray().iterator();

            // Fixme: Put verb uses a different endpoint...
            while(i.hasNext()) {
                JsonElement je = ((JsonElement) i.next());
                JsonObject resource = je.getAsJsonObject();
                JsonObject metadata = resource.get("metadata").getAsJsonObject();
                String ref = metadata.get("ref").getAsString();
                String kind = resource.get("kind").getAsString();
                kind = kind.toLowerCase() + "s";
                HttpResponse result = ctlUtils.putWithToken(kind + "/" + ref, data);
                String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
                out.println(message);
            }
        } else {
            String kind = jo.getAsJsonObject().get("kind").getAsString();
            JsonObject metadata = jo.getAsJsonObject().get("metadata").getAsJsonObject();
            String ref = metadata.get("ref").getAsString();
            kind = kind.toLowerCase() + "s";
            HttpResponse result = ctlUtils.putWithToken(kind + "/" + ref, data);
            String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
            out.print(message);
        }
    }
}
