package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
                JsonObject resource = je.getAsJsonObject();
                String kind = resource.get("kind").getAsString();
                kind = kind.toLowerCase() + "s";
                HttpResponse result = ctlUtils.postWithToken(kind, data);
                String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
                out.println(message);
            }
        } else {
            String kind = jo.getAsJsonObject().get("kind").getAsString();
            kind = kind.toLowerCase() + "s";
            HttpResponse result = ctlUtils.postWithToken(kind, data);
            String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
            out.print(message);
        }
    }
}
