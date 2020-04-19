package io.routr.ctl;

import java.io.File;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

import java.io.IOException;

import static java.lang.System.exit;
import static java.lang.System.out;

class CmdLogout {

    CmdLogout(Subparsers subparsers) {
        subparsers.addParser("logout").help("clear session credentials");
    }

    void run()  {
        File file = new File(Main.CONFIG_PATH);
        if (file.exists()) {
            file.delete();
        }
        out.println("Done");
    }
}
