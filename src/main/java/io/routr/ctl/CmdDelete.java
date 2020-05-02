package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import static java.lang.System.out;


class CmdDelete {

    private static CtlUtils ctlUtils;

    CmdDelete(Subparsers subparsers, CtlUtils ctlUtils) {
        String[] delSubCmds = new String[]{"agent", "agents", "peer", "peers", "domain", "domains", "number", "numbers", "gateway", "gateways"};
        Subparser del = subparsers.addParser("delete").aliases("del").help("delete an existing resource(s)");
        del.addArgument("resource").metavar("type").choices(delSubCmds).help("type of resource (ie.: agent, domain, etc)");
        del.addArgument("reference").nargs("?").help("reference to resource");
        del.addArgument("--filter").metavar("filter").setDefault("").help("apply filter base on resource(s) metadata ");

        del.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Deletes resource type Agent using its reference",
            "  $ rctl delete agent ag2g4s34\n",
            "  # or use the \"del\" alias\n",
            "  # Deletes resource type Numbers using the its parent Gateway reference",
            "  $ rctl del number --filter \"@.metadata.gwRef=\"gweef506\""
        ));

        CmdDelete.ctlUtils = ctlUtils;
    }

    void run(String resource, String ref, String filter) {
        if (ref.isEmpty() && filter.isEmpty()) {
            out.println("You must indicate a 'REF' or --filter");
            System.exit(1);
        }

        if(!resource.endsWith("s")) resource = resource + "s";

        HttpResponse response = ctlUtils.deleteWithToken(resource + '/' + ref, "filter=" + filter);

        Gson gson = new Gson();
        String message = gson.fromJson(response.getBody().toString(), JsonObject.class).get("message").getAsString();
        out.println(message);
    }
}
