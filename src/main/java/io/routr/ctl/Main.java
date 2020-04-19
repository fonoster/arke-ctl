package io.routr.ctl;

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
    public final static String CONFIG_PATH = System.getProperty("user.home") + "/.routr-access.json";
    public final static String INVALID_ACCESS_TOKEN = "Unable to find a valid access token. Please login";

    static public void main(String... args) throws Exception {
        String accessToken = null;
        String apiUrl = null;
        CtlUtils ctlUtils = null;

        ArgumentParser parser = ArgumentParsers
            .newFor("rctl")
            .build()
            .defaultHelp(true)
            .description("rctl controls the Routr server")
            .epilog("More information at https://routr.io");

        Subparsers subparsers = parser.addSubparsers().title("Basic Commands").metavar("COMMAND");

        if (args.length > 0 &&
            !args[0].equals("-h") &&
            !args[0].equals("--help") &&
            !args[0].equals("login") &&
            !args[0].equals("logout")) {

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
        CmdProxy cmdProxy = new CmdProxy(subparsers);
        CmdLogin cmdLogin = new CmdLogin(subparsers);
        CmdLogout cmdLogout = new CmdLogout(subparsers);

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
                    cmdSystem.run(res.get("subcommand"), res.get("now"));
                    break;
                case "login":
                    cmdLogin.run(res.get("apiUrl"), res.get("u"), res.get("p"));
                    break;
                case "logout":
                    cmdLogout.run();
                    break;
                case "proxy":
                    cmdProxy.run(apiUrl, accessToken, res.get("port"));
                    break;
                default:
                    // This is not possible;
            }
        } catch(ArgumentParserException ex) {
            parser.handleError(ex);
        } catch (UnirestException ex) {
            System.out.println("Routr server is not running");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
