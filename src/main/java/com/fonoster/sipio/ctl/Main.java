package com.fonoster.sipio.ctl;

import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

public class Main {

    static public void main(String... args) throws UnirestException {
        String accessToken = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiJ9.75_0_jp8__mLr2FK5Q-m2ph4euWA_zl3G_q01SdCo0Drg-_Dya3_OLTRGbRImnG5P-TfAgboqf5y3qGu1l39BA";

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
        CmdStop cmdStop = new CmdStop(subparsers);
        CmdConfig cmdConfig = new CmdConfig(subparsers);

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
                    cmdDelete.run(res.get("resource"), res.get("REF"), res.get("filter"));
                    break;
                case "get":
                    cmdGet.run(res.get("resource"), res.get("REF"), res.get("filter"));
                    break;
                case "stop":
                    cmdStop.run();
                    break;
                case "configure":
                    cmdConfig.run();
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
