package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.nio.file.NoSuchFileException;

import static java.lang.System.out;

class CmdCreate {

    private static CtlUtils ctlUtils;
    private Gson gson;

    CmdCreate(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser create = subparsers.addParser("create").aliases("crea").help("creates new resource(s)");
        create.addArgument("-f").metavar("FILE").help("path to yaml file with a resources(s)");

        create.epilog(String.join(
            System.getProperty("line.separator"),
            "`Examples:",
            "  # Creates a new agent from a yaml file",
            "  $ rctl crea -f agent.yaml\n\n",
            "  # Creates a set of gateways from a yaml file\n",
            "  $ rctl create -f gws.yaml\n"
        ));

        CmdCreate.ctlUtils = ctlUtils;
        gson = new Gson();
    }

    void run(String path) {
        String data = "";

        if (path.isEmpty()) {
            out.println("You must indicate the path to the resource");
            System.exit(1);
        }

        try {
            data = new FileUtils().getJsonString(path);
        } catch(Exception ex) {
            if (ex instanceof NoSuchFileException) {
                out.println("Please ensure file '" + ex.getMessage() + "' exist and has proper permissions");
            } else if (ex instanceof NullPointerException) {
                out.println("You must indicate a file :(");
            } else {
                out.println("Unexpected Exception: " + ex.getMessage());
            }
            System.exit(1);
        }

        JsonElement je = gson.fromJson(data, JsonElement.class);

        if(je.isJsonArray()) {
            for (JsonElement o : je.getAsJsonArray()) {
                create(o);
            }
        } else {
            create(je);
        }
    }

    private void create(JsonElement je) {
        JsonObject jo = je.getAsJsonObject();
        String collection = jo.getAsJsonObject().get("kind").getAsString().toLowerCase() + "s";
        HttpResponse response = ctlUtils.postWithToken(collection, gson.toJson(je), null);
        JsonObject jObject = gson.fromJson(response.getBody().toString(), JsonObject.class);
        String message = jObject.get("message").getAsString();
        out.println(message);
    }
}
