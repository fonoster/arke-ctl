package io.routr.ctl;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;
import java.util.Scanner;
import java.nio.file.NoSuchFileException;
import static java.lang.System.out;

class CmdConfig {

    private static CtlUtils ctlUtils;

    CmdConfig(Subparsers subparsers, CtlUtils ctlUtils) {
        Subparser config = subparsers.addParser("config").help("manage routr configuration");
        config.addArgument("subcommand").metavar("subcommand").choices("apply", "describe")
          .help("config subcommands: apply, describe");
        config.addArgument("-f", "--file").metavar("file").help("path to yaml file with the configuration");

        config.epilog(String.join(
            System.getProperty("line.separator"),
            "`Examples:",
            "  # Update configuration from a file",
            "  $ rctl config apply -f config.yaml\n",
            "  # Shows current merged configuration",
            "  $ rctl config describe\n"
        ));
        this.ctlUtils = ctlUtils;
    }

    void run(String subcommand, String path) {
        if (subcommand.equals("apply")) {
            String data = "";

            if (path !=null && !path.isEmpty()) {
                try {
                    data = new FileUtils().getJsonString(path);
                } catch(Exception ex) {
                    if (ex instanceof NoSuchFileException) {
                        out.println("Please ensure file '" + ex.getMessage()
                          + "' exist and has proper permissions");
                    } else if (ex instanceof NullPointerException) {
                        out.println("You must indicate a file");
                    } else {
                        out.println("Unexpected Exception: "
                            + ex.getMessage());
                    }
                    System.exit(1);
                }
            } else {
               Scanner sc = new Scanner(System.in);
               while(sc.hasNextLine()) {
                   data += sc.nextLine();
               }
            }

            new CmdConfigApply(this.ctlUtils).run(data);
        } else {
            new CmdConfigDescribe(this.ctlUtils).run();
        }
    }
}
