package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Main {

    static public void main(String... args) throws UnirestException {
        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.S7c6sLg7MvF2iPN4XksXQ4O6TIl-ln8Y8fqXPWgEj8pGQqHNFFXHOec-RqBUGiXKuWXlf_TSlZVfo2xy7AUhLg";

        ArgumentParser parser = ArgumentParsers
            .newFor("sipioctl")
            .build()
            .defaultHelp(true)
            .description("sipioctl controls the Sip I/O server")
            .epilog("More information at https://github.com/fonoster/sipio/wiki");

        Subparsers subparsers = parser.addSubparsers().title("Basic Commands").metavar("COMMAND");

        CmdGet cmdGet = new CmdGet(subparsers);
        CmdCreate cmdCreate = new CmdCreate(subparsers);
        CmdApply cmdApply = new CmdApply(subparsers);
        CmdDelete cmdDelete = new CmdDelete(subparsers);
        CmdLocate cmdLocate = new CmdLocate(subparsers);
        CmdRegistry cmdRegistry = new CmdRegistry(subparsers);

        try {
            // Variable 'args' is a global coming from the entry point script
            Namespace res = parser.parseArgs(args);

            switch (args[0]) {
                case "locate":
                case "loc":
                    cmdLocate.run();
                    break;
                case "registry":
                case "reg":
                    cmdRegistry.run();
                    break;
                case "create":
                case "crea":
                    cmdCreate.run(res.get("f"));
                    break;
                case "apply":
                    cmdApply.run(res.get("f"));
                    break;
                case "delete":
                case "del":
                    cmdDelete.run(res.get("f"));
                    break;
                case "get":
                    cmdGet.run(res.get("resource"), res.get("REF"), res.get("filter"));
                    break;
                default:
                    //throw "This is not possible";
            }
        } catch(ArgumentParserException ex) {
            parser.handleError(ex);
        } catch (UnirestException ex) {
            System.out.println("Sip I/O server is not running");
            System.exit(0);
        }
    }
}
