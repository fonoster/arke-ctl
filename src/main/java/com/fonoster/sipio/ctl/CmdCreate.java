package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.nio.file.NoSuchFileException;
import java.util.Iterator;

import static java.lang.System.out;

public class CmdCreate {

    private static CtlUtils ctlUtils;

    public CmdCreate(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser create = subparsers.addParser("create").aliases("crea").help("creates new resource(s)");
        create.addArgument("-f").metavar("FILE").help("path to yaml file with a resources(s)");

        create.epilog(String.join(
            System.getProperty("line.separator"),
            "`Examples:",
            "  # Creates a new agent from a yaml file",
            "  $ sipioctl -- crea -f agent.yaml\n\n",
            "  # Creates a set of gateways from a yaml file\n",
            "  $ sipioctl -- create -f gws.yaml\n"
        ));

        this.ctlUtils = ctlUtils;
    }

    void run(String path) {
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

            while(i.hasNext()) {
                JsonElement je = ((JsonElement) i.next());
                JsonObject jObj = je.getAsJsonObject();
                final String kind = jObj.get("kind").getAsString();
                HttpResponse result = ctlUtils.postWithToken(kind.toLowerCase() + "s", jObj.toString());

                if(result.getStatus() == 200) {
                    out.println(kind.toLowerCase() + " \"" + getName(jObj) + "\" created");
                } else {
                    String message = gson.fromJson(result.getBody().toString(), JsonPrimitive.class).getAsString();
                    out.println(message);
                }
            }
        } else {
            final String kind = jo.getAsJsonObject().get("kind").getAsString();
            HttpResponse result = ctlUtils.postWithToken(kind.toLowerCase() + "s", data);

            if(result.getStatus() == 200) {
                JsonObject jObj = gson.fromJson(result.getBody().toString(), JsonObject.class);
                out.println(kind.toLowerCase() + " \"" + getName(jObj) + "\" created");
            } else {
                String message = gson.fromJson(result.getBody().toString(), JsonPrimitive.class).getAsString();
                out.println(message);
            }
        }
    }

    private String getName (JsonObject obj) {
        final String kind = obj.get("kind").getAsString();

        if (kind.equalsIgnoreCase("User")
                || kind.equalsIgnoreCase("Agent")
                || kind.equalsIgnoreCase("Gateway")
                || kind.equalsIgnoreCase("Peer")
                || kind.equalsIgnoreCase("User")) {

            JsonObject metadata = obj.getAsJsonObject("metadata");

            return metadata.get("name").getAsString();
        } else if (kind.equalsIgnoreCase("DID")) {
            JsonObject spec = obj.getAsJsonObject("spec");
            JsonObject credentials = spec.getAsJsonObject("location");
            String result = credentials.get("telUrl").getAsString().replaceAll("tel:", "");
            return result;
        }

        return "";
    }
}
