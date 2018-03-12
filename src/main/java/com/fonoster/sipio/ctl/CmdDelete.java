package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.HttpResponse;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import static java.lang.System.out;


class CmdDelete {

    private static CtlUtils ctlUtils;

    CmdDelete(Subparsers subparsers, CtlUtils ctlUtils) {
        String[] delSubCmds = new String[]{"agent", "agents", "peer", "peers", "domain", "domains", "did", "dids", "gateway", "gateways"};
        Subparser del = subparsers.addParser("delete").aliases("del").help("delete an existing resource(s)");
        del.addArgument("resource").metavar("resource").choices(delSubCmds).help("type of resource (ie.: agent, domain, etc)");
        del.addArgument("REF").nargs("?").help("reference to resource");
        del.addArgument("--filter").setDefault("").help("apply filter base on resource(s) metadata ");

        del.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Deletes resource type Agent using its reference",
            "  $ sipioctl delete agent ag2g4s34\n",
            "  # or use \"del\" alias\n",
            "  # Deletes resource type DIDs using the its parent Gateway reference",
            "  $ sipioctl del did --filter \"@.metadata.gwRef=\"gweef506\""
        ));

        CmdDelete.ctlUtils = ctlUtils;
    }

    void run(String resource, String ref, String filter) {
        if (ref.isEmpty() && filter.isEmpty()) {
            out.print("You must indicate a 'REF' or --filter");
            System.exit(1);
        }

        if(!resource.endsWith("s")) resource = resource + "s";

        HttpResponse result = ctlUtils.deleteWithToken(resource + '/' + ref, "filter=" + filter);

        Gson gson = new Gson();
        String message = gson.fromJson(result.getBody().toString(), JsonObject.class).get("message").getAsString();
        out.println(message);
    }
}
