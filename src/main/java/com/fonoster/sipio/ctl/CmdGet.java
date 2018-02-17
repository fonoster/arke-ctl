package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.inf.Subparser;

public class CmdGet {

    public CmdGet(Subparsers subparsers) {
        Subparser get = subparsers.addParser("get").help("display a list of resources");
        get.addArgument("resource").metavar("resource").choices("agent", "agents", "peer", "peers",
                "domain", "domains", "did", "dids", "gateway", "gateways").help("the resource to be listed");
        get.addArgument("REF").nargs("?").setDefault("").help("reference to resource");
        get.addArgument("--filter").setDefault("*").help("apply filter base on resource metadata");
        get.epilog(String.join(
            System.getProperty("line.separator"),
            "Examples:",
            "  # Shows all the agents in the system",
            "  $ sipioctl -- get agents\n",
            "  # List a single agent by ref",
            "  $ sipioctl -- get agent ag3f77f6\n",
            "  # Gets did using its reference",
            "  $ sipioctl -- get dids --filter \"@.metadata.ref=='dd50baa4'"
        ));
    }

    void run(String resource, String ref, String filter) throws UnirestException {
        if (resource.equals("agent") || resource.equals("agents")) CmdGetAgents.printAgents(ref, filter);
        if (resource.equals("peer") || resource.equals("peers")) CmdGetPeers.printPeers(ref, filter);
        if (resource.equals("gateway") || resource.equals("gateways")) CmdGetGateways.printGateways(ref, filter);
        if (resource.equals("did") || resource.equals("dids")) CmdGetDIDs.printDIDs(ref, filter);
        if (resource.equals("domain") || resource.equals("domains")) CmdGetDomains.printDomains(ref, filter);
    }
}
