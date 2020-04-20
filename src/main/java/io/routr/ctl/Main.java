package io.routr.ctl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;
import net.sourceforge.argparse4j.impl.Arguments;
import java.util.Arrays;

import java.io.File;
import java.io.IOException;

import static java.lang.System.exit;
import static java.lang.System.out;

public class Main {
    public final static String CONFIG_PATH = System.getProperty("user.home") + "/.routr-access.json";
    public final static String INVALID_ACCESS_TOKEN = "Unable to find a valid access token. Please login";
    private final static String[] REGISTERED_COMMAND = {
      "--help", "-h",  "--version", "-v", "login", "logout", "get", "create",
      "crea", "delete", "del", "locate", "loc", "registry", "reg", "proxy",
      "logs", "restart", "stop", "ping", "version", "vers", "config" };

    static private boolean bypassToken(String... args) {
        String[] commands = {"-h", "--help", "--version", "-v", "login", "logout"};

        if (!Arrays.asList(REGISTERED_COMMAND).contains(args[0])) return true;

        for (String c : commands) {
            // If not a registered command pass control to parser
            if (Arrays.asList(args).contains(c)) return true;
        }
        return false;
    }

    static public void main(String... args) throws Exception {
        String accessToken = null;
        String apiUrl = null;
        CtlUtils ctlUtils = null;

        ArgumentParser parser = ArgumentParsers
            .newFor("rctl")
            .build()
            .defaultHelp(true)
            .version("rctl, version " + System.getenv("ROUTR_CTL_VERSION"))
            .description("A tool for the managment of a Routr instance")
            .epilog("Run 'rctl COMMAND --help' for more information on a command");

        parser.addArgument("-v", "--version").action(Arguments.version()).help("print version information and quit");

        Subparsers subparsers = parser.addSubparsers().title("Commands").metavar("COMMAND");

        if (args.length > 0 && !Main.bypassToken(args)) {

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
        CmdProxy cmdProxy = new CmdProxy(subparsers);
        CmdLogin cmdLogin = new CmdLogin(subparsers);
        CmdLogout cmdLogout = new CmdLogout(subparsers);
        CmdLogs cmdLogs = new CmdLogs(subparsers, ctlUtils);
        CmdRestart cmdRestart = new CmdRestart(subparsers, ctlUtils);
        CmdStop cmdStop = new CmdStop(subparsers, ctlUtils);
        CmdPing cmdPing = new CmdPing(subparsers, ctlUtils);
        CmdVersion cmdVersion = new CmdVersion(subparsers, ctlUtils);
        CmdConfig cmdConfig = new CmdConfig(subparsers, ctlUtils);

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
                case "restart":
                    cmdRestart.run(res.get("now"));
                    break;
                case "stop":
                    cmdStop.run(res.get("now"));
                    break;
                case "ping":
                    cmdPing.run();
                    break;
                case "login":
                    cmdLogin.run(res.get("apiUrl"), res.get("u"), res.get("p"));
                    break;
                case "logout":
                    cmdLogout.run();
                    break;
                case "ver":
                case "version":
                    cmdVersion.run();
                    break;
                case "proxy":
                    cmdProxy.run(apiUrl, accessToken, res.get("port"));
                    break;
                case "logs":
                    cmdLogs.run();
                    break;
                case "config":
                    cmdConfig.run(res.get("subcommand"),
                      res.get("file"),
                        res.get("full"));
                default:
                    // This is not possible;
            }
        } catch(ArgumentParserException ex) {
            parser.handleError(ex);
        } catch (UnirestException ex) {
            out.println("Routr server is not running");
            System.exit(0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
