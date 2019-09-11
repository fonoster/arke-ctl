package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.nio.file.NoSuchFileException;
import java.util.Iterator;

import static java.lang.System.out;

class CmdApply {

    private static CtlUtils ctlUtils;
    private Gson gson;

    CmdApply(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser apply = subparsers.addParser("apply").help("apply changes over existing resource(s)");
        apply.addArgument("-f").metavar("FILE").help("path to yaml file with a resources(s)");

        apply.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Apply changes to an existing agent",
            "  $ rctl apply -f agent.yaml\n",
            "  # Updates a set of gateways",
            "  $ rctl apply -f gws.yaml"
        ));

        CmdApply.ctlUtils = ctlUtils;
        this.gson = new Gson();
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


        JsonElement je = gson.fromJson(data, JsonElement.class);

        if(je.isJsonArray()) {
            Iterator i = je.getAsJsonArray().iterator();

            while(i.hasNext()) {
                apply((JsonElement) i.next());
            }
        } else {
            apply(je);
        }
    }

    private void apply(JsonElement je) {
        String kind = je.getAsJsonObject().get("kind").getAsString();
        String ref = getRef(je.getAsJsonObject());
        kind = kind.toLowerCase() + "s";

        if (ref == null) {
            out.println("No reference found for this resource. Use 'create' command");
            return;
        }

        HttpResponse result = ctlUtils.putWithToken(kind + "/" + ref, gson.toJson(je));
        String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
        out.println(message);
    }

    // Returns ref base on Object `kind`
    private String getRef(JsonObject object) {
        String kind = object.get("kind").getAsString();

        if (kind.equalsIgnoreCase("agent")) {
            String username = object
                .get("spec").getAsJsonObject()
                    .get("credentials").getAsJsonObject()
                        .get("username").getAsString();
            String domains = object
                .get("spec").getAsJsonObject()
                    .get("domains").getAsJsonArray().toString();

            String response = ctlUtils.getWithToken("agents",
                "filter=@.spec.credentials.username=='" + username + "'")
                    .getBody().toString();

            JsonObject res = gson.fromJson(response, JsonObject.class);
            JsonArray agents = res.getAsJsonArray("result");

            Iterator i = agents.iterator();

            while(i.hasNext()) {
                JsonElement je = (JsonElement) i.next();
                String udomains = je.getAsJsonObject()
                    .get("spec").getAsJsonObject()
                        .get("domains").getAsJsonArray().toString();

                if (domains.equals(udomains)) {
                    return je.getAsJsonObject()
                        .get("metadata").getAsJsonObject()
                            .get("ref").getAsString();
                }
            }

            return null;
        } else if (kind.equalsIgnoreCase("domain")) {
            String domainUri = object
                .get("spec").getAsJsonObject()
                    .get("context").getAsJsonObject()
                        .get("domainUri").getAsString();

            String response = ctlUtils.getWithToken("domains",
                "filter=@.spec.context.domainUri=='" + domainUri + "'")
                    .getBody().toString();

            JsonObject res = gson.fromJson(response, JsonObject.class);
            JsonArray domain = res.getAsJsonArray("result");

            if (domain.iterator().hasNext()) {
                return  domain.iterator()
                    .next().getAsJsonObject()
                        .get("metadata").getAsJsonObject()
                            .get("ref").getAsString();
            }
        } else if (kind.equalsIgnoreCase("peer")) {
            String username = object
                .get("spec").getAsJsonObject()
                    .get("credentials").getAsJsonObject()
                        .get("username").getAsString();

            String response = ctlUtils.getWithToken("peers",
                "filter=@.spec.credentials.username=='" + username + "'")
                    .getBody().toString();

            JsonObject res = gson.fromJson(response, JsonObject.class);
            JsonArray peers = res.getAsJsonArray("result");

            if (peers.iterator().hasNext()) {
                return  peers.iterator()
                    .next().getAsJsonObject()
                        .get("metadata").getAsJsonObject()
                            .get("ref").getAsString();
            }
        } else if (kind.equalsIgnoreCase("gateway")) {
            String host = object
                .get("spec").getAsJsonObject()
                    .get("regService").getAsJsonObject()
                        .get("host").getAsString();

            String response = ctlUtils.getWithToken("gateways",
                    "filter=@.spec.regService.host=='" + host + "'")
                    .getBody().toString();

            JsonObject res = gson.fromJson(response, JsonObject.class);
            JsonArray gateways = res.getAsJsonArray("result");

            if (gateways.iterator().hasNext()) {
                return  gateways.iterator()
                    .next().getAsJsonObject()
                        .get("metadata").getAsJsonObject()
                            .get("ref").getAsString();
            }
        } else if (kind.equalsIgnoreCase("number")) {
            String telUrl = object
                .get("spec").getAsJsonObject()
                    .get("location").getAsJsonObject()
                        .get("telUrl").getAsString();

            String response = ctlUtils.getWithToken("numbers",
                "filter=@.spec.location.telUrl=='" + telUrl + "'")
                    .getBody().toString();

            JsonObject res = gson.fromJson(response, JsonObject.class);
            JsonArray numbers = res.getAsJsonArray("result");

            if (numbers.iterator().hasNext()) {
                return  numbers.iterator()
                    .next().getAsJsonObject()
                        .get("metadata").getAsJsonObject()
                            .get("ref").getAsString();
            }
        }

        return null;
    }
}
