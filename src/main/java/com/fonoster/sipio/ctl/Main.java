package com.fonoster.sipio.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.File;
import java.io.IOException;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {
    public final static String CONFIG_PATH = System.getProperty("user.home") + "/.sipio-access.json";
    public final static String INVALID_ACCESS_TOKEN = "Unable to find a valid access token. Please login";

    static public void main(String... args) throws UnirestException, IOException {
        String accessToken = null;
        String apiUrl = null;
        CtlUtils ctlUtils = null;

        ArgumentParser parser = ArgumentParsers
            .newFor("sipioctl")
            .build()
            .defaultHelp(true)
            .description("sipioctl controls the Sip I/O server")
            .epilog("More information at https://github.com/fonoster/sipio/wiki");

        Subparsers subparsers = parser.addSubparsers().title("Basic Commands").metavar("COMMAND");

        if (!args[0].equals("login")) {
            if (!new File(CONFIG_PATH).exists()) {
                out.println(INVALID_ACCESS_TOKEN);
                exit(1);
            } else {
                Gson gson = new Gson();
                String serverInfo = new FileUtils().readFile(CONFIG_PATH);
                JsonObject jo = gson.fromJson(serverInfo, JsonObject.class);
                apiUrl = jo.get("apiUrl").getAsString();
                accessToken = jo.get("token").getAsString();
            }

            ctlUtils = new CtlUtils(apiUrl, accessToken);
        }

        CmdGet cmdGet = new CmdGet(subparsers, ctlUtils);
        CmdCreate cmdCreate = new CmdCreate(subparsers, ctlUtils);
        CmdApply cmdApply = new CmdApply(subparsers, ctlUtils);
        CmdDelete cmdDelete = new CmdDelete(subparsers, ctlUtils);
        CmdLocate cmdLocate = new CmdLocate(subparsers, ctlUtils);
        CmdRegistry cmdRegistry = new CmdRegistry(subparsers, ctlUtils);
        CmdSystem cmdSystem = new CmdSystem(subparsers, ctlUtils);
        CmdLogin cmdLogin = new CmdLogin(subparsers);

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
                case "system":
                case "sys":
                    cmdSystem.run(res.get("subcommand"));
                    break;
                case "login":
                    cmdLogin.run(res.get("apiUrl"), res.get("u"), res.get("p"));
                    break;
                default:
                    // This is not possible;
            }
        } catch(ArgumentParserException ex) {
            parser.handleError(ex);
        } catch (UnirestException ex) {
            System.out.println("Sip I/O server is not running");
            System.exit(0);
        }
    }
}
